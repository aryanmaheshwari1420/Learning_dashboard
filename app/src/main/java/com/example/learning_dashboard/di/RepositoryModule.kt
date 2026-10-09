package com.example.learning_dashboard.di

import com.example.learning_dashboard.data.repository.AuthRepositoryImpl
import com.example.learning_dashboard.data.repository.CourseRepositoryImpl
import com.example.learning_dashboard.domain.repository.AuthRepository
import com.example.learning_dashboard.domain.repository.CourseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        courseRepositoryImpl: CourseRepositoryImpl
    ): CourseRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
