package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.data.network.model.IntroScreenAPIDataPart2
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherGoldRedemptionIntroPart2UseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherGoldRedemptionIntroPart2UseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherGoldRedemptionIntroPart2UseCase {
    override suspend fun fetchGoldRedemptionIntroPart2(): Flow<RestClientResult<ApiResponseWrapper<IntroScreenAPIDataPart2?>>> {
        return goldRedemptionRepository.fetchGoldRedemptionIntroPart2()
    }
}