package com.jar.app.feature_coupon_api

import com.jar.app.feature_coupon_api.data.network.CouponDataSource
import com.jar.app.feature_coupon_api.data.repository.CouponRepository
import com.jar.app.feature_coupon_api.domain.repository.CouponRepositoryImpl
import com.jar.app.feature_coupon_api.domain.use_case.ApplyCouponUseCase
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_coupon_api.domain.use_case.impl.ApplyCouponUseCaseImpl
import com.jar.app.feature_coupon_api.domain.use_case.impl.FetchCouponCodeUseCaseImpl
import com.jar.app.feature_coupon_api.util.CouponOrderUtil
import io.ktor.client.HttpClient

class CouponApiModule(
    client: HttpClient,
    ) {
    private val couponDataSource by lazy {
        CouponDataSource(client = client)
    }

    private val couponRepository: CouponRepository by lazy {
        CouponRepositoryImpl(couponDataSource)
    }

    val fetchCouponCodeUseCase: FetchCouponCodeUseCase by lazy {
        FetchCouponCodeUseCaseImpl(couponRepository)
    }


    val applyCouponUseCase: ApplyCouponUseCase by lazy {
        ApplyCouponUseCaseImpl(couponRepository)
    }

    val couponOrderUtil: CouponOrderUtil by lazy {
        CouponOrderUtil()
    }
}