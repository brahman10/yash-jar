package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllStoreFromCityUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherAllStoreFromCityUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherAllStoreFromCityUseCase {
    override suspend fun fetchAllStoreFromCity(
        cityName: String,
        brandName: String,
    ): Flow<RestClientResult<ApiResponseWrapper<List<String?>?>>> {
        return goldRedemptionRepository.fetchAllStoreFromCity(cityName, brandName)
    }
}