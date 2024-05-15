package com.jar.app.feature_coupon_api.domain.model.jar_coupon


import com.jar.app.feature_coupon_api.domain.model.EmptyCouponData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JarCouponData(
    @SerialName("activeCoupons")
    val activeCoupons: Int,
    @SerialName("activeOffersDescription")
    val activeOffersDescription: String?= null,
    @SerialName("applyCouponDeepLink")
    val applyCouponDeepLink: String?= null,
    @SerialName("jarCouponInfoList")
    val jarCouponInfoList: List<JarCouponInfo>?= null,
    @SerialName("offersEmptyResponse")
    val offersEmptyResponse: EmptyCouponData?= null
)