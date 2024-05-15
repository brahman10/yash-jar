package com.jar.app.feature_lending.impl.ui.repayments.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchTransactionDetailsUseCase
import com.jar.app.feature_lending.shared.ui.repayments.payment.RepaymentTxnDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RepaymentTxnDetailViewModelAndroid @Inject constructor(
    private val fetchTransactionDetailsUseCase: FetchTransactionDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        RepaymentTxnDetailViewModel(
            fetchTransactionDetailsUseCase = fetchTransactionDetailsUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}