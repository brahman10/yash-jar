package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceTransactionData(
    @SerialName("amount")
    val amount: String,
    @SerialName("date")
    val date: String,
    @SerialName("header")
    val header: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("id")
    val id: String,
    @SerialName("insuranceId")
    val insuranceId: String,
    @SerialName("status")
    val status: String,
    @SerialName("statusText")
    val statusText: String,
    @SerialName("statusIcon")
    val statusIcon: String
)