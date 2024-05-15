package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2Details(
    @SerialName("jewellerName")
    val jewellerName: String? = null,

    @SerialName("jewellerIcon")
    val jewellerIcon: String? = null,

    @SerialName("jewellerEstablished")
    val jewellerEstablished: String? = null,

    @SerialName("jewellerId")
    val jewellerId: String? = null,

    @SerialName("leasedGoldComponent")
    val leasedGoldComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("earningsTillDateComponent")
    val earningsTillDateComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("jarGoldUsedComponent")
    val jarGoldUsedComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("currentMonthEarningsComponent")
    val currentMonthEarningsComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("goldPurchasedComponent")
    val goldPurchasedComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("earningsCreditedComponent")
    val earningsCreditedComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("userLeaseStatus")
    private val userLeaseStatus: String? = null,

    @SerialName("userCommunicationComponent")
    val userCommunicationComponent: LeaseIconAndDescriptionComponent? = null,

    @SerialName("leaseOrderDetailsInformationComponent")
    val leaseOrderDetailsInformationComponent: LeaseOrderDetailsInformationComponent? = null,

    @SerialName("leaseOrderDetailsAgreementComponent")
    val leaseOrderDetailsAgreementComponent: LeaseOrderDetailsAgreementComponent? = null,

    @SerialName("whatsappMessage")
    val whatsappMessage: String? = null,

    @SerialName("earningsPercentage")
    val earningsPercentage: Float? = null,

    @SerialName("lockInDays")
    val lockInDays: Int? = null,

    @SerialName("startDate")
    val startDate: String? = null,

    @SerialName("endDate")
    val endDate: String? = null
) {
    fun getUserLeaseStatus() = UserLeaseStatus.values().find { it.name == userLeaseStatus }
}

@kotlinx.serialization.Serializable
data class LeaseOrderDetailsAgreementComponent(
    @SerialName("title")
    val title: String? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("agreementLink")
    val agreementLink: String? = null
)

@kotlinx.serialization.Serializable
data class LeaseOrderDetailsInformationComponent(
    @SerialName("title")
    val title: String,

    @SerialName("jarBonusPercentage")
    val jarBonusPercentage: Float? = null,

    @SerialName("earningsPercentageComponent")
    val earningsPercentageComponent: GoldLeaseV2TitleValuePair? = null,

    @SerialName("leaseOrderInformationComponentValuesList")
    val leaseOrderInformationComponentValuesList: List<GoldLeaseV2TitleValuePair>? = null
)