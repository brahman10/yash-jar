package com.jar.app.feature_sell_gold.shared.domain.repository

import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature_sell_gold.shared.domain.models.DrawerDetailsResponse
import com.jar.app.feature_sell_gold.shared.domain.models.GoldSellOptionResponse
import com.jar.app.feature_sell_gold.shared.domain.models.KycDetailsResponse
import com.jar.app.feature_sell_gold.shared.domain.models.RetryPayoutResponse
import com.jar.app.feature_sell_gold.shared.domain.models.SellGoldStaticData
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawHelpData
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawRequest
import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface IWithdrawalRepository : BaseRepository {

    suspend fun postGoldWithdrawalRequest(request: WithdrawRequest):
            Flow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>

    suspend fun fetchGoldSellOptionData():
            Flow<RestClientResult<ApiResponseWrapper<GoldSellOptionResponse?>>>

    suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType): Flow<RestClientResult<ApiResponseWrapper<SellGoldStaticData?>>>

    suspend fun fetchWithdrawalStatus(orderId: String): Flow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>

    suspend fun updateWithdrawalReason(
        orderId: String,
        reason: String
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun postTransactionAction(
        orderId: String,
        type: TransactionActionType,
        vpa: String
    ): Flow<RestClientResult<ApiResponseWrapper<RetryPayoutResponse?>>>

    suspend fun fetchWithdrawBottomSheetData(): Flow<RestClientResult<ApiResponseWrapper<WithdrawHelpData?>>>

    suspend fun fetchDrawerDetails(): Flow<RestClientResult<ApiResponseWrapper<DrawerDetailsResponse?>>>
    suspend fun fetchKycDetails(): Flow<RestClientResult<ApiResponseWrapper<KycDetailsResponse?>>>
}