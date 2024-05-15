package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PhoneNumberResponse(
    @SerialName("profilePhoto")
    val profilePhoto: String? = null,

    @SerialName("userName")
    val userName: String? = null,

    @SerialName("phoneNumbers")
    val phoneNumbers: List<String>? = null,

    @SerialName("isExperianConsentNeeded")
    val experianConsent: HashMap<String,Boolean>? = null
)