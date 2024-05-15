package com.jar.app.feature.home.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PopupMetaData(
    @SerialName("popupData")
    val popupData: String? = null
)
