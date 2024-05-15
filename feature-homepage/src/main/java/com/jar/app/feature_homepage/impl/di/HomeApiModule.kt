package com.jar.app.feature_homepage.impl.di

import com.jar.app.feature_homepage.api.data.HomePageApi
import com.jar.app.feature_homepage.impl.HomePageApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class HomeApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideHomePageApi(homePageApiImpl: HomePageApiImpl): HomePageApi

}