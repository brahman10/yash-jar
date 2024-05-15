package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GoldSipAutoPayPendingOrFailureViewModel constructor(
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val fetchMandatePaymentStatusUseCase: FetchMandatePaymentStatusUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _mandateStatusFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FetchMandatePaymentStatusResponse?>>>(RestClientResult.none())
    val mandateStatusFlow: CFlow<RestClientResult<ApiResponseWrapper<FetchMandatePaymentStatusResponse?>>>
        get() = _mandateStatusFlow.toCommonFlow()

    private val _updateGoldSipDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>()
    val updateGoldSipDetailsFlow:
            CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>
        get() = _updateGoldSipDetailsFlow.toCommonFlow()


    fun fetchAutoInvestStatus(mandatePaymentResultFromSDK: com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK) {
        viewModelScope.launch {
            fetchMandatePaymentStatusUseCase.fetchMandatePaymentStatus(mandatePaymentResultFromSDK)
                .collect {
                    _mandateStatusFlow.emit(it)
                }
        }
    }

    fun updateGoldSip(updateSipDetails: com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails) {
        viewModelScope.launch {
            updateGoldSipDetailsUseCase.updateGoldSipDetails(updateSipDetails).collect {
                _updateGoldSipDetailsFlow.emit(it)
            }
        }
    }


    fun fireSipAutoPayPendingOrFailureEvent(
        eventName: String,
        eventParamsMap: Map<String, Any>? = null
    ) {
        eventParamsMap?.let {
            analyticsApi.postEvent(eventName, it)
        } ?: kotlin.run {
            analyticsApi.postEvent(eventName)
        }
    }
}
