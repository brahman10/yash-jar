package com.jar.app.feature_gifting.shared.domain.model

import com.jar.app.feature_gifting.shared.util.Constants

data class ReceiverDetail(
    val name: String,
    val number: String
) : GiftView {

    override fun getOrder(): Int {
        return Constants.GiftCardOrder.RECEIVER_DETAIL
    }

}