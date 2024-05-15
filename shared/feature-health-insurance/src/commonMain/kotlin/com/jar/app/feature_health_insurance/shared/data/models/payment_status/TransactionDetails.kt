package com.jar.app.feature_health_insurance.shared.data.models.payment_status


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDetails(
    @SerialName("title")
    val title: String?= null,
    @SerialName("data")
    val data: List<Attribute>? = null
)