package com.jar.app.feature_mandate_payment.impl.ui.verify_status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.app.feature_mandate_payments_common.shared.ui.verify_mandate_status.VerifyMandatePaymentStatusFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class VerifyMandatePaymentStatusFragmentViewModelAndroid @Inject constructor(
    private val fetchMandatePaymentStatusUseCase: FetchMandatePaymentStatusUseCase
) : ViewModel() {

    private val viewModel by lazy {
        VerifyMandatePaymentStatusFragmentViewModel(
            fetchMandatePaymentStatusUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}