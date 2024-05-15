package com.jar.app.feature_coupon_api.data.network

import com.jar.app.feature_coupon_api.domain.model.ApplyCouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.CouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandCouponData
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.app.feature_coupon_api.domain.model.jar_coupon.JarCouponData
import com.jar.app.feature_coupon_api.util.CouponConstants
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class CouponDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {
    suspend fun fetchCouponCodes(context: String?, includeView: Boolean) =
        getResult<ApiResponseWrapper<CouponCodeResponse?>> {
            client.get {
                url(CouponConstants.Endpoints.FETCH_COUPON_CODES)
                parameter("context", context)
                parameter("includeView", includeView)
            }
        }

    suspend fun applyCouponCode(
        amount: Float,
        couponCodeId: String?,
        couponCode: String,
        couponType: String?,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse
    ) = getResult<ApiResponseWrapper<ApplyCouponCodeResponse?>> {
        client.post {
            url(CouponConstants.Endpoints.APPLY_COUPON_CODE)
            parameter("amount", amount)
            parameter("couponCodeId", couponCodeId)
            parameter("couponCode", couponCode)
            parameter("couponType", couponType)
            setBody(fetchCurrentGoldPriceResponse)
        }
    }

    suspend fun fetchJarCoupons(context: String?) =
        getResult<ApiResponseWrapper<JarCouponData>> {
            client.get {
                url(CouponConstants.Endpoints.FETCH_JAR_COUPONS)
                parameter("context", context)
            }
        }

    suspend fun fetchBrandCoupons(page: Int, size: Int) =
        getResult<ApiResponseWrapper<BrandCouponData>> {
            client.get {
                url(CouponConstants.Endpoints.FETCH_BRAND_COUPONS)
                parameter("page", page)
                parameter("size", size)
            }
        }

    suspend fun fetchCouponDetails(brandCouponCodeId: String) =
        getResult<ApiResponseWrapper<CouponDetailsResponse>> {
            client.get {
                url(CouponConstants.Endpoints.FETCH_COUPON_DETAILS)
                parameter("brandCouponCodeId", brandCouponCodeId)
            }
        }


}