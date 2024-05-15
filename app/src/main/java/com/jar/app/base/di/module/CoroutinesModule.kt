package com.jar.app.base.di.module

import com.jar.app.base.util.AppDispatcherProvider
import com.jar.app.base.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoroutinesModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return AppDispatcherProvider()
    }
}