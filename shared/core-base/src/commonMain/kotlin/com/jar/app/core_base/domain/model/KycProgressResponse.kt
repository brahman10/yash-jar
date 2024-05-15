package com.jar.app.core_base.domain.model

@kotlinx.serialization.Serializable
data class KycProgressResponse(
    val kycProgress: KycProgress?,

    val kycVerified: Boolean
)

@kotlinx.serialization.Serializable
data class KycProgress(
    val AADHAAR: AADHAAR?,

    val EMAIL: EMAIL?,

    val PAN: PAN?,

    val SELFIE: SELFIE?
)

@kotlinx.serialization.Serializable
data class AADHAAR(
    val aadhaarNo: String?,

    val requestedAt: String?,

    val status: String,

    val dob: String?,

    val name: String?,

    val validityInSeconds: Long?,

    val resentOTPInSeconds: Long?,

    val verifiedAt: Long?
) {
    fun isEmpty() = name.isNullOrEmpty()
}

@kotlinx.serialization.Serializable
data class EMAIL(
    val email: String?,

    val requestedAt: String?,

    val status: String,

    val validityInSeconds: Long?,

    val resentOTPInSeconds: Long?,

    val verifiedAt: String?
)

@kotlinx.serialization.Serializable
data class PAN(
    val panNo: String?,

    val firstName: String?,

    val lastName: String?,

    val dob: String?,

    val jarVerifiedPAN: Boolean?,

    val requestedAt: String?,

    val status: String,

    val resentOTPInSeconds: Long?,

    val validityInSeconds: Long?,

    val verifiedAt: Long?
) {
    fun getPrintableName() = firstName.orEmpty() + lastName.orEmpty()
}

@kotlinx.serialization.Serializable
data class SELFIE(
    val status: String
)

enum class KycEmailAndAadhaarProgressStatus {
    CKYC_AADHAAR,
    VERIFIED,
    OTP_SENT,
    EXPIRED,
    FAILED,
    OTP_VERIFIED,
    INPROGRESS
}

enum class KycPANProgressStatus {
    VERIFIED,
    OTP_SENT,
    OTP_VERIFIED,
    FAILED,
    RETRY_LIMIT_EXHAUSTED,
    RETRY_LIMIT_EXCEEDED
}

enum class KycSelfieProgressStatus {
    VERIFIED,
    FAILED,
    RETRY_LIMIT_EXHAUSTED,
    RETRY_LIMIT_EXCEEDED
}