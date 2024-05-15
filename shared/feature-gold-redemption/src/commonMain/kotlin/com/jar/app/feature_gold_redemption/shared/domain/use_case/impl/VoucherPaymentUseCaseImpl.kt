package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherInitiatePaymentUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherPaymentUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherInitiatePaymentUseCase {
    override suspend fun initiatePayment(
        tnxAmt: String,
        orderId: String
    ): Flow<RestClientResult<ApiResponseWrapper<FetchManualPaymentRequest?>>> {
        return goldRedemptionRepository.initiatePayment(tnxAmt, orderId)
    }
}