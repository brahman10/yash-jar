package com.jar.app.feature_gold_sip.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class EligibleForGoldSipData(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("goldSipHomeCardInfo")
    val eligibleForGoldSip: EligibleForGoldSip
)

@kotlinx.serialization.Serializable
data class EligibleForGoldSip(
    @SerialName("eligible")
    val eligible: Boolean
)