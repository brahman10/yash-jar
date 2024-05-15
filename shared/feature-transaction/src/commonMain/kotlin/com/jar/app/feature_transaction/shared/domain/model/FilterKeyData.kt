package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FilterKeyData(
    @SerialName("name")
    val name: String,

    @SerialName("displayName")
    val displayName: String,

    //For UI purpose
    @SerialName("isSelected")
    var isSelected: Boolean? = false
)
