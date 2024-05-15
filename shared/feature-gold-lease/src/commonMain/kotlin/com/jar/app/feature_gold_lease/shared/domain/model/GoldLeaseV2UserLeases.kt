package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2UserLeases(
    @SerialName("userLeasesList")
    val userLeasesList: List<GoldLeaseV2UserLeaseItem>? = null
)

@kotlinx.serialization.Serializable
data class GoldLeaseV2UserLeaseItem(
    @SerialName("jewellerName")
    val jewellerName: String? = null,

    @SerialName("jewellerIcon")
    val jewellerIcon: String? = null,

    @SerialName("leaseStatus")
    private val leaseStatus: String? = null,

    @SerialName("leasedGoldComponent")
    val leasedGoldComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("earningsPercentageComponent")
    val earningsPercentageComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("earningsTillDateComponent")
    val earningsTillDateComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("extraInformationComponent")
    val extraInformationComponent: LeaseIconAndDescriptionComponent? = null,

    @SerialName("jarBonusPercentage")
    val jarBonusPercentage: Float? = null,

    @SerialName("leaseId")
    val leaseId: String? = null,

    @SerialName("cosmetics")
    private val cosmetics: String? = null,

    @SerialName("header")
    val header: String? = null,

    @SerialName("userLeasesType")
    val userLeasesType: String? = null
) {
    fun getUserLeaseStatus() = UserLeaseStatus.values().find { it.name == leaseStatus }

    fun getUserLeaseCosmetics() = UserLeaseCosmetics.values().find { it.name == cosmetics }
}

@kotlinx.serialization.Serializable
data class LeaseIconAndDescriptionComponent(
    @SerialName("iconLink")
    val iconLink: String? = null,

    @SerialName("description")
    val description: String? = null
)

enum class UserLeaseCosmetics {
    NONE,
    TOP_HALF_BLUR
}

enum class UserLeaseStatus {
    ACTIVE,
    IN_PROGRESS,
    CANCELLED,
    CLOSED,
    COMPLETED,
    FAILED
}