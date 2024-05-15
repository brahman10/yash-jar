package com.jar.app.feature_transaction.shared.domain.repository

import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface TransactionRepository : BaseRepository {

    suspend fun fetchUserGoldDetails(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserGoldDetailsRes?>>>

    suspend fun fetchUserWinningDetails(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserWinningDetailsRes>>>

    suspend fun fetchInvestedAmountBreakdown(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.InvestmentBreakDown?>>>

    suspend fun fetchTransactionFilters(): Flow<RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_transaction.shared.domain.model.FilterResponse>>>>

    suspend fun getTransactionListingPaginated(request: com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest):
            RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_transaction.shared.domain.model.TransactionData>>?>

    suspend fun getWinningListingPaginated(pageNo: Int, pageSize: Int):
            RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_transaction.shared.domain.model.WinningData>>>

    suspend fun fetchTxnDetails(orderId: String, assetSourceType: String, assetTxnId: String):
            Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails>>>

    suspend fun fetchNewTxnDetails(orderId: String, assetSourceType: String, assetTxnId: String):
            Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.TransactionDetailsV5Data?>>>

    suspend fun postTransactionAction(
        orderId: String,
        type: TransactionActionType,
        vpa: String
    ): Flow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse>>>

    suspend fun investWinningInGold(investWinningInGoldRequest: com.jar.app.feature_transaction.shared.domain.model.InvestWinningInGoldRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchPaymentTransactionBreakup(
        orderId: String?,
        type: String?
    ): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.PaymentTransactionBreakup>>>

    suspend fun fetchPostSetupTransactionDetails(
        id: String
    ): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails>>>

    suspend fun fetchUserWinningsBreakDown() : Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserWinningBreakdownModel>>>
}