package com.jar.app.feature_gold_lease.shared.domain.model

import com.jar.app.feature_user_api.domain.model.SuggestedAmount
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2GoldOptions(
    @SerialName("leasedQuantityTitle")
    val leasedQuantityTitle: String? = null,

    @SerialName("goldEarningsTitle")
    val goldEarningsTitle: String? = null,

    @SerialName("useJarSavingsPrompt")
    val useJarSavingsPrompt: String? = null,

    @SerialName("amountPayableText")
    val amountPayableText: String? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("leaseGoldOptionsAmountList")
    val leaseGoldOptionsAmountList: List<SuggestedAmount>? = null,

    @SerialName("leaseGoldOptionsVolumeList")
    val leaseGoldOptionsVolumeList: List<SuggestedAmount>? = null,

    @SerialName("prefillVolume")
    val prefillVolume: Float? = null,

    @SerialName("lockInPeriodText")
    val lockInPeriodText: String
)