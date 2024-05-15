package com.jar.app.feature_lending_kyc.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class KycAadhaar(
    @SerialName("aadhaarNumber")
    val aadhaarNumber: String? = null,

    @SerialName("dob")
    val dob: String? = null,

    @SerialName("name")
    val name: String? = null,

    val jarVerifiedPAN: Boolean? = null
) : Parcelable {

    fun maskAadhaarNumber(): String {
        val masked = StringBuilder()
        masked.append("**** **** ")
        masked.append(aadhaarNumber?.takeLast(4).orEmpty())
        return masked.toString()
    }
}