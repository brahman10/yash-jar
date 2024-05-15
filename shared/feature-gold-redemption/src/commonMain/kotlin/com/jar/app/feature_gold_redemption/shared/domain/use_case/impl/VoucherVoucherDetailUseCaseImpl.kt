package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherPurchaseAPIData
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherVoucherDetailUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherVoucherDetailUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherVoucherDetailUseCase {
    override suspend fun fetchVoucherDetail(string: String): Flow<RestClientResult<ApiResponseWrapper<VoucherPurchaseAPIData?>>> {
        return goldRedemptionRepository.fetchVoucherDetail(string)
    }
}