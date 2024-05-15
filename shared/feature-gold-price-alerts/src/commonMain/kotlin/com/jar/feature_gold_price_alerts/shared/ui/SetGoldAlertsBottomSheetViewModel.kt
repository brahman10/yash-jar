package com.jar.feature_gold_price_alerts.shared.ui

import com.jar.app.core_base.util.orZero
import com.jar.feature_gold_price_alerts.shared.domain.model.CreateAlertRequest
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendBottomSheetStaticData
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendPricePill
import com.jar.feature_gold_price_alerts.shared.domain.model.LiveBuyPrice
import com.jar.feature_gold_price_alerts.shared.domain.use_case.CreateGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendBottomSheetStaticDataUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.GetLatestGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.GoldPrice_AlertBSClicked
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.GoldPrice_AlertBSShown
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.PopularAlertPrice
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.alertpricechosen
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.alertpriceshown
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.clickaction
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SetGoldAlertsBottomSheetViewModel constructor(
    private val createGoldPriceAlertUseCase: CreateGoldPriceAlertUseCase,
    private val getLatestGoldPriceAlertUseCase: GetLatestGoldPriceAlertUseCase,
    private val fetchBottomSheetStaticDataUseCase: FetchGoldPriceTrendBottomSheetStaticDataUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _bottomSheetStaticDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldTrendBottomSheetStaticData?>>>(
            RestClientResult.none()
        )
    val bottomSheetStaticDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldTrendBottomSheetStaticData?>>>
        get() = _bottomSheetStaticDataFlow.toCommonStateFlow()

    private val _amountPillsListFlow =
        MutableStateFlow<List<GoldTrendPricePill>>(listOf())
    val amountPillsListFlow: CStateFlow<List<GoldTrendPricePill>>
        get() = _amountPillsListFlow.toCommonStateFlow()

    private val _createGoldPriceAlertFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit>>>()
    val createGoldPriceAlertFlow: CFlow<RestClientResult<ApiResponseWrapper<Unit>>>
        get() = _createGoldPriceAlertFlow.toCommonFlow()

    fun fetchBottomSheetStaticDataUseCase() {
        viewModelScope.launch {
            fetchBottomSheetStaticDataUseCase.fetchGoldPriceTrendBottomSheetStaticData().collect {
                _bottomSheetStaticDataFlow.emit(it)
            }
        }
        viewModelScope.launch {
            getLatestGoldPriceAlertUseCase.getLatestGoldPriceAlert().collect {

            }
        }
    }

    fun setPriceList(priceDropPills: List<GoldTrendPricePill>) {
        viewModelScope.launch {
            _amountPillsListFlow.emit(priceDropPills)
            analyticsApi.postEvent(GoldPrice_AlertBSShown, buildMapForAnalytics())
        }
    }

    fun submitPriceAlert(price: Float) {
        _bottomSheetStaticDataFlow.value.data?.data?.liveBuyPrice?.let {
            postPriceAlert(
                price,
                it
            )
        }
    }


    fun submitPriceAlert() {
        val liveBuyPrice = _bottomSheetStaticDataFlow.value.data?.data?.liveBuyPrice
        (_amountPillsListFlow.value.filter { it.isSelected }.takeIf { it.isNotEmpty() }
            ?.getOrNull(0)?.price)?.takeIf { liveBuyPrice != null }?.let {
            postPriceAlert(
                it,
                _bottomSheetStaticDataFlow.value.data?.data?.liveBuyPrice!!
            )
        } ?: run {
            // show toast
        }
    }

    private fun postPriceAlert(title: Float, liveBuyPrice: LiveBuyPrice) {
        viewModelScope.launch {
            createGoldPriceAlertUseCase.createGoldPriceAlert(
                CreateAlertRequest(
                    liveBuyPrice,
                    title
                )
            ).collectLatest {
                _createGoldPriceAlertFlow.emit(it)
            }
        }
    }

    fun setSelected(selected: GoldTrendPricePill) {
        viewModelScope.launch {
            val newList = _amountPillsListFlow.value.map { it.copy(isSelected = selected.price.equals(it.price)) }
            _amountPillsListFlow.emit(newList)
        }
    }

    fun getSelectedAmount(): String {
        return _amountPillsListFlow.value.singleOrNull { it.isSelected }?.price.orZero().toString()
    }
    fun buildMapForAnalytics(): MutableMap<String, String> {
        val amounts = _bottomSheetStaticDataFlow.value.data?.data?.priceDropPills?.map { it.toString() }
                ?.joinToString(separator = ",")
        val popularPrice = _bottomSheetStaticDataFlow.value.data?.data?.pricePills?.find { it.pillText.isNullOrEmpty().not() }?.price.orZero().toString()
        return mutableMapOf<String, String>(
            alertpriceshown to amounts.orEmpty(),
            alertpricechosen to getSelectedAmount(),
            PopularAlertPrice to popularPrice
        )
    }

    fun postEventForClick(cta: String) {
        val popularPrice = _bottomSheetStaticDataFlow.value.data?.data?.pricePills?.find { it.pillText.isNullOrEmpty().not() }?.price.orZero().toString()
        analyticsApi.postEvent(GoldPrice_AlertBSClicked, buildMapForAnalytics().apply {
            this[clickaction] = cta
            this[PopularAlertPrice] = popularPrice
        })
    }
}
