package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.AllVouchersApiData
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllVouchersUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherAllVouchersUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherAllVouchersUseCase {
    override suspend fun fetchAllVouchers(category: String?): Flow<RestClientResult<ApiResponseWrapper<AllVouchersApiData?>>> {
        return goldRedemptionRepository.fetchAllVouchers(category)
    }
}