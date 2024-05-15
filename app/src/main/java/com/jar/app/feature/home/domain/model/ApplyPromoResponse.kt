package com.jar.app.feature.home.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ApplyPromoResponse(
    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("amount")
    val amount: Double? = null,

    @SerialName("promocode")
    val promoCode: String? = null
) : Parcelable