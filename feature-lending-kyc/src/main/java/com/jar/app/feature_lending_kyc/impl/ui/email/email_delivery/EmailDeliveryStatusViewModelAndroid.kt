package com.jar.app.feature_lending_kyc.impl.ui.email.email_delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestEmailOtpUseCase
import com.jar.app.feature_lending_kyc.shared.ui.email.email_delivery.EmailDeliveryStatusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EmailDeliveryStatusViewModelAndroid @Inject constructor(
    private val requestEmailOtpUseCase: RequestEmailOtpUseCase
) : ViewModel() {

    private val viewModel by lazy {
        EmailDeliveryStatusViewModel(
            requestEmailOtpUseCase = requestEmailOtpUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}