package com.jar.app.core_base.domain.mapper

import com.jar.app.core_base.data.dto.*
import com.jar.app.core_base.domain.model.*

fun KycProgressResponseDTO.toKycProgressResponse(): KycProgressResponse {
    return KycProgressResponse(
        kycProgress = kycProgress?.toKycProgress(),
        kycVerified = kycVerified
    )
}

fun KycProgressDTO.toKycProgress(): KycProgress {
    return KycProgress(
        AADHAAR = aadhaar?.toAadhaar(),
        EMAIL = email?.toEmail(),
        PAN = pan?.toPan(),
        SELFIE = selfie?.toSelfie()
    )
}

fun AadhaarDTO.toAadhaar(): AADHAAR {
    return AADHAAR(
        aadhaarNo = aadhaarNo,
        requestedAt = requestedAt,
        status = status,
        dob = dob,
        name = name,
        validityInSeconds = validityInSeconds,
        resentOTPInSeconds = resentOTPInSeconds,
        verifiedAt = verifiedAt
    )
}

fun EmailDTO.toEmail(): EMAIL {
    return EMAIL(
        email = email,
        requestedAt = requestedAt,
        status = status,
        validityInSeconds = validityInSeconds,
        resentOTPInSeconds = resentOTPInSeconds,
        verifiedAt = verifiedAt
    )
}

fun PanDTO.toPan(): PAN {
    return PAN(
        panNo = panNo,
        firstName = firstName,
        lastName = lastName,
        dob = dob,
        jarVerifiedPAN = jarVerifiedPAN,
        requestedAt = requestedAt,
        status = status,
        resentOTPInSeconds = resentOTPInSeconds,
        validityInSeconds = validityInSeconds,
        verifiedAt = verifiedAt
    )
}

fun SelfieDTO.toSelfie(): SELFIE {
    return SELFIE(
        status = status
    )
}