package com.jar.app.feature.transaction.ui.transaction_breakup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_transaction.shared.domain.use_case.FetchPaymentTransactionBreakupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PaymentTransactionBreakupFragmentViewModel @Inject constructor(
    private val fetchPaymentTransactionBreakupUseCase: FetchPaymentTransactionBreakupUseCase
) : ViewModel() {

    private val _paymentTransactionBreakupLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.PaymentTransactionBreakup>>>()
    val paymentTransactionBreakupLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.PaymentTransactionBreakup>>>
        get() = _paymentTransactionBreakupLiveData

    fun fetchPaymentTransactionBreakup(orderId: String?, type: String?) {
        viewModelScope.launch {
            fetchPaymentTransactionBreakupUseCase.fetchPaymentTransactionBreakup(orderId, type)
                .collect {
                    _paymentTransactionBreakupLiveData.postValue(it)
                }
        }
    }
}