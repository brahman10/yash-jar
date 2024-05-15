package com.jar.app.feature_gold_lease.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2PlanList(
    @SerialName("leasePlansList")
    val leasePlansList: List<LeasePlanList>? = null
)

@Parcelize
@kotlinx.serialization.Serializable
data class LeasePlanList(
    @SerialName("planId")
    val planId: String? = null,

    @SerialName("jewellerName")
    val jewellerName: String? = null,

    @SerialName("jewellerIcon")
    val jewellerIcon: String? = null,

    @SerialName("jewellerEstablishedText")
    val jewellerEstablishedText: String? = null,

    @SerialName("jewellerId")
    val jewellerId: String? = null,

    @SerialName("earningsPercentage")
    val earningsPercentage: Float? = null,

    @SerialName("bonusPercentage")
    val bonusPercentage: Float? = null,

    @SerialName("earningsTitle")
    val earningsTitle: String? = null,

    @SerialName("minimumQuantityComponent")
    val minimumQuantityComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("lockInComponent")
    val lockInComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("socialProofComponent")
    val socialProofComponent: SocialProofComponent? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("leasePlanCapacityEnum")
    private val leasePlanCapacityEnum: String? = null,

    @SerialName("leasePlanCapacityDescription")
    val leasePlanCapacityDescription: String? = null,

    @SerialName("leasePlanState")
    private val leasePlanState: String? = null,

    @SerialName("metalLeaseId")
    val metalLeaseId: String? = null,

    @SerialName("maximumLeaseVolume")
    val maximumLeaseVolume: Float? = null
): Parcelable {
    fun getLeasePlanCapacityStatus(): LeasePlanCapacity {
        return LeasePlanCapacity.values().find { it.name == leasePlanCapacityEnum.orEmpty() } ?: LeasePlanCapacity.NO_TAG
    }

    fun getLeasePlanState() = LeasePlanState.values().find { it.name == leasePlanState } ?: LeasePlanState.INACTIVE
}

@Parcelize
@kotlinx.serialization.Serializable
data class SocialProofComponent(
   @SerialName("iconLink")
   val iconLink: String? = null,

   @SerialName("description")
   val description: String? = null
): Parcelable

enum class LeasePlanState {
    ACTIVE,
    INACTIVE
}

enum class LeasePlanCapacity{
    NEW_LAUNCH,
    FILLING_FAST,
    ALMOST_FULL,
    CLOSED,
    NO_TAG //For UI
}