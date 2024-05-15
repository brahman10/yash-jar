package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DrawdownDetails(
    @SerialName("emiAmount")
    val emiAmount: Float? = null,
    @SerialName("firstEMIDate")
    val firstEMIDate: String? = null,//dd MMM yyyy
    @SerialName("lastEMIDate")
    val lastEMIDate: String? = null,//dd MMM yyyy
    @SerialName("roi")
    val roi: Float? = null,
    @SerialName("tenure")
    val tenure: Int? = null,
    @SerialName("totalAmount")
    val totalAmount: Float? = null,
    @SerialName("totalRepaymentAmount")
    val totalRepaymentAmount: Float? = null
)