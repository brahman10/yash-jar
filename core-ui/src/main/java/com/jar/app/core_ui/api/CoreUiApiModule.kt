package com.jar.app.core_ui.api

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class CoreUiApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideCoreUiApi(coreUiApiImpl: CoreUiApiImpl): CoreUiApi
}