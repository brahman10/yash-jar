package com.jar.app.base.di.module

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WorkerModule {

    @Provides
    @Singleton
    fun provideWorkManger(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}