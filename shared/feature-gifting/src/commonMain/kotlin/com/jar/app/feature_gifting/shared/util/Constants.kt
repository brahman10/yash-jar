package com.jar.app.feature_gifting.shared.util

object Constants {

    const val EXTRA_MESSAGE = "EXTRA_MESSAGE"

    const val MIN_GOLD_AMOUNT = 10f

    object GiftCardOrder {
        const val WHOM_TO_SEND = 0
        const val RECEIVER_DETAIL = 1
        const val HOW_MUCH_TO_SEND = 2
        const val AMOUNT_DETAIL = 3
    }

    object SuggestedAmountUnit {
        const val UNIT_GM = "gm"
        const val UNIT_RS = "rs"
    }

    internal object Endpoints {
        const val SEND_GOLD_GIFT = "v1/api/gift/validateAndSend"
        const val FETCH_RECEIVED_GIFTS = "v1/api/gift/received/all"
        const val MARK_RECEIVED_GIFT_VIEWED = "v1/api/gift/received/viewed"
        const val FETCH_GOLD_GIFT_OPTIONS = "v2/api/dashboard/static"
    }
}