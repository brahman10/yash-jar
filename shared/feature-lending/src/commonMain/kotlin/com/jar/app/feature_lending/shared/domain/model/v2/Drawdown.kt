package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Drawdown(
    @SerialName("emiAmount")
    val emiAmount: Float? = null,
    @SerialName("firstEMIDate")
    val firstEMIDate: String? = null,
    @SerialName("lastEMIDate")
    val lastEMIDate: String? = null,
    @SerialName("roi")
    val roi: Float? = null,
    @SerialName("tenure")
    val tenure: Int? = null,
    @SerialName("totalAmount")
    val totalAmount: Int? = null,
    @SerialName("totalRepaymentAmount")
    val totalRepaymentAmount: Float? = null
)