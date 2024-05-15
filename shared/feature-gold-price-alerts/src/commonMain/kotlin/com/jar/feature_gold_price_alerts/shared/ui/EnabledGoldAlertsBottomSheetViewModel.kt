package com.jar.feature_gold_price_alerts.shared.ui

import com.jar.feature_gold_price_alerts.shared.domain.model.LatestGoldPriceAlertResponse
import com.jar.feature_gold_price_alerts.shared.domain.use_case.DisableGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.GetLatestGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.GoldPrice_AlertBSClicked
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.GoldPrice_AlertBSShown
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.alertstatus
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.clickaction
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.title
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EnabledGoldAlertsBottomSheetViewModel constructor(
    private val disableGoldPriceAlertUseCase: DisableGoldPriceAlertUseCase,
    private val fetchLatestGoldPriceAlertUseCase: GetLatestGoldPriceAlertUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _bottomSheetStaticDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LatestGoldPriceAlertResponse>>>(
            RestClientResult.none()
        )
    val bottomSheetStaticDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LatestGoldPriceAlertResponse>>>
        get() = _bottomSheetStaticDataFlow.toCommonStateFlow()

    private val _disableGoldAlertFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit>>>(RestClientResult.none())
    val disableGoldAlertFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit>>>
        get() = _disableGoldAlertFlow.toCommonStateFlow()

    fun fetchBottomSheetStaticDataUseCase() {
        viewModelScope.launch {
            fetchLatestGoldPriceAlertUseCase.getLatestGoldPriceAlert().collect {
                _bottomSheetStaticDataFlow.emit(it)
            }
        }
    }

    fun removeAlert() {
        val alertId = _bottomSheetStaticDataFlow.value.data?.data?.alertId.orEmpty()
        viewModelScope.launch {
            disableGoldPriceAlertUseCase.disableGoldPriceAlert(alertId).collect {
                _disableGoldAlertFlow.emit(it)
            }
        }
    }

    fun postAnalyticEventForClickAction(fromClickAction: String) {
        val data = _bottomSheetStaticDataFlow.value.data?.data
        analyticsApi.postEvent(GoldPrice_AlertBSClicked, mutableMapOf<String, String>().apply {
            data?.tableData?.forEach { this[it.key.orEmpty()] = it.value.orEmpty() }
            this[alertstatus] = true.toString()
            this[title] = data?.title.toString()
            this[clickaction] = fromClickAction
        })

    }
    fun postShownAnalyticEvent() {
        val data = _bottomSheetStaticDataFlow.value.data?.data
        analyticsApi.postEvent(GoldPrice_AlertBSShown, mutableMapOf<String, String>().apply {
            data?.tableData?.forEach { this[it.key.orEmpty()] = it.value.orEmpty() }
            this[alertstatus] = true.toString()
            this[title] = data?.title.toString()
        })
    }
}
