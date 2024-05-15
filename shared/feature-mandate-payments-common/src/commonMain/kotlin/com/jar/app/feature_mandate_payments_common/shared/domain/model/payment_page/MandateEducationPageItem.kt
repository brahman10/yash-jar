package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help.MandateEducationItem
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class MandateEducationPageItem(
    @SerialName("header")
    val header: String,
    @SerialName("mandateEducationObjectList")
    val mandateEducationList: List<MandateEducationItem>,
    @SerialName("videoUrl")
    val videoUrl: String?,
    @SerialName("expanded")
    val isExpanded: Boolean = false,
    @SerialName("cashbackText")
    val cashbackText: String?,
    @SerialName("knowMoreText")
    val knowMoreText: String?,
    @SerialName("rupeeImage")
    val rupeeImage: String?,
) : BasePaymentPageItem {
    override val uniqueId: String
        get() = header.plus(mandateEducationList.size)
}