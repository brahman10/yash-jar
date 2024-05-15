package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class OneTimeInvestOrderDetails(

    @SerialName("name")
    val name: String,

    @SerialName("phoneNumber")
    val phoneNumber: String,

    @SerialName("goldVolume")
    val goldVolume: String,

    @SerialName("ratePerGm")
    val ratePerGm: String,

    @SerialName("goldAmt")
    val goldAmt: Double,

    @SerialName("gstAmt")
    val gstAmt: Double,

    @SerialName("totalAmt")
    val totalAmt: Double,

    @SerialName("auspiciousTime")
    val auspiciousTime: Boolean? = null,

    @SerialName("auspiciousStartTime")
    val auspiciousStartTime: String? = null,

    @SerialName("auspiciousEndTime")
    val auspiciousEndTime: String? = null,

    @SerialName("invoiceLink")
    val invoiceLink: String? = null,

    @SerialName("shareImageUrl")
    val shareImageUrl: String? = null,

    @SerialName("shareText")
    val shareText: String? = null
) : Parcelable