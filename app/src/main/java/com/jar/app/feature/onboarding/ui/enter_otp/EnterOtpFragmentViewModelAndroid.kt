package com.jar.app.feature.onboarding.ui.enter_otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOTPStatusUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.OtpLoginUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.RequestOtpUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.TruecallerLoginUseCase
import com.jar.app.feature_onboarding.shared.ui.enter_otp.EnterOtpFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EnterOtpFragmentViewModelAndroid @Inject constructor(
    deviceUtils: DeviceUtils,
    otpLoginUseCase: OtpLoginUseCase,
    requestOtpUseCase: RequestOtpUseCase,
    fetchOTPStatusUseCase: FetchOTPStatusUseCase,
    truecallerLoginUseCase: TruecallerLoginUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        EnterOtpFragmentViewModel(
            deviceUtils,
            otpLoginUseCase,
            requestOtpUseCase,
            fetchOTPStatusUseCase,
            truecallerLoginUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}