package com.jar.app.feature_health_insurance.shared.data.models.manage_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaData(
    @SerialName("premiumAmt")
    val premiumAmount: Float,
    @SerialName("premiumFreq")
    val premiumFrequency: String,
    @SerialName("policyStartDate")
    val policyStartDate: String,
    @SerialName("policyEndDate")
    val policyEndDate: String,
    @SerialName("premiumDueDate")
    val premiumDueData: String,
    @SerialName("membersInsured")
    val membersInsured: List<String>,
    @SerialName("isOnGracePeriod")
    val isOnGracePeriod: Boolean,
    @SerialName("isTxnSuccessful")
    val isTransactionSuccessful: Boolean,
    @SerialName("planType")
    val planType: String,
    @SerialName("isKycVerified")
    val isKycVerified: Boolean,
)
