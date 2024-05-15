package com.jar.app.feature_payment.impl.ui.transaction_failed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.RetryPaymentRequest
import com.jar.app.feature_one_time_payments.shared.domain.use_case.RetryPaymentUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TransactionFailedFragmentViewModel @Inject constructor(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val retryPaymentUseCase: RetryPaymentUseCase
) : ViewModel() {

    private val _retryPaymentLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>()
    val retryPaymentLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>
        get() = _retryPaymentLiveData

    fun retryPayment(orderId: String, amount: Float) {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collect(
                onLoading = {
                    _retryPaymentLiveData.postValue(RestClientResult.loading())
                },
                onSuccess = {
                    val request = RetryPaymentRequest(
                        orderId = orderId,
                        amount = amount,
                        paymentProvider = com.jar.app.core_base.domain.model.OneTimePaymentGateway.JUSPAY.name,
                        priceResponse = it
                    )
                    retryPaymentUseCase.retryPayment(request).collect {
                        _retryPaymentLiveData.postValue(it)
                    }
                },
                onError = { errorMessage, errorCode ->
                    _retryPaymentLiveData.postValue(RestClientResult.error(errorMessage))
                }
            )
        }
    }
}
