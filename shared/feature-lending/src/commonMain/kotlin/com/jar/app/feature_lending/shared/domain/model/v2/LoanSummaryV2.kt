package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoanSummaryV2(
    @SerialName("readyCashBreakdown")
    val readyCashBreakdown: List<KeyValueData>? = null,
    @SerialName("readyCashBreakdownDescription")
    val readyCashBreakdownDescription: List<QuestionAnswer>? = null,
    @SerialName("readyCashCharges")
    val readyCashCharges: List<KeyValueData>? = null,
    @SerialName("readyCashChargesDescription")
    val readyCashChargesDescription: List<ReadyCashChargesDescription>? = null,
    @SerialName("readyCashDetails")
    val readyCashDetails: List<KeyValueData>? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("withdrawalConsent")
    val withdrawalConsent: String? = null,
    @SerialName("amountToBeCredited")
    val amountToBeCredited: Float? = null
)