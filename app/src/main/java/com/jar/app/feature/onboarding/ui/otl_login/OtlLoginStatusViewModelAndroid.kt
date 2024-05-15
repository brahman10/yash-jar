package com.jar.app.feature.onboarding.ui.otl_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOtlUserInfoUseCase
import com.jar.app.feature_onboarding.shared.ui.otl_login.OtlLoginStatusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OtlLoginStatusViewModelAndroid @Inject constructor(
    fetchOtlUserInfoUseCase: FetchOtlUserInfoUseCase,
    deviceUtils: DeviceUtils,
) : ViewModel() {

    private val viewModel by lazy {
        OtlLoginStatusViewModel(
            fetchOtlUserInfoUseCase,
            deviceUtils,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}