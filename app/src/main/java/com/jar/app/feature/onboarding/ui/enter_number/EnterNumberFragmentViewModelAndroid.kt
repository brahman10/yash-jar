package com.jar.app.feature.onboarding.ui.enter_number

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOtlUserInfoUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSupportedLanguagesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.TruecallerLoginUseCase
import com.jar.app.feature_onboarding.shared.ui.enter_number.EnterNumberFragmentViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EnterNumberFragmentViewModelAndroid @Inject constructor(
    deviceUtils: DeviceUtils,
    truecallerLoginUseCase: TruecallerLoginUseCase,
    fetchSupportedLanguagesUseCase: FetchSupportedLanguagesUseCase,
    fetchOtlUserInfoUseCase: FetchOtlUserInfoUseCase,
    analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        EnterNumberFragmentViewModel(
            deviceUtils,
            truecallerLoginUseCase,
            fetchSupportedLanguagesUseCase,
            fetchOtlUserInfoUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}