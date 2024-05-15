package com.jar.app.feature_lending.shared.domain.model.mandate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MandateSetupFailureContent(
    @SerialName("iconUrl")
    val iconUrl: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("isDebitCardEnabled")
    val isDebitCardEnabled: Boolean = false,
    @SerialName("debitCardIconUrl")
    val debitCardIconUrl: String? = null,
    @SerialName("debitCardAlertMessage")
    val debitCardAlertMessage: String? = null,
    @SerialName("debitCardDisplayText")
    val debitCardDisplayText: String? = null,
    @SerialName("isNetBankingEnabled")
    val isNetBankingEnabled: Boolean = false,
    @SerialName("netBankingIconUrl")
    val netBankingIconUrl: String? = null,
    @SerialName("netBankingAlertMessage")
    val netBankingAlertMessage: String? = null,
    @SerialName("netBankingDisplayText")
    val netBankingDisplayText: String? = null
)