package com.jar.feature_gold_price_alerts.shared.ui

import com.jar.app.core_base.util.orZero
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrend
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendScreenStaticData
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendScreenStaticUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendUseCase
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.GoldPrice_HomeScreenClicked
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.alertstatus
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.cardFlowType
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.carddescription
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.cardtitle
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.clickaction
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.graphclicked
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.messageshown
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.pricedurationclicked
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.timespent
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class GoldPriceDetailFragmentViewModel constructor(
    private val fetchGoldTrendUseCase: FetchGoldTrendUseCase,
    private val fetchGoldPriceTrendScreenStaticUseCase: FetchGoldPriceTrendScreenStaticUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldTrendLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldTrend>>>(RestClientResult.none())
    val goldTrendLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<GoldTrend>>>
        get() = _goldTrendLiveData.toCommonStateFlow()

    private var job: Job? = null

    private val _staticScreenResponse =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldTrendScreenStaticData?>>>(
            RestClientResult.none()
        )
    val staticScreenResponse: CStateFlow<RestClientResult<ApiResponseWrapper<GoldTrendScreenStaticData?>>>
        get() = _staticScreenResponse.toCommonStateFlow()

    val initiatedTime: Long = Clock.System.now().toEpochMilliseconds()

    fun fetchStaticScreenData() {
        viewModelScope.launch {
            fetchGoldPriceTrendScreenStaticUseCase.fetchGoldPriceTrendScreenStatic().collect {
                _staticScreenResponse.emit(it)
            }
        }
    }

    fun fetchGoldPriceTrend() {
        fetchGoldPriceTrend("Y", 5)
    }

    fun fetchGoldPriceTrend(unit: String, period: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            fetchGoldTrendUseCase.fetchGoldPriceTrend(unit, period).collect {
                _goldTrendLiveData.emit(it)
            }
        }
    }

    fun buildMapForAnalytics(): MutableMap<String, Any> {
        val data = _staticScreenResponse.value.data?.data
        return mutableMapOf<String, Any>(
            cardtitle to data?.savingsCard?.title.orEmpty(),
            carddescription to data?.savingsCard?.description.orEmpty(),
            alertstatus to data?.activeAlertExists.orFalse().toString(),
        )
    }

    fun postSaveNowButtonClickedAnalytics(ctaAction: String, cardFlow: String) {
        analyticsApi.postEvent(GoldPrice_HomeScreenClicked, buildMapForAnalytics().apply {
            this[cardFlowType] = cardFlow
            this[messageshown] = _goldTrendLiveData.value.data?.data?.subText.orEmpty()
            this[clickaction] = ctaAction
            this[timespent] = calculateTimeDiffInSeconds(initiatedTime)
        })
    }

    private fun calculateTimeDiffInSeconds(initiatedTime: Long): Int {
        return ((Clock.System.now().toEpochMilliseconds() - initiatedTime) / 1000).orZero().toInt()
    }

    fun postTabSelectedAnalyticEvent(orEmpty: String, orZero: Int) {
        analyticsApi.postEvent(GoldPrice_HomeScreenClicked, buildMapForAnalytics().apply {
            this[pricedurationclicked] = "${orEmpty}${orZero}"
        })
    }

    fun postGraphClicked() {
        analyticsApi.postEvent(GoldPrice_HomeScreenClicked, buildMapForAnalytics().apply {
            this[graphclicked] = true.toString()
        })
    }
}
