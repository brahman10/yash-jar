package com.jar.app.feature_mandate_payments_common.shared.ui.verify_mandate_status

import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VerifyMandatePaymentStatusFragmentViewModel constructor(
    private val fetchMandatePaymentStatusUseCase: FetchMandatePaymentStatusUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _mandatePaymentStatusLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<FetchMandatePaymentStatusResponse?>>>()
    val mandatePaymentStatusLiveData: CFlow<RestClientResult<ApiResponseWrapper<FetchMandatePaymentStatusResponse?>>>
        get() = _mandatePaymentStatusLiveData.toCommonFlow()

    fun fetchMandatePaymentStatus(mandatePaymentResultFromSDK: MandatePaymentResultFromSDK) {
        viewModelScope.launch {
            fetchMandatePaymentStatusUseCase.fetchMandatePaymentStatus(mandatePaymentResultFromSDK)
                .collectLatest {
                    _mandatePaymentStatusLiveData.emit(it)
                }
        }
    }
}