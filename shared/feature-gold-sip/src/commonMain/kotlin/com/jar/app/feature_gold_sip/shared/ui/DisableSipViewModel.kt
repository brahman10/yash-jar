package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.feature_gold_sip.shared.domain.use_case.DisableGoldSipUseCase
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class DisableSipViewModel constructor(
    private val disableGoldSipUseCase: DisableGoldSipUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _disableGoldSipFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>()
    val disableGoldSipFlow: CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>
        get() = _disableGoldSipFlow.toCommonFlow()

    fun disableSip() {
        viewModelScope.launch {
            disableGoldSipUseCase.disableGoldSip().collect {
                _disableGoldSipFlow.emit(it)
            }
        }
    }

    fun fireSipBottomSheetEvent(eventName: String, action: String) {
        analyticsApi.postEvent(
            eventName,
            mapOf(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to action
            )
        )
    }
}