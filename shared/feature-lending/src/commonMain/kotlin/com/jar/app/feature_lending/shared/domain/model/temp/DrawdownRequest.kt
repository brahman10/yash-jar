package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DrawdownRequest(
    @SerialName("amount")
    val amount: Float? = null,

    @SerialName("tenure")
    val tenure: Int? = null,

    @SerialName("frequency")
    val frequency: String? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("roi")
    val roi: Float? = null,

    @SerialName("createdAtEpoch")
    val createdAtEpoch: Long? = null,

    @SerialName("charges")
    val charges: List<DrawDownCharges>? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class DrawDownCharges(
    @SerialName("key")
    val key: String? = null,

    @SerialName("actualAmt")
    val actualAmt: Float? = null,

    @SerialName("currentAmt")
    val currentAmt: Float? = null,

    @SerialName("discountApplicable")
    val isDiscountApplicable: Boolean? = null
) : Parcelable