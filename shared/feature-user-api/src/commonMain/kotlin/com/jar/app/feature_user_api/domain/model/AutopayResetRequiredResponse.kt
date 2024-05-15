package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AutopayResetRequiredResponse(
    @SerialName("resetRequired")
    val isResetRequired: Boolean,
    @SerialName("currentMandateAmount")
    val mandateAmount: Float? = null,
    @SerialName("newMandateAmount")
    val newMandateAmount: Float? = null,
    @SerialName("isAutopaySetup")
    val isAutopaySetup: Boolean? = null,
    @SerialName("authWorkflowType")
    val authWorkflowType: String? = null
) {
    fun getFinalMandateAmount() = newMandateAmount ?: mandateAmount ?: 0f
}