package com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class InitiateMandatePaymentApiRequest(
    @SerialName("provider")
    val provider: String,

    @SerialName("mandateAmount")
    val mandateAmount: Float,

    @SerialName("authWorkflowType")
    val authWorkflowType: String,

    @SerialName("packageName")
    val packageName: String,

    @SerialName("phonePeVersionCode")
    val phonePeVersionCode: String? = null,

    @SerialName("insuranceId")
    val insuranceId: String? = null,

    @SerialName("subscriptionType")
    val subscriptionType: String? = null,

    @SerialName("subsSetupType")
    val subsSetupType: String? = null,

    @SerialName("goalId")
    val goalId: String? = null,

    @SerialName("couponCodeId")
    val couponCodeId: String? = null,
)