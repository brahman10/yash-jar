package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAbandonScreenUseCase
import com.jar.app.feature_gold_redemption.shared.data.network.model.AbandonScreenData
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherAbandonScreenUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherAbandonScreenUseCase {
    override suspend fun fetchAbandonScreen(): Flow<RestClientResult<ApiResponseWrapper<AbandonScreenData?>>> {
        return goldRedemptionRepository.fetchabandonScreen()
    }
}