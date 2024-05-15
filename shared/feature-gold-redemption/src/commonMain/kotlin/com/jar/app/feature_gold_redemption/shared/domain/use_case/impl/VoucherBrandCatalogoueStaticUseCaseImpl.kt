package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.BrandCatalogoueApiData
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherBrandCatalogoueStaticUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherBrandCatalogoueStaticUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherBrandCatalogoueStaticUseCase {
    override suspend fun fetchBrandCatalogoueStatic(): Flow<RestClientResult<ApiResponseWrapper<BrandCatalogoueApiData?>>> {
        return goldRedemptionRepository.fetchBrandCatalogoueStatic()
    }
}