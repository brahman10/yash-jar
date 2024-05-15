package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import dev.icerock.moko.resources.ColorResource
import kotlinx.serialization.SerialName

data class SeparatorPaymentPageItem(
    @SerialName("bgColor")
    val bgColor: ColorResource,

    @SerialName("position")
    val position: Int
) : BasePaymentPageItem {
    override val uniqueId: String
        get() = bgColor.toString().plus(position)
}