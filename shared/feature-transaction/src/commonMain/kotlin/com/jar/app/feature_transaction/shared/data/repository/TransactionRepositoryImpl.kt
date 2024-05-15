package com.jar.app.feature_transaction.shared.data.repository

import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_transaction.shared.data.network.TransactionDataSource
import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository

internal class TransactionRepositoryImpl constructor(
    private val transactionDataSource: TransactionDataSource
) : TransactionRepository {

    override suspend fun fetchUserGoldDetails() = getFlowResult {
        transactionDataSource.fetchUserGoldDetails()
    }

    override suspend fun fetchUserWinningDetails() = getFlowResult {
        transactionDataSource.fetchUserWinningDetails()
    }

    override suspend fun fetchInvestedAmountBreakdown() = getFlowResult {
        transactionDataSource.fetchInvestedAmountBreakup()
    }

    override suspend fun fetchTransactionFilters() = getFlowResult {
        transactionDataSource.fetchFilters()
    }

    override suspend fun getTransactionListingPaginated(request: com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest) =
        transactionDataSource.fetchTransactionListPaginated(request)

    override suspend fun getWinningListingPaginated(pageNo: Int, pageSize: Int) =
        transactionDataSource.fetchWinningListPaginated(pageNo, pageSize)

    override suspend fun fetchTxnDetails(
        orderId: String,
        assetSourceType: String,
        assetTxnId: String
    ) = getFlowResult {
        transactionDataSource.fetchTxnDetails(orderId, assetSourceType, assetTxnId)
    }

    override suspend fun fetchNewTxnDetails(
        orderId: String,
        assetSourceType: String,
        assetTxnId: String
    ) = getFlowResult {
        transactionDataSource.fetchNewTxnDetails(orderId, assetSourceType, assetTxnId)
    }

    override suspend fun postTransactionAction(
        orderId: String,
        type: TransactionActionType,
        vpa: String
    ) = getFlowResult {
        transactionDataSource.postTransactionAction(type, orderId, vpa)
    }

    override suspend fun investWinningInGold(investWinningInGoldRequest: com.jar.app.feature_transaction.shared.domain.model.InvestWinningInGoldRequest) =
        getFlowResult { transactionDataSource.investWinningInGold(investWinningInGoldRequest) }

    override suspend fun fetchPaymentTransactionBreakup(
        orderId: String?,
        type: String?
    ) = getFlowResult { transactionDataSource.fetchPaymentTransactionBreakup(orderId, type) }


    override suspend fun fetchPostSetupTransactionDetails(id: String) =
        getFlowResult { transactionDataSource.fetchPostSetupTransactionDetails(id) }

    override suspend fun fetchUserWinningsBreakDown() = getFlowResult {
        transactionDataSource.fetchUserWinningsBreakdown()
    }
}