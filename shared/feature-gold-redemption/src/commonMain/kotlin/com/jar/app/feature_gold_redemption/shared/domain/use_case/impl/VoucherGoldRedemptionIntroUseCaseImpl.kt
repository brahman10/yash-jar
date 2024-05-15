package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.IntroScrenApiData
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherGoldRedemptionIntroUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherGoldRedemptionIntroUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherGoldRedemptionIntroUseCase {
    override suspend fun fetchGoldRedemptionIntro(): Flow<RestClientResult<ApiResponseWrapper<IntroScrenApiData?>>> {
        return goldRedemptionRepository.fetchGoldRedemptionIntro()
    }
}