package com.jar.app.feature_lending_kyc.impl.ui.pan.base_pan_verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpUseCase
import com.jar.app.feature_lending_kyc.shared.ui.pan.base_pan_verification.PanVerificationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PanVerificationViewModelAndroid @Inject constructor(
    private val requestCreditReportOtpUseCase: RequestCreditReportOtpUseCase
) : ViewModel() {

    private val viewModel by lazy {
        PanVerificationViewModel(
            requestCreditReportOtpUseCase = requestCreditReportOtpUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}