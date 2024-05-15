package com.jar.android.feature_post_setup.impl.ui.status.failure_or_pending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_post_setup.domain.use_case.InitiateFailedPaymentsUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PaymentPendingOrFailureViewModel @Inject constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase,
    private val initiateFailedPaymentsUseCase: InitiateFailedPaymentsUseCase,
) : ViewModel() {

    private val _fetchManualPaymentResponseLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>()
    val fetchManualPaymentResponseLiveData: LiveData<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>
        get() = _fetchManualPaymentResponseLiveData

    private val _failedPaymentLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>()
    val failedPaymentLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>
        get() = _failedPaymentLiveData

    fun fetchManualPaymentStatus(transactionId: String, paymentProvider: String) {
        viewModelScope.launch {
            fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                FetchManualPaymentRequest(
                    orderId = transactionId,
                    paymentProvider = paymentProvider
                )
            ).collect {
                _fetchManualPaymentResponseLiveData.postValue(it)
            }
        }
    }

    fun initiateFailedPayment(amount: Float, paymentProvider: String, roundOffIds: List<String>) {
        viewModelScope.launch {
            initiateFailedPaymentsUseCase.initiatePaymentForFailedTransactions(
                amount = amount,
                paymentProvider = paymentProvider,
                type = SavingsType.DAILY_SAVINGS.name,
                roundOffsLinked = roundOffIds
            ).collect {
                _failedPaymentLiveData.postValue(it)
            }
        }
    }
}