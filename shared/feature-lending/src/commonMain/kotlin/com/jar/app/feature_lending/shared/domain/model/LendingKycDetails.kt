package com.jar.app.feature_lending.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LendingKycDetails(
    @SerialName("AADHAAR")
    val AADHAAR: LendingKycAadhaar,

    @SerialName("EMAIL")
    val EMAIL: LendingKycEmail,

    @SerialName("PAN")
    val PAN: LendingKycPan,
): Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class LendingKycAadhaar(
    @SerialName("aadhaarNo")
    val aadhaarNo: String,

    @SerialName("dob")
    val dob: String,

    @SerialName("name")
    val name: String,
): Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class LendingKycEmail(
    @SerialName("emailId")
    val emailId: String,
): Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class LendingKycPan(
    @SerialName("dob")
    val dob: String,

    @SerialName("firstName")
    val firstName: String,

    @SerialName("lastName")
    val lastName: String,

    @SerialName("panNo")
    val panNo: String
):Parcelable

