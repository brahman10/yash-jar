package com.jar.app.feature_coupon_api.domain.use_case

import com.jar.app.feature_coupon_api.domain.model.CouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandCouponData
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.app.feature_coupon_api.domain.model.jar_coupon.JarCouponData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchCouponCodeUseCase {
    suspend fun fetchCouponCodes(
        context: String? = null,
        includeView: Boolean = false
    ): Flow<RestClientResult<ApiResponseWrapper<CouponCodeResponse?>>>

    suspend fun fetchJarCoupons(context: String? = null): Flow<RestClientResult<ApiResponseWrapper<JarCouponData>>>

    suspend fun fetchBrandCouponsWithoutPaging():Flow<RestClientResult<ApiResponseWrapper<BrandCouponData>>>

    suspend fun fetchCouponDetails(brandCouponId: String): Flow<RestClientResult<ApiResponseWrapper<CouponDetailsResponse>>>

}