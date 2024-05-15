package com.jar.app.feature_buy_gold_v2.shared.data.repository

import com.jar.app.core_base.domain.model.InfoDialogResponse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousDateList
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousTimeResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldAbandonResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldBottomSheetV2Data
import com.jar.app.feature_buy_gold_v2.shared.domain.model.ContextBannerResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.InitiateBuyGoldRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.SuggestedAmountData
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface BuyGoldV2Repository : BaseRepository {

    suspend fun fetchBuyGoldInfo(): Flow<RestClientResult<ApiResponseWrapper<InfoDialogResponse>>>

    suspend fun fetchAuspiciousDates(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<AuspiciousDateList>>

    suspend fun fetchIsAuspiciousTime(): Flow<RestClientResult<ApiResponseWrapper<AuspiciousTimeResponse>>>

    suspend fun buyGoldManual(initiateBuyGoldRequest: InitiateBuyGoldRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>

    suspend fun fetchSuggestedAmount(
        flowContext: String?,
        couponCode: String?
    ): Flow<RestClientResult<ApiResponseWrapper<SuggestedAmountData?>>>

    suspend fun fetchBuyGoldBottomSheetV2Data(): Flow<RestClientResult<ApiResponseWrapper<BuyGoldBottomSheetV2Data>>>

    suspend fun fetchContextBanner(flowContext: String): Flow<RestClientResult<ApiResponseWrapper<ContextBannerResponse?>>>

    suspend fun fetchBuyGoldAbandonInfo(staticContentType: BaseConstants.StaticContentType):
            Flow<RestClientResult<ApiResponseWrapper<BuyGoldAbandonResponse>>>
}