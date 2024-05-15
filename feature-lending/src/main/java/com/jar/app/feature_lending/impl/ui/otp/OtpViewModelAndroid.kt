package com.jar.app.feature_lending.impl.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_utils.data.NetworkUtil
import com.jar.app.feature_lending.shared.domain.use_case.RequestLendingOtpUseCase
import com.jar.app.feature_lending.shared.domain.use_case.VerifyLendingOtpUseCase
import com.jar.app.feature_lending.shared.ui.otp.OtpViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OtpViewModelAndroid @Inject constructor(
    private val requestLendingOtpUseCase: RequestLendingOtpUseCase,
    private val verifyLendingOtpUseCase: VerifyLendingOtpUseCase,
    private val networkUtil: NetworkUtil
) : ViewModel() {


    private val viewModel by lazy {
        OtpViewModel(
            requestLendingOtpUseCase = requestLendingOtpUseCase,
            verifyLendingOtpUseCase = verifyLendingOtpUseCase,
            localIPAddress = networkUtil.getLocalIpAddress(),
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}