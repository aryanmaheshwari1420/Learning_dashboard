package com.example.learning_dashboard.data.repository

import android.util.Log
import com.example.learning_dashboard.data.local.dao.CourseDao
import com.example.learning_dashboard.data.local.dao.LessonDao
import com.example.learning_dashboard.data.local.entity.CourseEntity
import com.example.learning_dashboard.data.local.entity.LessonEntity
import com.example.learning_dashboard.data.remote.datasource.RemoteDataSource
import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.domain.model.Lesson
import com.example.learning_dashboard.domain.repository.CourseRepository
import com.example.learning_dashboard.domain.util.ProgressCalculator
import com.example.learning_dashboard.domain.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val courseDao: CourseDao,
    private val lessonDao: LessonDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CourseRepository {

    companion object {
        private const val TAG = "CourseRepository"
    }

    override fun getCourses(): Flow<Resource<List<Course>>> = flow {
        emit(Resource.Loading)

        // 1. Check local Room cache first
        val cachedEntities = courseDao.getAllCourses()
        if (cachedEntities.isNotEmpty()) {
            val cachedCourses = mapEntitiesToDomainCourses(cachedEntities)
            emit(Resource.Success(cachedCourses))
            Log.d(TAG, "Loaded courses from cache")
        }

        // 2. Try fetching from remote
        try {
            syncRemoteData()
            // 3. Emit updated Room data after successful sync
            val updatedEntities = courseDao.getAllCourses()
            val updatedCourses = mapEntitiesToDomainCourses(updatedEntities)
            emit(Resource.Success(updatedCourses))
        } catch (e: Exception) {
            Log.e(TAG, "Remote fetch failed", e)
            val localEntities = courseDao.getAllCourses()
            if (localEntities.isNotEmpty()) {
                Log.d(TAG, "Loaded courses from cache (fallback)")
                val domainCourses = mapEntitiesToDomainCourses(localEntities)
                emit(Resource.Success(domainCourses))
            } else {
                emit(Resource.Error("Unable to load courses. Please check your internet connection.", e))
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun refreshCourses(): Result<Unit> = withContext(ioDispatcher) {
        try {
            syncRemoteData()
            Result.success(Unit)
        } catch (e: Exception) {
            val localCount = courseDao.getAllCourses().size
            if (localCount > 0) {
                Log.d(TAG, "Loaded courses from cache during refresh fallback")
                Result.success(Unit)
            } else {
                Result.failure(e)
            }
        }
    }

    override fun getCourseDetails(courseId: Long): Flow<Resource<Pair<Course, List<Lesson>>>> = flow {
        emit(Resource.Loading)

        // Ensure lessons are populated for this course if missing
        val localLessons = lessonDao.getLessonsForCourse(courseId)
        if (localLessons.isEmpty()) {
            try {
                val remoteLessons = remoteDataSource.getLessons(courseId)
                val lessonEntities = remoteLessons.map {
                    LessonEntity(
                        id = it.id,
                        courseId = it.courseId,
                        title = it.title,
                        isCompleted = it.isCompleted,
                        order = it.order
                    )
                }
                syncLessonsPreservingCompletion(lessonEntities)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to fetch lessons remotely: ${e.message}")
            }
        }

        // Combine reactive Room flows for course & lessons
        val courseFlow = courseDao.getCourseByIdFlow(courseId)
        val lessonsFlow = lessonDao.getLessonsForCourseFlow(courseId)

        courseFlow.combine(lessonsFlow) { courseEntity, lessonEntities ->
            if (courseEntity == null) {
                Resource.Error("Course not found")
            } else {
                val lessons = lessonEntities.map { it.toDomainModel() }
                val completedCount = lessons.count { it.isCompleted }
                val totalCount = if (lessons.isNotEmpty()) lessons.size else courseEntity.totalLessons
                val calculatedProgress = ProgressCalculator.calculateProgress(completedCount, totalCount)

                val domainCourse = courseEntity.toDomainModel(completedCount).copy(
                    progress = calculatedProgress,
                    totalLessons = totalCount
                )
                Resource.Success(Pair(domainCourse, lessons))
            }
        }.collect { combinedResource ->
            emit(combinedResource)
        }
    }.flowOn(ioDispatcher)

    override suspend fun markLessonCompleted(courseId: Long, lessonId: Long): Result<Unit> {
        return toggleLessonCompletion(courseId, lessonId, true)
    }

    override suspend fun toggleLessonCompletion(
        courseId: Long,
        lessonId: Long,
        completed: Boolean
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            // Update lesson completion state in Room
            lessonDao.updateLessonCompletion(lessonId, completed)

            // Recalculate dynamic progress
            val totalLessons = lessonDao.getTotalLessonsCount(courseId)
            val completedLessons = lessonDao.getCompletedLessonsCount(courseId)
            val newProgress = ProgressCalculator.calculateProgress(
                completedLessons = completedLessons,
                totalLessons = totalLessons
            )

            // Persist recalculated progress in course entity
            courseDao.updateCourseProgress(courseId, newProgress)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun setSimulateNetworkError(simulate: Boolean) {
        remoteDataSource.setSimulateNetworkError(simulate)
    }

    override fun isSimulatingNetworkError(): Boolean {
        return remoteDataSource.isSimulatingNetworkError()
    }

    private suspend fun syncLessonsPreservingCompletion(remoteLessons: List<LessonEntity>) {
        val existingLessons = lessonDao.getAllLessons().associateBy { it.id }
        val merged = remoteLessons.map { remote ->
            val local = existingLessons[remote.id]
            if (local != null) {
                remote.copy(isCompleted = local.isCompleted)
            } else {
                remote
            }
        }
        lessonDao.insertLessons(merged)
    }

    private suspend fun syncRemoteData() {
        val remoteCourses = remoteDataSource.getCourses()
        val remoteLessons = remoteDataSource.getAllLessons()

        // 1. Insert courses FIRST so courses table is populated
        val initialCourseEntities = remoteCourses.map { courseDto ->
            CourseEntity(
                id = courseDto.id,
                title = courseDto.title,
                instructor = courseDto.instructor,
                progress = courseDto.progress,
                totalLessons = courseDto.lessons
            )
        }
        courseDao.insertCourses(initialCourseEntities)

        // 2. Sync lessons preserving user-marked completion status
        val lessonEntities = remoteLessons.map {
            LessonEntity(
                id = it.id,
                courseId = it.courseId,
                title = it.title,
                isCompleted = it.isCompleted,
                order = it.order
            )
        }
        syncLessonsPreservingCompletion(lessonEntities)

        // 3. Recalculate dynamic progress from lessons
        val updatedCourseEntities = initialCourseEntities.map { course ->
            val total = lessonDao.getTotalLessonsCount(course.id).takeIf { it > 0 } ?: course.totalLessons
            val completed = lessonDao.getCompletedLessonsCount(course.id)
            val calculatedProgress = if (total > 0 && completed > 0) {
                ProgressCalculator.calculateProgress(completed, total)
            } else {
                course.progress
            }
            course.copy(progress = calculatedProgress, totalLessons = total)
        }
        courseDao.insertCourses(updatedCourseEntities)
    }


    private suspend fun mapEntitiesToDomainCourses(entities: List<CourseEntity>): List<Course> {
        return entities.map { entity ->
            val completed = lessonDao.getCompletedLessonsCount(entity.id)
            val total = entity.totalLessons
            val calculatedProgress = if (total > 0) {
                ProgressCalculator.calculateProgress(completed, total)
            } else {
                entity.progress
            }
            entity.toDomainModel(completed).copy(progress = calculatedProgress)
        }
    }
}
