package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.SerialName

data class DescriptionPaymentPageItem(
    @SerialName("description")
    val description: StringResource,

    @SerialName("icon")
    val icon: Int? = null,
) : BasePaymentPageItem {
    override val uniqueId: String
        get() = description.toString().plus(icon)
}