package com.jar.app.feature_lending.impl.ui.repayments.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchRepaymentDetailsUseCase
import com.jar.app.feature_lending.shared.ui.repayments.payment.PaymentDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PaymentDetailViewModelAndroid @Inject constructor(
    private val fetchRepaymentDetailsUseCase: FetchRepaymentDetailsUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        PaymentDetailViewModel(
            fetchRepaymentDetailsUseCase = fetchRepaymentDetailsUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}