package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FilterValueData(
    @SerialName("name")
    val name: String,

    //Parent Key name
    @SerialName("keyName")
    val keyName: String,

    @SerialName("displayName")
    val displayName: String,

    //For UI purpose
    @SerialName("isSelected")
    var isSelected: Boolean? = false,

    //For custom date only
    @SerialName("startDate")
    var startDate: Long? = null,

    @SerialName("endDate")
    var endDate: Long? = null
)
