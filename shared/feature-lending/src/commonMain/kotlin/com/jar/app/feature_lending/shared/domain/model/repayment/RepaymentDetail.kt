package com.jar.app.feature_lending.shared.domain.model.repayment


import com.jar.app.feature_lending.shared.domain.model.v2.KeyValueData
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RepaymentDetail(
    @SerialName("emiTitle")
    val emiCount: String? = null,
    @SerialName("emiSummary")
    val emiSummary: List<KeyValueData>? = null,
    @SerialName("Schedule")
    val schedule: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("statusText")
    val statusText: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("paymentStatus")
    val paymentStatus: String? = null,
    @SerialName("foreclosureOrderId")
    val foreclosureOrderId: String? = null,
    @SerialName("cardDisabled")
    val isCardDisabled: Boolean? = null,
    @SerialName("foreclosureCard")
    val isForeclosureCard: Boolean? = null,
)