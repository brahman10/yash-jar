package com.jar.app.feature_coupon_api.domain.use_case.impl

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_coupon_api.data.repository.CouponRepository
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandCouponData
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import kotlinx.coroutines.flow.Flow

internal class FetchCouponCodeUseCaseImpl constructor(private val couponRepository: CouponRepository) :
    FetchCouponCodeUseCase {

    override suspend fun fetchCouponCodes(context: String?, includeView: Boolean) =
        couponRepository.fetchCouponCodes(context, includeView)

    override suspend fun fetchJarCoupons(context:String?) = couponRepository.fetchJarCoupons(context)

    override suspend fun fetchBrandCouponsWithoutPaging(): Flow<RestClientResult<ApiResponseWrapper<BrandCouponData>>> = couponRepository.fetchBrandCouponsWithoutPaging()

    override suspend fun fetchCouponDetails(brandCouponId: String): Flow<RestClientResult<ApiResponseWrapper<CouponDetailsResponse>>> = couponRepository.fetchCouponDetails(brandCouponId)
}