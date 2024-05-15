package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.data.network.model.GoldRedemptionTransactionData
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherTxnDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherTxnDetailsUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherTxnDetailsUseCase {
    override suspend fun fetchTxnDetails(
        orderId: String?,
        voucherId: String?,
        showLoading: () -> Unit,
        shouldRetry: (result: RestClientResult<ApiResponseWrapper<GoldRedemptionTransactionData?>>) -> Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<GoldRedemptionTransactionData?>>> {
        return goldRedemptionRepository.fetchTxnDetailsPolling(orderId, voucherId, showLoading, shouldRetry)
    }
}