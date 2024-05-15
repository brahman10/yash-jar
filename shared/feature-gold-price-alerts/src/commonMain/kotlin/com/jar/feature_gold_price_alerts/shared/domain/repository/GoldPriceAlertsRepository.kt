package com.jar.feature_gold_price_alerts.shared.domain.repository

import com.jar.feature_gold_price_alerts.shared.domain.model.AverageBuyPrice
import com.jar.feature_gold_price_alerts.shared.domain.model.CreateAlertRequest
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrend
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendBottomSheetStaticData
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendHomeScreenTab
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendScreenStaticData
import com.jar.feature_gold_price_alerts.shared.domain.model.LatestGoldPriceAlertResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface GoldPriceAlertsRepository : BaseRepository {
    suspend fun fetchGoldPriceTrend(
        unit: String,
        period: Int
    ): Flow<RestClientResult<ApiResponseWrapper<GoldTrend>>>

    suspend fun fetchAverageBuyPrice(): Flow<RestClientResult<ApiResponseWrapper<AverageBuyPrice>>>
    suspend fun fetchGoldPriceTrendScreenStatic(): Flow<RestClientResult<ApiResponseWrapper<GoldTrendScreenStaticData?>>>

    suspend fun createGoldPriceAlert(body: CreateAlertRequest): Flow<RestClientResult<ApiResponseWrapper<Unit>>>
    suspend fun getLatestGoldPriceAlert(): Flow<RestClientResult<ApiResponseWrapper<LatestGoldPriceAlertResponse>>>
    suspend fun disableGoldPriceAlert(alertId: String): Flow<RestClientResult<ApiResponseWrapper<Unit>>>

    suspend fun fetchGoldPriceTrendBottomSheetStaticData(): Flow<RestClientResult<ApiResponseWrapper<GoldTrendBottomSheetStaticData?>>>

    suspend fun fetchGoldTrendHomeScreenTab(): Flow<RestClientResult<ApiResponseWrapper<GoldTrendHomeScreenTab>>>
}