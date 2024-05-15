package com.jar.app.feature_coupon_api.domain.model


import com.jar.app.feature_coupon_api.domain.model.offer_list.SpinCTA
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmptyCouponData(
    @SerialName("header")
    val header: String,
    @SerialName("image")
    val image: String,
    @SerialName("spinCTA")
    val spinCTA: SpinCTA? = null
)