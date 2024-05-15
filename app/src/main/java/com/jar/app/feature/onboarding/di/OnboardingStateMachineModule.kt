package com.jar.app.feature.onboarding.di

import com.jar.app.BuildConfig
import com.jar.app.core_logger.shared.LoggerApi
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal class OnboardingStateMachineModule {

    @Provides
    @ActivityScoped
    fun provideOnboardingStateMachine(
        prefsApi: PrefsApi,
        serializer: Serializer,
        remoteConfigApi: RemoteConfigApi
    ): OnboardingStateMachine {
        return OnboardingStateMachine(
            prefsApi,
            serializer,
            remoteConfigApi
        )
    }
}