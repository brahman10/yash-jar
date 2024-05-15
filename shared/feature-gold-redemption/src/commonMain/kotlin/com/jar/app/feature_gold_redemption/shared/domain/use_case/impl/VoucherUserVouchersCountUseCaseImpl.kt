package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.MyVouchersTabCountResponse
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherUserVouchersCountUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherUserVouchersCountUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherUserVouchersCountUseCase {
    override suspend fun fetchUserVouchersCount(): Flow<RestClientResult<ApiResponseWrapper<MyVouchersTabCountResponse?>>> {
        return goldRedemptionRepository.fetchUserVouchersCount()
    }
}