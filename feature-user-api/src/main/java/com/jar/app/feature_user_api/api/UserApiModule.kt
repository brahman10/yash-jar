package com.jar.app.feature_user_api.api

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class UserApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideUserApi(userApiImpl: UserApiImpl): UserApi

}