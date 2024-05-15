package com.jar.app.feature_lending.shared.domain.model.v2


import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BankIfscResponseV2(
    @SerialName("ADDRESS")
    val ADDRESS: String? = null,

    @SerialName("BANK")
    val BANK: String? = null,

    @SerialName("BANKCODE")
    val BANKCODE: String? = null,

    @SerialName("BRANCH")
    val BRANCH: String? = null,

    @SerialName("CITY")
    val CITY: String? = null,

    @SerialName("IFSC")
    val IFSC: String? = null,

    @SerialName("bankLogo")
    val bankLogo: String? = null
)