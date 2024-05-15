package com.jar.android.feature_post_setup.impl.ui.status.success

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentSuccessViewModel @Inject constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase
) : ViewModel() {

    var fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse? = null

    private val _fetchManualPaymentResponseLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>()
    val fetchManualPaymentResponseLiveData: LiveData<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>
        get() = _fetchManualPaymentResponseLiveData

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
}