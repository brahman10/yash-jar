package com.jar.feature_quests

import com.jar.feature_quests.api.QuestsApi
import com.jar.feature_quests.impl.data.QuestsApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
@Module
@InstallIn(ActivityComponent::class)
internal abstract class QuestsModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideQuestsApi(questsApi: QuestsApiImpl): QuestsApi
}