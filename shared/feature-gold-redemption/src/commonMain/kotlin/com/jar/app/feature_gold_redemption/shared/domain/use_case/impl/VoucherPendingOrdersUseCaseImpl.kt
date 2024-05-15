package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl


import com.jar.app.feature_gold_redemption.shared.data.network.model.PendingOrdersAPIResponse
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherPendingOrdersUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow


class VoucherPendingOrdersUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherPendingOrdersUseCase {
    override suspend fun fetchPendingOrders(): Flow<RestClientResult<ApiResponseWrapper<PendingOrdersAPIResponse?>>> {
        return goldRedemptionRepository.fetchPendingOrders()
    }
}