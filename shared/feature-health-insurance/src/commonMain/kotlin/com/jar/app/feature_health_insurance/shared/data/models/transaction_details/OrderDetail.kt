package com.jar.app.feature_health_insurance.shared.data.models.transaction_details


import com.jar.app.feature_health_insurance.shared.data.models.payment_status.Attribute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetail(
    @SerialName("data")
    val orderDetailsInfoList: List<Attribute>,
    @SerialName("notification")
    val notification: Notification? = null,
    @SerialName("title")
    val title: String
)