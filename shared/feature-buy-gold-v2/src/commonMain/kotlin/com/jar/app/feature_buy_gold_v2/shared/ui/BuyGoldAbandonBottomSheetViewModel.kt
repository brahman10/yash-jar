package com.jar.app.feature_buy_gold_v2.shared.ui

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldAbandonResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchBuyGoldAbandonDataUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class BuyGoldAbandonBottomSheetViewModel constructor(
    private val fetchBuyGoldAbandonDataUseCase: FetchBuyGoldAbandonDataUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _abandonStepsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<BuyGoldAbandonResponse>>>()
    val abandonStepsFlow: CFlow<RestClientResult<ApiResponseWrapper<BuyGoldAbandonResponse>>>
        get() = _abandonStepsFlow.toCommonFlow()

    fun fetchAbandonData() {
        viewModelScope.launch {
            fetchBuyGoldAbandonDataUseCase.fetchBuyGoldAbandonInfo(BaseConstants.StaticContentType.BUY_GOLD_ONBOARDING)
                .collect {
                    _abandonStepsFlow.emit(it)
                }
        }
    }


    fun sendAnalyticsEvent(
        eventName: String,
        eventParamsMap: Map<String, Any>?
    ) {
        eventParamsMap?.let {
            analyticsApi.postEvent(eventName, it)
        } ?: kotlin.run {
            analyticsApi.postEvent(eventName)
        }
    }

}