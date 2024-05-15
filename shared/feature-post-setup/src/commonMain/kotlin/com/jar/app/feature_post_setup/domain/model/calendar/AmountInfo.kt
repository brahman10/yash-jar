package com.jar.app.feature_post_setup.domain.model.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AmountInfo(
    @SerialName("status")
    val status: String,
    @SerialName("amount")
    val amount: Float,
    @SerialName("noOfDays")
    val noOfDays: Int,
    @SerialName("allowPayment")
    val allowPayment: Boolean? = null,
    @SerialName("roundOffIds")
    val roundOffIds: List<String>? = null,
    var shouldShowShimmer: Boolean = false
)
