package com.jar.app.feature_gold_lease.shared.domain.model

import com.jar.app.feature_transactions_common.shared.NewTransactionRoutine
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2StatusResponse(
    @SerialName("title")
    val title: String? = null,

    @SerialName("leasePostOrderHeaderInfo")
    val leasePostOrderHeaderInfo: LeasePostOrderHeaderInfo? = null,

    @SerialName("transactionStatusDetails")
    val transactionStatusDetails: LeaseTransactionStatusDetails? = null,

    @SerialName("postOrderLeaseDetailsCard")
    val postOrderLeaseDetailsCard: LeasePostOrderDetailsCard? = null,

    @SerialName("leasePostOrderDetailsComponent")
    val leasePostOrderDetailsComponent: LeasePostOrderDetailsComponent? = null,

    @SerialName("whatsappMessage")
    val whatsappMessage: String? = null,

    @SerialName("leaseStatus")
    private val leaseStatus: String? = null,

    @SerialName("leaseId")
    val leaseId: String? = null,

    @SerialName("primaryCta")
    val primaryCta: String? = null,

    @SerialName("secondaryCta")
    val secondaryCta: String? = null,

    @SerialName("totalGoldVolume")
    val totalGoldVolume: Float? = null,

    @SerialName("lockerGoldVolume")
    val lockerGoldVolume: Float? = null,

    @SerialName("totalGoldAmount")
    val totalGoldAmount: Float? = null,

    @SerialName("purchasedGoldAmount")
    val purchasedGoldAmount: Float? = null
) {
    fun getLeaseTransactionStatus() = LeaseV2TransactionStatus.values().find { it.name == leaseStatus }
}

enum class LeaseV2TransactionStatus {
    SUCCESS,
    FAILURE,
    PENDING
}

@kotlinx.serialization.Serializable
data class LeasePostOrderDetailsComponent(
    @SerialName("title")
    val title: String? = null,

    @SerialName("leasePostOrderDetailsItemList")
    val leasePostOrderDetailsItemList: List<LeasePostOrderDetailsItemList>? = null
)

@kotlinx.serialization.Serializable
data class LeasePostOrderDetailsItemList(
    @SerialName("title")
    val title: String? = null,

    @SerialName("rowsList")
    val rowsList: List<GoldLeaseV2TitleValuePair>? = null
)

@kotlinx.serialization.Serializable
data class LeasePostOrderDetailsCard(
    @SerialName("title")
    val title: String? = null,

    @SerialName("valueCommonLeaseComponentList")
    val valueCommonLeaseComponentList: List<GoldLeaseV2TitleValuePair>
)

@kotlinx.serialization.Serializable
data class LeasePostOrderHeaderInfo(
    @SerialName("title")
    val title: String? = null,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("volume")
    val volume: String? = null
)

@kotlinx.serialization.Serializable
data class LeaseTransactionStatusDetails(
    @SerialName("title")
    val title: String? = null,

    @SerialName("invoiceLink")
    val invoiceLink: String? = null,

    @SerialName("txnRoutineList")
    val txnRoutineList: List<NewTransactionRoutine>? = null
)