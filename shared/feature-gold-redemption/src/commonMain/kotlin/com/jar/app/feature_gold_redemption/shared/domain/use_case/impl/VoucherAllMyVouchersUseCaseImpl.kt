package com.jar.app.feature_gold_redemption.shared.domain.use_case.impl

import com.jar.app.feature_gold_redemption.shared.data.network.model.MyVouchersAPIResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.FetchVoucherType
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllMyVouchersUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class VoucherAllMyVouchersUseCaseImpl constructor(private val goldRedemptionRepository: GoldRedemptionRepository) :
    VoucherAllMyVouchersUseCase {
    override suspend fun fetchAllMyVouchers(voucherType: FetchVoucherType?): Flow<RestClientResult<ApiResponseWrapper<MyVouchersAPIResponse?>>> {
        return goldRedemptionRepository.fetchAllMyVouchers(voucherType)
    }
}