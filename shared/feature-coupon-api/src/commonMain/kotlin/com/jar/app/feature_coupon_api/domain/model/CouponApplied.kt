package com.jar.app.feature_coupon_api.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize


@Parcelize
@kotlinx.serialization.Serializable
data class CouponApplied(
    val couponCode: String,
    val couponTile: String,
    val couponDescription: String,
    val couponPosition: Int,
    val couponType: String
): Parcelable