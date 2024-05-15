package com.jar.app.feature_coupon_api.domain.model.coupon_details


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponDetailsResponse(
    @SerialName("brandIconLink")
    val brandIconLink: String?= null,
    @SerialName("brandName")
    val brandName: String?= null,
    @SerialName("couponCode")
    val couponCode: String?= null,
    @SerialName("couponCodeText")
    val couponCodeText: String?= null,
    @SerialName("description")
    val description: String?= null,
    @SerialName("expiryDate")
    val expiryDate: Long?= null,
    @SerialName("pageHeader")
    val pageHeader: String?= null,
    @SerialName("redeemDescription")
    val redeemDescription: String?= null,
    @SerialName("redeemHeader")
    val redeemHeader: String?= null,
    @SerialName("shareMsg")
    val shareMsg: String?= null,
    @SerialName("termsAndConditionsDescription")
    val termsAndConditionsDescription: String?= null,
    @SerialName("termsAndConditionsHeader")
    val termsAndConditionsHeader: String?= null,
    @SerialName("title")
    val title: String?= null
)