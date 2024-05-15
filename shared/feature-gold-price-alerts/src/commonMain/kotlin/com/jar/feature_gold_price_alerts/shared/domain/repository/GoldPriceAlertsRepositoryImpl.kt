package com.jar.feature_gold_price_alerts.shared.domain.repository

import com.jar.feature_gold_price_alerts.shared.data.network.GoldPriceAlertsDataSource
import com.jar.feature_gold_price_alerts.shared.domain.model.CreateAlertRequest
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendBottomSheetStaticData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class GoldPriceAlertsRepositoryImpl constructor(private val dataSource: GoldPriceAlertsDataSource) :
    GoldPriceAlertsRepository {

    override suspend fun fetchGoldPriceTrend(
        unit: String,
        period: Int
    ) = getFlowResult { dataSource.fetchGoldPriceTrend(unit, period) }

    override suspend fun fetchAverageBuyPrice() = getFlowResult {
        dataSource.fetchAverageBuyPrice()
    }

    override suspend fun fetchGoldPriceTrendScreenStatic() =
        getFlowResult { dataSource.fetchGoldPriceTrendScreenStatic() }

    override suspend fun createGoldPriceAlert(body: CreateAlertRequest) =
        getFlowResult { dataSource.createGoldPriceAlert(body) }

    override suspend fun getLatestGoldPriceAlert() =
        getFlowResult { dataSource.getLatestGoldPriceAlert() }

    override suspend fun disableGoldPriceAlert(alertId: String) =
        getFlowResult { dataSource.disableGoldPriceAlert(alertId) }

    override suspend fun fetchGoldPriceTrendBottomSheetStaticData(): Flow<RestClientResult<ApiResponseWrapper<GoldTrendBottomSheetStaticData?>>> =
        getFlowResult { dataSource.fetchGoldPriceTrendBottomSheetStaticData() }

    override suspend fun fetchGoldTrendHomeScreenTab() =
        getFlowResult { dataSource.fetchGoldTrendHomeScreenTab() }
}