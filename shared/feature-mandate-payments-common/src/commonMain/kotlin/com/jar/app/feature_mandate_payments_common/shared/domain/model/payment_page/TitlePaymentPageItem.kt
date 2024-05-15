package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.SerialName

data class TitlePaymentPageItem(
    @SerialName("title")
    val title: StringResource? = null,

    @SerialName("titleString")
    val titleString: String? = null,

    @SerialName("textSize")
    val textSize: Int? = null,

    @SerialName("bgColor")
    val bgColor: ColorResource? = null
) : BasePaymentPageItem {
    override val uniqueId: String
        get() = title?.toString()?.plus(titleString)?.plus(textSize)?.plus(bgColor?.toString()).orEmpty()
}