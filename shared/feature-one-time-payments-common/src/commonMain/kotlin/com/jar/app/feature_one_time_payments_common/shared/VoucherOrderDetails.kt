package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class VoucherOrderDetails(
    @SerialName("amount")
    val amount: Int? = null,
    @SerialName("brandName")
    val brandName: String? = null,
    @SerialName("productType")
    val productType: String? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
    @SerialName("validity")
    val validity: String? = null
) : Parcelable