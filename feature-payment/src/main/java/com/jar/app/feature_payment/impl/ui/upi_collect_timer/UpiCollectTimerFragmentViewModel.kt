package com.jar.app.feature_payment.impl.ui.upi_collect_timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UpiCollectTimerFragmentViewModel @Inject constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase
) : ViewModel() {

    private val _fetchManualPaymentStatusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>()
    val fetchManualPaymentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>
        get() = _fetchManualPaymentStatusLiveData

    fun fetchManualPaymentStatus(oneTimePaymentResult: OneTimePaymentResult) {
        viewModelScope.launch {
            val provider = oneTimePaymentResult.oneTimePaymentGateway
            val fetchManualPaymentRequest = FetchManualPaymentRequest(
                paymentProvider = provider.name,
                orderId = oneTimePaymentResult.orderId,
                juspay = oneTimePaymentResult.juspayPaymentResponse
            )
            fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(fetchManualPaymentRequest)
                .collect {
                    _fetchManualPaymentStatusLiveData.postValue(it)
                }
        }
    }
}