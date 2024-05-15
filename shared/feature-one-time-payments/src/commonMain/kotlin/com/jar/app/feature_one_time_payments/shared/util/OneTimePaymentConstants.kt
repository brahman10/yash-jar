package com.jar.app.feature_one_time_payments.shared.util

object OneTimePaymentConstants {

    object JuspayAction {
        const val GET_PAYMENT_METHODS = "getPaymentMethods"
        const val UPI_TXN = "upiTxn"
        const val CARD_INFO = "cardInfo"
        const val CARD_TXN = "cardTxn"
        const val CARD_LIST = "cardList"
    }

    const val GOLD_PRICE_CHANGED_ACTION = "GOLD_PRICE_CHANGED_ACTION"
    const val CANCEL_TRANSACTION_ACTION = "CANCEL_TRANSACTION_ACTION"

    object GoldPriceChangedAction {
        const val CONTINUE_PAYMENT = 1
        const val CANCEL_PAYMENT = 2
    }

    object CancelTransactionAction {
        const val CONTINUE_PAYMENT = 1
        const val CANCEL_PAYMENT = 2
    }

    internal object Endpoints {
        const val FETCH_MANUAL_PAYMENT_STATUS = "v2/api/payments/status"
        const val VERIFY_VPA = "v2/api/payments/verify-vpa"
        const val INITIATE_UPI_COLLECT = "v2/api/payments/upi-collect"
        const val RETRY_PAYMENT = "v2/api/payments/retry"
        const val CANCEL_PAYMENT = "v2/api/payments/cancel"
        const val FETCH_RECENTLY_USED_PAYMENT_METHODS = "v2/api/payments/recently-used"
        const val FETCH_SAVED_VPA = "v2/api/payments/savedUpiIds"
        const val FETCH_ENABLED_PAYMENT_METHODS = "v2/api/paymentMethods/enabled"
        const val FETCH_ORDER_STATUS_DYNAMIC_CARDS = "v1/api/features/order/status"
    }

}