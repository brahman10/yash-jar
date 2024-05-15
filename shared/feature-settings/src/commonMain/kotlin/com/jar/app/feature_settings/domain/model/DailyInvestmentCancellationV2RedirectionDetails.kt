package com.jar.app.feature_settings.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentCancellationV2RedirectionDetails(
    @SerialName("deeplink")
    val deeplink: String?,
    @SerialName("version")
    val version: String?,
    @SerialName("experiment")
    val experiment: Boolean?,
    @SerialName("firstSetup")
    val isFirstSetup: Boolean?
)