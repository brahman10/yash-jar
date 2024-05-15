package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.ViewVoucherDetailsAPIResponse
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherViewDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherViewDetailsUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherViewDetailsUseCase {
    override suspend fun fetchViewDetails(
        voucherId: String,
        orderId: String,
    ): Flow<RestClientResult<ApiResponseWrapper<ViewVoucherDetailsAPIResponse?>>> {
        return goldRedemptionRepository.fetchviewDetails(voucherId, orderId)
    }
}