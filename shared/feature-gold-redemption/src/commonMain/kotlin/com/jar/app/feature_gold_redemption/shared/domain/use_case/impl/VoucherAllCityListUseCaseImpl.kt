package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.AllCitiesResponse
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllCityListUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherAllCityListUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherAllCityListUseCase {
    override suspend fun fetchAllCityList(
        stateName: String,
        brandName: String,
    ): Flow<RestClientResult<ApiResponseWrapper<AllCitiesResponse?>>> {
        return goldRedemptionRepository.fetchAllCityList(stateName, brandName)
    }
}