package com.jar.feature_gold_price_alerts.shared.data.network;

import com.jar.feature_gold_price_alerts.shared.domain.model.AverageBuyPrice
import com.jar.feature_gold_price_alerts.shared.domain.model.CreateAlertRequest
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrend
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendBottomSheetStaticData
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendHomeScreenTab
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendScreenStaticData
import com.jar.feature_gold_price_alerts.shared.domain.model.LatestGoldPriceAlertResponse
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.Endpoints.DISABLE_LATEST_ALERT
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.Endpoints.FETCH_GOLD_PRICE_TREND_HOMESCREEN_TAB
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.Endpoints.FETCH_LATEST_ALERT
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.Endpoints.POST_NEW_ALERT
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class GoldPriceAlertsDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchAverageBuyPrice() =
        getResult<ApiResponseWrapper<AverageBuyPrice>> {
            client.get {
                url(GoldPriceAlertsConstants.Endpoints.FETCH_AVERAGE_BUY_PRICE)
            }
        }

    suspend fun fetchGoldPriceTrend(unit: String, period: Int) =
        getResult<ApiResponseWrapper<GoldTrend>> {
            client.get {
                url(GoldPriceAlertsConstants.Endpoints.FETCH_GOLD_PRICE_TREND)
                parameter("unit", unit)
                parameter("period", period)
            }
        }

    suspend fun fetchGoldPriceTrendScreenStatic() =
        getResult<ApiResponseWrapper<GoldTrendScreenStaticData?>> {
            client.get {
                url(GoldPriceAlertsConstants.Endpoints.FETCH_GOLD_PRICE_SCREEN_STATIC)
            }
        }

    suspend fun fetchGoldPriceTrendBottomSheetStaticData() =
        getResult<ApiResponseWrapper<GoldTrendBottomSheetStaticData?>> {
            client.get {
                url(GoldPriceAlertsConstants.Endpoints.FETCH_GOLD_PRICE_BOTTOMSHEET_STATIC)
            }
        }

    suspend fun createGoldPriceAlert(body: CreateAlertRequest) =
        getResult<ApiResponseWrapper<Unit>> {
            client.post {
                url(POST_NEW_ALERT)
                setBody(body)
            }
        }

    suspend fun getLatestGoldPriceAlert() =
        getResult<ApiResponseWrapper<LatestGoldPriceAlertResponse>> {
            client.get { url(FETCH_LATEST_ALERT) }
        }

    suspend fun disableGoldPriceAlert(alertId: String) = getResult<ApiResponseWrapper<Unit>> {
        client.post {
            url(DISABLE_LATEST_ALERT)
            parameter("alertId", alertId)
        }
    }

    suspend fun fetchGoldTrendHomeScreenTab() = getResult<ApiResponseWrapper<GoldTrendHomeScreenTab>> {
        client.get {
            url(FETCH_GOLD_PRICE_TREND_HOMESCREEN_TAB)
        }
    }
}
