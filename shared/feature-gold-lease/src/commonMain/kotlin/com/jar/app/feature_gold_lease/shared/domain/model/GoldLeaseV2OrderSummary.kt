package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2OrderSummary(
    @SerialName("leaseQuantityTitle")
    val leaseQuantityTitle: String? = null,

    @SerialName("goldEarningsTitle")
    val goldEarningsTitle: String? = null,

    @SerialName("lockInTitle")
    val lockInTitle: String? = null,

    @SerialName("leaseActivationDelayText")
    val leaseActivationDelayText: String? = null,

    @SerialName("jarSavingsUsedTitle")
    val jarSavingsUsedTitle: String? = null,

    @SerialName("goldPurchasedTitle")
    val goldPurchasedTitle: String? = null,

    @SerialName("amountPayableTitle")
    val amountPayableTitle: String? = null,

    @SerialName("kycVerificationRequired")
    val kycVerificationRequired: Boolean? = null,

    @SerialName("emailRequired")
    val emailRequired: Boolean? = null,

    @SerialName("kycPendingDescription")
    val kycPendingDescription: String? = null,

    @SerialName("kycVerifiedTitle")
    val kycVerifiedTitle: String? = null,

    @SerialName("leaseActivationDelayIcon")
    val leaseActivationDelayIcon: String? = null
)