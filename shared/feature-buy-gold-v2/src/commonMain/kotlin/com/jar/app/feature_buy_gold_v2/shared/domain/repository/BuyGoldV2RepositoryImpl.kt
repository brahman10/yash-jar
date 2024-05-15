package com.jar.app.feature_buy_gold_v2.shared.domain.repository

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_buy_gold_v2.shared.data.network.BuyGoldV2DataSource
import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldBottomSheetV2Data
import com.jar.app.feature_buy_gold_v2.shared.domain.model.ContextBannerResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.InitiateBuyGoldRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class BuyGoldV2RepositoryImpl constructor(
    private val buyGoldV2DataSource: BuyGoldV2DataSource
) : BuyGoldV2Repository {

    override suspend fun fetchBuyGoldInfo() =
        getFlowResult { buyGoldV2DataSource.fetchBuyGoldInfo() }

    override suspend fun fetchAuspiciousDates(
        page: Int,
        size: Int
    ) = buyGoldV2DataSource.fetchAuspiciousDates(page, size)

    override suspend fun fetchIsAuspiciousTime() =
        getFlowResult { buyGoldV2DataSource.fetchIsAuspiciousTime() }

    override suspend fun buyGoldManual(initiateBuyGoldRequest: InitiateBuyGoldRequest) =
        getFlowResult { buyGoldV2DataSource.buyGoldManual(initiateBuyGoldRequest) }

    override suspend fun fetchSuggestedAmount(flowContext: String?, couponCode: String?) =
        getFlowResult {
            buyGoldV2DataSource.fetchBuyGoldOptions(
                flowContext = flowContext,
                couponCode = couponCode
            )
        }

    override suspend fun fetchBuyGoldBottomSheetV2Data(): Flow<RestClientResult<ApiResponseWrapper<BuyGoldBottomSheetV2Data>>> =
        getFlowResult {
            buyGoldV2DataSource.fetchBuyGoldBottomSheetV2Data()
        }

    override suspend fun fetchContextBanner(flowContext: String): Flow<RestClientResult<ApiResponseWrapper<ContextBannerResponse?>>> =
        getFlowResult {
            buyGoldV2DataSource.fetchContextBanner(flowContext)
        }

    override suspend fun fetchBuyGoldAbandonInfo(staticContentType: BaseConstants.StaticContentType) =
        getFlowResult {
            buyGoldV2DataSource.fetchBuyGoldAbandonInfo(staticContentType)
        }

}