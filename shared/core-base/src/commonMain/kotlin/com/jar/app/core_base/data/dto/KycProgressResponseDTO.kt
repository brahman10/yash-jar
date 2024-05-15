package com.jar.app.core_base.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class KycProgressResponseDTO(
    @SerialName("kycProgress")
    val kycProgress: KycProgressDTO? = null,

    @SerialName("kycverified")
    val kycVerified: Boolean
)

@kotlinx.serialization.Serializable
data class KycProgressDTO(
    @SerialName("AADHAAR")
    val aadhaar: AadhaarDTO? = null,

    @SerialName("EMAIL")
    val email: EmailDTO? = null,

    @SerialName("PAN")
    val pan: PanDTO? = null,

    @SerialName("SELFIE")
    val selfie: SelfieDTO? = null
)

@kotlinx.serialization.Serializable
data class AadhaarDTO(
    @SerialName("aadhaarNo")
    val aadhaarNo: String? = null,

    @SerialName("requestedAt")
    val requestedAt: String? = null,

    @SerialName("status")
    val status: String,

    @SerialName("dob")
    val dob: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("validityInSeconds")
    val validityInSeconds: Long? = null,

    @SerialName("resentOTPInSeconds")
    val resentOTPInSeconds: Long? = null,

    @SerialName("verifiedAt")
    val verifiedAt: Long? = null
)

@kotlinx.serialization.Serializable
data class EmailDTO(
    @SerialName("email")
    val email: String? = null,

    @SerialName("requestedAt")
    val requestedAt: String? = null,

    @SerialName("status")
    val status: String,

    @SerialName("validityInSeconds")
    val validityInSeconds: Long? = null,

    @SerialName("resentOTPInSeconds")
    val resentOTPInSeconds: Long? = null,

    @SerialName("verifiedAt")
    val verifiedAt: String? = null
)

@kotlinx.serialization.Serializable
data class PanDTO(

    @SerialName("panNo")
    val panNo: String? = null,

    @SerialName("firstName")
    val firstName: String? = null,

    @SerialName("lastName")
    val lastName: String? = null,

    @SerialName("dob")
    val dob: String? = null,

    @SerialName("jarVerifiedPAN")
    val jarVerifiedPAN: Boolean? = null,

    @SerialName("requestedAt")
    val requestedAt: String? = null,

    @SerialName("status")
    val status: String,

    @SerialName("resentOTPInSeconds")
    val resentOTPInSeconds: Long? = null,

    @SerialName("validityInSeconds")
    val validityInSeconds: Long? = null,

    @SerialName("verifiedAt")
    val verifiedAt: Long? = null
)

@kotlinx.serialization.Serializable
data class SelfieDTO(
    @SerialName("status")
    val status: String
)