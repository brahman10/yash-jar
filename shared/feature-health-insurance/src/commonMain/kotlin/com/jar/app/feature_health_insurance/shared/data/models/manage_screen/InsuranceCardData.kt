package com.jar.app.feature_health_insurance.shared.data.models.manage_screen

import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.Attribute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InsuranceCardData(
    @SerialName("headerText") val headerText: String? = null,
    @SerialName("headerValue") val headerValue: String? = null,
    @SerialName("headerIcon") val headerIcon: String? = null,
    @SerialName("subHeaderText") val subHeaderText: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusColor") val statusColor: String? = null,
    @SerialName("statusIcon") val statusIcon: String? = null,
    @SerialName("communication") val communication: List<Communication>? = null,
    @SerialName("viewBenefits") val viewBenefits: ViewBenefits? = null,
    @SerialName("data") val data: List<Attribute>? = null,
    @SerialName("footer") val footer: List<TextData>? = null,
    @SerialName("txnStatus") val txnStatus: InsuranceTransactionStatus? = null,

    )

@Serializable
data class InsuranceTransactionStatus(
    @SerialName("text") val text: String,
    @SerialName("backgroundColor") val backgroundColor: String,
    @SerialName("textColor") val textColor: String
)


