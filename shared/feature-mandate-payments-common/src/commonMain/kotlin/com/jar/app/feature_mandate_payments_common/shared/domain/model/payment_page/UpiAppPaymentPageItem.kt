package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpiAppPaymentPageItem(
    @SerialName("upiAppPackageName") val upiAppPackageName: String,

    @SerialName("isSelected") var isSelected: Boolean
) : BasePaymentPageItem {
    override val uniqueId: String
        get() = upiAppPackageName.plus(isSelected)
}