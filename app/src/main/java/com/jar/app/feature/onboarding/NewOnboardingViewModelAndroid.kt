package com.jar.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSupportedLanguagesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.IGetPhoneByDeviceUseCase
import com.jar.app.feature_onboarding.shared.ui.NewOnboardingViewModel
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class NewOnboardingViewModelAndroid @Inject constructor(
    updateUserUseCase: UpdateUserUseCase,
    getPhoneByDeviceUseCase: IGetPhoneByDeviceUseCase,
    fetchSupportedLanguagesUseCase: FetchSupportedLanguagesUseCase,
    weeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase,
    prefs: PrefsApi,
    serializer: Serializer,
    deviceUtils: DeviceUtils,
    analyticsHandler: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        NewOnboardingViewModel(
            updateUserUseCase,
            getPhoneByDeviceUseCase,
            fetchSupportedLanguagesUseCase,
            weeklyChallengeMetaDataUseCase,
            prefs,
            serializer,
            deviceUtils,
            analyticsHandler,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}