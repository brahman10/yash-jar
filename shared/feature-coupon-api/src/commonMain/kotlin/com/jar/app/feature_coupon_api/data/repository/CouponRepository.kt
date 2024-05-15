package com.jar.app.feature_coupon_api.data.repository

import com.jar.app.feature_coupon_api.domain.model.ApplyCouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.CouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandCouponData
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.app.feature_coupon_api.domain.model.jar_coupon.JarCouponData
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface CouponRepository : BaseRepository {
    suspend fun fetchCouponCodes(
        context: String? = null,
        includeView: Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<CouponCodeResponse?>>>

    suspend fun applyCouponCode(
        amount: Float,
        codeId: String?,
        code: String,
        couponType: String?,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse
    ): Flow<RestClientResult<ApiResponseWrapper<ApplyCouponCodeResponse?>>>

    suspend fun fetchJarCoupons(context: String? = null): Flow<RestClientResult<ApiResponseWrapper<JarCouponData>>>

    suspend fun fetchBrandCouponsWithoutPaging(): Flow<RestClientResult<ApiResponseWrapper<BrandCouponData>>>

    suspend fun fetchCouponDetails(brandCouponId: String): Flow<RestClientResult<ApiResponseWrapper<CouponDetailsResponse>>>
}