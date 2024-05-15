package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ExperianConsent(
    @SerialName("experianConsent")
    val experianTermsAndCondition: ExperianTermsAndCondition
)