package com.jar.app.feature_payment.impl.ui.cancel_transction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_one_time_payments.shared.domain.use_case.CancelPaymentUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CancelTransactionDialogViewModel @Inject constructor(
    private val cancelPaymentUseCase: CancelPaymentUseCase
) : ViewModel() {

    private val _cancelPaymentLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val cancelPaymentLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _cancelPaymentLiveData

    fun cancelPayment(orderId: String) {
        viewModelScope.launch {
            cancelPaymentUseCase.cancelPayment(orderId).collect {
                _cancelPaymentLiveData.postValue(it)
            }
        }
    }

}