package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureRedirectionResp(
    @SerialName("deepLink")
    val deeplink: String? = null
)
