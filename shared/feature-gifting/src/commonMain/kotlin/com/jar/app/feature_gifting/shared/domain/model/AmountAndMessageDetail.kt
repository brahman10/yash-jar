package com.jar.app.feature_gifting.shared.domain.model

import com.jar.app.feature_gifting.shared.util.Constants

data class AmountAndMessageDetail(
    val amountInRupees: Float,
    val volumeInGm: Float,
    val message: String?
) : GiftView {

    override fun getOrder(): Int {
        return Constants.GiftCardOrder.AMOUNT_DETAIL
    }

}