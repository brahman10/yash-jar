package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseTermsAndConditions(
    @SerialName("termsAndConditions")
    val termsAndConditions: String
)