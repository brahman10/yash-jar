package com.jar.app.feature_coupon_api.domain.repository

import com.jar.app.feature_coupon_api.data.network.CouponDataSource
import com.jar.app.feature_coupon_api.data.repository.CouponRepository
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandCouponData
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.app.feature_coupon_api.domain.model.jar_coupon.JarCouponData
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class CouponRepositoryImpl constructor(
    private val couponDataSource: CouponDataSource
) : CouponRepository {
    override suspend fun fetchCouponCodes(context: String?, includeView: Boolean) =
        getFlowResult { couponDataSource.fetchCouponCodes(context, includeView) }

    override suspend fun applyCouponCode(
        amount: Float,
        codeId: String?,
        code: String,
        couponType: String?,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse
    ) = getFlowResult {
        couponDataSource.applyCouponCode(
            amount,
            codeId,
            code,
            couponType,
            fetchCurrentGoldPriceResponse
        )
    }

    override suspend fun fetchJarCoupons(context: String?): Flow<RestClientResult<ApiResponseWrapper<JarCouponData>>> =
        getFlowResult { couponDataSource.fetchJarCoupons(context) }


    override suspend fun fetchBrandCouponsWithoutPaging(): Flow<RestClientResult<ApiResponseWrapper<BrandCouponData>>> =
        getFlowResult {
            couponDataSource.fetchBrandCoupons(INITIAL_PAGE, NETWORK_PAGE_SIZE)
        }


    override suspend fun fetchCouponDetails(brandCouponId: String): Flow<RestClientResult<ApiResponseWrapper<CouponDetailsResponse>>> =
        getFlowResult {
            couponDataSource.fetchCouponDetails(brandCouponId)
        }

}

const val NETWORK_PAGE_SIZE = 20
const val INITIAL_PAGE = 0