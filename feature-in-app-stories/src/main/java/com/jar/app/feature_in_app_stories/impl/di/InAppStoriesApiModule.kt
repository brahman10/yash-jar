package com.jar.app.feature_in_app_stories.impl.di

import android.content.Context
import com.jar.app.feature_in_app_stories.api.InAppStoriesApi
import com.jar.app.feature_in_app_stories.impl.InAppStoriesApiImpl
import com.jar.app.feature_in_app_stories.impl.uitl.WatermarkUtil
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ActivityComponent::class)
internal abstract class InAppStoriesApiModule {
    @Binds
    @ActivityScoped
    internal abstract fun provideSpendTrackerApi(inAppStoriesApi: InAppStoriesApiImpl): InAppStoriesApi

}

@Module
@InstallIn(SingletonComponent::class)
object WatermarkModule {

    @Provides
    fun provideWatermarkUtil(context: Context): WatermarkUtil {
        return WatermarkUtil(context)
    }
}