package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PreferredBankPageItem(
    @SerialName("title")
    val title: String? = null,

    @SerialName("bankName")
    val bankName: String? = null,

    @SerialName("bankIconLink")
    val bankIconLink: String? = null
) : BasePaymentPageItem {
    override val uniqueId: String
        get() = title.orEmpty()
}