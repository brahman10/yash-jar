package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.GenericFAQs
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherFaqsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherFaqsUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherFaqsUseCase {
    override suspend fun fetchFaqs(): Flow<RestClientResult<ApiResponseWrapper<GenericFAQs?>>> {
        return goldRedemptionRepository.fetchFaqs()
    }
}