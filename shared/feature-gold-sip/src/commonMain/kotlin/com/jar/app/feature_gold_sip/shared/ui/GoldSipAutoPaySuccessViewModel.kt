package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GoldSipAutoPaySuccessViewModel constructor(
    private val fetchGoldSipTypeSetupInfoUseCase: FetchGoldSipTypeSetupInfoUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _fetchSetupGoldSipFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipSetupInfo>>>(
            RestClientResult.none())
    val fetchSetupGoldSipFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipSetupInfo>>>
        get() = _fetchSetupGoldSipFlow.toCommonStateFlow()

    fun fetchSetupGoldSipData(subscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType) {
        viewModelScope.launch {
            fetchGoldSipTypeSetupInfoUseCase.fetchGoldSipTypeSetupInfo(subscriptionType.name)
                .collect {
                    _fetchSetupGoldSipFlow.emit(it)
                }
        }
    }

    fun fireSipAutoPaySuccessEvent(
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