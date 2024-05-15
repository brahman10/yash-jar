package com.jar.app.feature_coupon_api.domain.use_case.impl

import com.jar.app.feature_coupon_api.data.repository.CouponRepository
import com.jar.app.feature_coupon_api.domain.use_case.ApplyCouponUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse

internal class ApplyCouponUseCaseImpl constructor(private val couponRepository: CouponRepository) :
    ApplyCouponUseCase {

    override suspend fun applyCouponCode(
        amount: Float,
        codeId: String?,
        code: String,
        couponType: String?,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse
    ) = couponRepository.applyCouponCode(amount, codeId, code, couponType, fetchCurrentGoldPriceResponse)
}