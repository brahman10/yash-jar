package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipIntroUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class GoldSipIntroViewModel constructor(
    private val fetchGoldSipIntroUseCase: FetchGoldSipIntroUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldSipIntroFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipIntroData>>>()
    val goldSipIntroFlow: CFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipIntroData>>>
        get() = _goldSipIntroFlow.toCommonFlow()

    fun fetchGoldSipIntro() {
        viewModelScope.launch {
            fetchGoldSipIntroUseCase.fetchGoldSipIntro().collect {
                _goldSipIntroFlow.emit(it)
            }
        }
    }

    fun fireSipIntroEvent(eventName: String, action: String) {
        analyticsApi.postEvent(
            eventName,
            mapOf(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to action
            )
        )
    }
}