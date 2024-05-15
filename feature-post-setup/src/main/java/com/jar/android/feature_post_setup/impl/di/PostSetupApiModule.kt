package com.jar.android.feature_post_setup.impl.di

import com.jar.android.feature_post_setup.api.PostSetupApi
import com.jar.android.feature_post_setup.api.PostSetupApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class PostSetupApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun providePostSetupApi(postSetupApiImpl: PostSetupApiImpl): PostSetupApi
}