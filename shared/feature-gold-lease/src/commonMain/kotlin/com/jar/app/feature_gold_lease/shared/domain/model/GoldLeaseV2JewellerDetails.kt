package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2JewellerDetails(
    @SerialName("jewellerName")
    val jewellerName: String? = null,

    @SerialName("jewellerIcon")
    val jewellerIcon: String? = null,

    @SerialName("establishedText")
    val establishedText: String? = null,

    @SerialName("jewellerSummary")
    val jewellerSummary: List<GoldLeaseV2TitleValuePair>? = null,

    @SerialName("jewellerTitle")
    val jewellerTitle: String? = null,

    @SerialName("jewellerDescription")
    val jewellerDescription: String? = null,

    @SerialName("ctaText")
    val ctaText: String? = null
)