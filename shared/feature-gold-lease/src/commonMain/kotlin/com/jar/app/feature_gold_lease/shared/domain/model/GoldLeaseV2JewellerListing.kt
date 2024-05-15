package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2JewellerListing(
    @SerialName("title")
    val title: String? = null,

    @SerialName("jewellerIcons")
    val jewellerIcons: List<String>? = null,

    @SerialName("socialProofText")
    val socialProofText: String? = null,

    @SerialName("socialProofIcon")
    val socialProofIcon: String? = null
)