package com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpendsTrackerResponseSummary(
    @SerialName("balance")
    val balance: String,
    @SerialName("balanceText")
    val balanceText: String,
    @SerialName("monthName")
    val monthName: String? = null ,
    @SerialName("spends")
    val spends: String,
    @SerialName("spendsPrompt")
    val spendsPrompt: String,
    @SerialName("spendsPromptIcon")
    val spendsPromptIcon: String,
    @SerialName("spendsText")
    val spendsText: String,
    @SerialName("summaryTitle")
    val summaryTitle: String,
    @SerialName("balanceIcon")
    val balanceIcon: String,
    @SerialName("spendsIcon")
    val spendsIcon: String,

)