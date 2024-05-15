package com.jar.app.core_logger.shared.di

import com.jar.app.core_logger.shared.LoggerApi
import com.jar.app.core_logger.shared.LoggerApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class LoggerModule {

    @Provides
    @Singleton
    fun provideLoggerApi(): LoggerApi {
        return LoggerApiImpl(shouldEnableLogs = true)
    }

}