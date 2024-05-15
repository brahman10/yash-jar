package com.jar.app.feature_payment.impl.ui.verify_status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments.shared.ui.VerifyPaymentStatusFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class VerifyPaymentStatusFragmentViewModelAndroid @Inject constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase
) : ViewModel() {

    private val viewModel by lazy {
        VerifyPaymentStatusFragmentViewModel(
            fetchManualPaymentStatusUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}