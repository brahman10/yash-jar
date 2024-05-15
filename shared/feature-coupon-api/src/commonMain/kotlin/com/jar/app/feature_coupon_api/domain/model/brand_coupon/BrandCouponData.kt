package com.jar.app.feature_coupon_api.domain.model.brand_coupon


import kotlinx.serialization.SerialName
import com.jar.app.feature_coupon_api.domain.model.EmptyCouponData
import kotlinx.serialization.Serializable

@Serializable
data class BrandCouponData(
    @SerialName("activeCoupons")
    val activeCoupons: Int,
    @SerialName("activeCouponsDescription")
    val activeCouponsDescription: String,
    @SerialName("brandsCouponInfoList")
    val brandsCouponInfoList: List<BrandsCouponInfo>?= null,
    @SerialName("offersEmptyResponse")
    val offersEmptyResponse: EmptyCouponData?= null,
    @SerialName("totalCoupons")
    val totalCoupons: Int
)

