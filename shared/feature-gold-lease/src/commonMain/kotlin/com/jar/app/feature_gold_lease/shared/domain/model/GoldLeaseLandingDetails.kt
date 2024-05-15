package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseLandingDetails(
    @SerialName("primaryTitle")
    val primaryTitle: String? = null,

    @SerialName("primarySubtitle")
    val primarySubtitle: String? = null,

    @SerialName("goldLeaseComparisonTable")
    val goldLeaseComparisonTable: GoldLeaseComparisonTable? = null,

    @SerialName("secondaryTitle")
    val secondaryTitle: String? = null,

    @SerialName("leaseBasicInfoTileList")
    val leaseBasicInfoTileList: List<LeaseBasicInfoTile>? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("socialProofText")
    val socialProofText: String? = null,

    @SerialName("primaryTabTitle")
    val primaryTabTitle: String? = null,

    @SerialName("secondaryTabTitle")
    val secondaryTabTitle: String? = null
)

@kotlinx.serialization.Serializable
data class GoldLeaseComparisonTable(
    @SerialName("socialProofText")
    val socialProofText: String? = null,

    @SerialName("earningsText")
    val earningsText: String? = null,

    @SerialName("leaseComparisonTableRowsList")
    val leaseComparisonTableRowsList: List<LeaseComparisonTableRowsList>? = null
)

@kotlinx.serialization.Serializable
data class LeaseComparisonTableRowsList(
    @SerialName("rowTitle")
    val rowTitle: String? = null,

    @SerialName("leaseGoldValue")
    val leaseGoldValue: String? = null,

    @SerialName("commonGoldValue")
    val commonGoldValue: String? = null,

    @SerialName("rowPlacement")
    val rowPlacement: String? = null
)

@kotlinx.serialization.Serializable
data class LeaseBasicInfoTile(
    @SerialName("iconLink")
    val iconLink: String? = null,

    @SerialName("description")
    val description: String? = null
)

enum class LeaseComparisonRowPlacement {
    UPPER,
    LOWER
}