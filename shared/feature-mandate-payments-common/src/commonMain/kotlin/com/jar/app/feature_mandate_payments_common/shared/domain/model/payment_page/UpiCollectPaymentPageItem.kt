package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpiCollectPaymentPageItem(
    @SerialName("upiAppsUrl")
    val upiAppsUrl: String,

    @SerialName("errorMessage")
    var errorMessage: String? = null,

    @SerialName("isSelected")
    var isSelected: Boolean = false
) : BasePaymentPageItem {
    override val uniqueId: String
        get() = upiAppsUrl.plus(errorMessage).plus(isSelected)
}