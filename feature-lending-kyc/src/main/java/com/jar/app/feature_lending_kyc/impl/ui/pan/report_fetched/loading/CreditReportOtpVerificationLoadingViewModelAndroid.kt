package com.jar.app.feature_lending_kyc.impl.ui.pan.report_fetched.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpUseCase
import com.jar.app.feature_lending_kyc.shared.ui.pan.report_fetched.loading.CreditReportOtpVerificationLoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CreditReportOtpVerificationLoadingViewModelAndroid @Inject constructor(
    private val requestCreditReportOtpUseCase: RequestCreditReportOtpUseCase
) : ViewModel() {
    private val viewModel by lazy {
        CreditReportOtpVerificationLoadingViewModel(
            requestCreditReportOtpUseCase = requestCreditReportOtpUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}