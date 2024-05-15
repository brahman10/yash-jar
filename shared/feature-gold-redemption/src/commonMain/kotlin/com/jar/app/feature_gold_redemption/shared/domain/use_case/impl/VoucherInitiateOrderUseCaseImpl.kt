package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.GoldRedemptionInitiateCreateOrderRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherInitiateOrderUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherInitiateOrderUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherInitiateOrderUseCase {
    override suspend fun initiateOrder(goldRedemptionInitiateCreateOrderRequest: GoldRedemptionInitiateCreateOrderRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>> {
        return goldRedemptionRepository.initiateOrder(goldRedemptionInitiateCreateOrderRequest)
    }
}