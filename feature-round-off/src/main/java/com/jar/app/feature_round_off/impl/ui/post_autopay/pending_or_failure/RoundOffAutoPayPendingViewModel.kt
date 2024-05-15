package com.jar.app.feature_round_off.impl.ui.post_autopay.pending_or_failure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RoundOffAutoPayPendingViewModel @Inject constructor(
    private val fetchMandatePaymentStatusUseCase: FetchMandatePaymentStatusUseCase
) : ViewModel() {

    private val _statusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse?>>>()
    val statusLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse?>>>
        get() = _statusLiveData

    fun fetchAutoInvestStatus(mandatePaymentResultFromSDK: com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK) {
        viewModelScope.launch {
            fetchMandatePaymentStatusUseCase.fetchMandatePaymentStatus(mandatePaymentResultFromSDK).collect {
                _statusLiveData.postValue(it)
            }
        }
    }
}