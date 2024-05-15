package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ConsentDto(
    @SerialName("consentText")
    val consentText: String? = null,
    //For ui purpose
    @SerialName("isSelected")
    var isSelected: Boolean = false
)