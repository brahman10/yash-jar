package com.jar.app.feature_vasooli.impl.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class VasooliEntryRequest(
    @SerialName("borrowerName")
    val borrowerName: String? = null,

    @SerialName("borrowerPhoneNo")
    val borrowerPhoneNo: String? = null,

    @SerialName("borrowerCountryCode")
    val borrowerCountryCode: String? = null,

    @SerialName("amount")
    val amount: Int? = null,

    @SerialName("lentOn")
    val lentOn: Long? = null,

    @SerialName("dueOn")
    val dueOn: Long? = null
): Parcelable