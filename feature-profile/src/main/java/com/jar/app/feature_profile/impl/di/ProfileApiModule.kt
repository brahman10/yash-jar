package com.jar.app.feature_profile.impl.di

import com.jar.app.feature_profile.api.ProfileApi
import com.jar.app.feature_profile.api.ProfileApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class ProfileApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideProfileApi(profileApiImpl: ProfileApiImpl): ProfileApi

}