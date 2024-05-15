package com.jar.app.feature_coupon_api.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_coupon_api.domain.model.ApplyCouponCodeResponse
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import kotlinx.coroutines.flow.Flow

interface ApplyCouponUseCase {
    suspend fun applyCouponCode(
        amount: Float,
        codeId: String?,
        code: String,
        couponType: String?,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse
    ): Flow<RestClientResult<ApiResponseWrapper<ApplyCouponCodeResponse?>>>
}