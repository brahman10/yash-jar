package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BankAccount(
    @SerialName("accountHolderName")
    val accountHolderName: String? = null,
    @SerialName("accountNumber")
    val accountNumber: String? = null,
    @SerialName("accountType")
    val accountType: String? = null,
    @SerialName("bankName")
    val bankName: String? = null,
    @SerialName("ifsc")
    val ifsc: String? = null,
    @SerialName("bankLogo")
    val bankLogo: String? = null
)