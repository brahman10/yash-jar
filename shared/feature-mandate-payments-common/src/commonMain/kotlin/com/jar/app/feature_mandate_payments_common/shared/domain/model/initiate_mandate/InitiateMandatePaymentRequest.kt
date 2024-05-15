package com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class InitiateMandatePaymentRequest(
    @SerialName("mandateAmount")
    val mandateAmount: Float,

    @SerialName("authWorkflowType")
    val authWorkflowType: MandateWorkflowType,

    @SerialName("subscriptionType")
    val subscriptionType: String? = null,

    @SerialName("insuranceId")
    val insuranceId: String? = null,

    @SerialName("subsSetupType")
    val subsSetupType: String? = null, // Setup, Update

    @SerialName("goalId")
    val goalId: String? = null,

    @SerialName("couponCodeId")
    val couponCodeId: String? = null,
)