package com.jar.app.feature.home.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForceUpdateResponse(
    @SerialName("minVersionCode")
    val minVersionCode: Int,
    @SerialName("shouldShowForceUpdate")
    val shouldShowForceUpdate: Boolean
)
