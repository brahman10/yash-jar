package com.jar.app.feature_gifting.impl.ui.gifting_success

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class GoldGiftStatusViewModel @Inject constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase
) : ViewModel() {

    private val _fetchManualPaymentResponseLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>()
    val fetchManualPaymentResponseLiveData: LiveData<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>
        get() = _fetchManualPaymentResponseLiveData

    fun fetchManualPaymentStatus(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        viewModelScope.launch {
            fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                FetchManualPaymentRequest(
                    orderId = fetchManualPaymentStatusResponse.transactionId!!,
                    paymentProvider = fetchManualPaymentStatusResponse.paymentProvider!!
                )
            ).collectLatest {
                _fetchManualPaymentResponseLiveData.postValue(it)
            }
        }
    }
}