package com.jar.app.feature_lending.shared.domain.model.camps_flow

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RealtimeBankData(
    @SerialName("fipId")
    val fipId: String? = null,
    @SerialName("isPrimary")
    val isPrimary: Boolean? = null,
    @SerialName("bankName")
    val bankName: String? = null,
    @SerialName("isDown")
    val isDown: Boolean? = null,
    @SerialName("order")
    val order: Int? = null,
    @SerialName("bankIcon")
    val url: String? = null
)

