package com.jar.app.feature_coupon_api.domain.model.brand_coupon


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrandsCouponInfo(
    @SerialName("brandCouponCodeId")
    val brandCouponCodeId: String?= null,
    @SerialName("brandIconLink")
    val brandIconLink: String?= null,
    @SerialName("brandName")
    val brandName: String?= null,
    @SerialName("couponState")
    val couponState: CouponState?= null,
    @SerialName("description")
    val description: String?= null,
    @SerialName("expiry")
    val expiry: Long?= null,
    @SerialName("title")
    val title: String?= null
)

enum class CouponState{
    ACTIVE,
    INACTIVE
}