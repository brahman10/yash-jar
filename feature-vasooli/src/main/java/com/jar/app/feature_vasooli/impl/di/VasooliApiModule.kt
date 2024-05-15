package com.jar.app.feature_vasooli.impl.di

import com.jar.app.feature_vasooli.api.VasooliApi
import com.jar.app.feature_vasooli.impl.data.VasooliApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class VasooliApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideKycApi(vasooliApiImpl: VasooliApiImpl): VasooliApi

}