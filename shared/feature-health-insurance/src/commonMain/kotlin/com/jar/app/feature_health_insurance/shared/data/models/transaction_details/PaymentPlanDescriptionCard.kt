package com.jar.app.feature_health_insurance.shared.data.models.transaction_details


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentPlanDescriptionCard(
    @SerialName("headerIcon")
    val headerIcon: String,
    @SerialName("statusIcon")
    val statusIcon: String,
    @SerialName("headerLabelText")
    val headerLabelText: String? = null,
    @SerialName("headerValueText")
    val headerValueText: String? = null,
    @SerialName("subheaderLabelText")
    val subHeaderLabelText: String? = null,
    @SerialName("subheaderValueText")
    val subHeaderValueText: String? = null
)