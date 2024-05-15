package com.jar.app.feature_gold_sip.shared.ui

import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.app.feature_user_api.domain.mappers.toUserGoldSipDetails
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SetupGoldSipViewModel constructor(
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldSipDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>(RestClientResult.none())
    val goldSipDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>
        get() = _goldSipDetailsFlow.toCommonStateFlow()

    fun fetchGoldSipDetails() {
        viewModelScope.launch {
            fetchGoldSipDetailsUseCase.fetchGoldSipDetails()
                .mapToDTO {
                    it?.toUserGoldSipDetails()
                }
                .collect {
                    _goldSipDetailsFlow.emit(it)
                }
        }
    }

    fun fireSetupSipEvent(
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