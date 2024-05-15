package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.StateData
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllStatesListUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherAllStatesListUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherAllStatesListUseCase {
    override suspend fun fetchAllStatesList(brandName: String): Flow<RestClientResult<ApiResponseWrapper<List<StateData?>?>>> {
        return goldRedemptionRepository.fetchAllStatesList(brandName)
    }
}