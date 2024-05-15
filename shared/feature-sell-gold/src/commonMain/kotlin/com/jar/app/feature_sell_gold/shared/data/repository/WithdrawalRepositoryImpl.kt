package com.jar.app.feature_sell_gold.shared.data.repository

import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature_sell_gold.shared.data.network.WithdrawalDataSource
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawRequest
import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold_common.shared.TransactionActionType

internal class WithdrawalRepositoryImpl constructor(
    private val dataSource: WithdrawalDataSource
) : IWithdrawalRepository {

    override suspend fun postGoldWithdrawalRequest(request: WithdrawRequest) = getFlowResult {
        dataSource.makeWithdrawalRequest(request)
    }

    override suspend fun fetchGoldSellOptionData() = getFlowResult {
        dataSource.fetchGoldSellOptions()
    }

    override suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType) =
        getFlowResult { dataSource.fetchDashboardStaticContent(staticContentType) }

    override suspend fun fetchWithdrawalStatus(orderId: String) =
        getFlowResult { dataSource.fetchWithdrawalStatus(orderId) }

    override suspend fun updateWithdrawalReason(orderId: String, reason: String) =
        getFlowResult { dataSource.updateWithdrawalReason(orderId, reason) }

    override suspend fun postTransactionAction(
        orderId: String,
        type: TransactionActionType,
        vpa: String
    ) = getFlowResult {
        dataSource.postTransactionAction(type, orderId, vpa)
    }

    override suspend fun fetchWithdrawBottomSheetData() = getFlowResult {
        dataSource.fetchWithdrawBottomSheetData()
    }

    override suspend fun fetchDrawerDetails() = getFlowResult {
        dataSource.fetchDrawerDetails()
    }

    override suspend fun fetchKycDetails() = getFlowResult {
        dataSource.fetchKycDetails()
    }
}