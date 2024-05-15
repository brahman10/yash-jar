package com.jar.app.feature_lending.shared.domain.model.repayment


import kotlinx.serialization.SerialName
import com.jar.app.feature_lending.shared.domain.model.v2.KeyValueData
import kotlinx.serialization.Serializable

@Serializable
data class PaymentBreakDownDetails(
    @SerialName("cardHeader")
    val cardHeader: String? = null,
    @SerialName("cardBreakdown")
    val cardBreakdown: List<KeyValueData>? = null
)