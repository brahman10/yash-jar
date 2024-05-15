package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherPurchaseApiResponse
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherPurchaseHistoryUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherPurchaseHistoryUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherPurchaseHistoryUseCase {
    override suspend fun fetchPurchaseHistory(): Flow<RestClientResult<ApiResponseWrapper<VoucherPurchaseApiResponse?>>> {
        return goldRedemptionRepository.fetchPurchaseHistory()
    }
}