package com.jar.app.feature_health_insurance.shared.data.models.transaction_details


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusData(
    @SerialName("date")
    val date: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("label")
    val label: String,
    @SerialName("status")
    val status: String? = null
)