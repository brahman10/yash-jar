package com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help

import kotlinx.serialization.SerialName
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.MandateEducationPageItem

@kotlinx.serialization.Serializable
data class MandateEducationResp(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("mandateEducation")
    val mandateEducationPageItem: MandateEducationPageItem
)