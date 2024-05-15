package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2MyOrders(
    @SerialName("leasedGoldComponent")
    val leasedGoldComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("earnedGoldComponent")
    val earnedGoldComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("subtitleComponent")
    val subtitleComponent: LeaseIconAndDescriptionComponent? = null,

    @SerialName("mainTitle")
    val mainTitle: String? = null,

    @SerialName("totalEarnings")
    val totalEarnings: Float? = null,

    @SerialName("todayEarnings")
    val todayEarnings: String? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("ongoingLeaseCount")
    val ongoingLeaseCount: Int? = null
)