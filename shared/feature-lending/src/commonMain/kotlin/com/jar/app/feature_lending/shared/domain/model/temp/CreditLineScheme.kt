package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class CreditLineScheme(
    @SerialName("amountPerMonth")
    val amountPerMonth: Float,

    @SerialName("repaymentAmount")
    val repaymentAmount: Float? = null,

    @SerialName("firstEmiDate")
    val firstEmiDate: String, //dd MMM yyyy

    @SerialName("lastEmiDate")
    val lastEmiDate: String, //dd MMM yyyy

    @SerialName("tenure")
    val tenure: Int,

    @SerialName("recommended")
    var isRecommended: Boolean = false,

    //For UI purpose
    @SerialName("isSelected")
    var isSelected: Boolean = false,
) : Parcelable