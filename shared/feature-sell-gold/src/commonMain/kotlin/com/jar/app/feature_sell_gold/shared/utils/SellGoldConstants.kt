package com.jar.app.feature_sell_gold.shared.utils

object SellGoldConstants {
    const val WITHDRAWAL_TYPE_GOLD = "GOLD"
    const val INSTRUMENT_TYPE_GOLD = "GOLD"
    const val WITHDRAWAL_TYPE_AMOUNT = "AMOUNT"
    const val WITHDRAWAL_TYPE_UPI = "UPI"

    object WithdrawalErrorCodes {
        const val VPA_USERNAME_MISMATCH = "101"
        const val VPA_INCORRECT = "102"
    }


    internal object Endpoints {
        const val POST_WITHDRAWAL_REQUEST = "v3/api/payouts/vpaTransfer"
        const val FETCH_GOLD_SELL_OPTIONS = "v2/api/gold/sellOptions"
        const val FETCH_STATIC_CONTENT = "v2/api/dashboard/static"
        const val FETCH_WITHDRAWAL_STATUS = "v2/api/payouts/status"
        const val POST_WITHDRAWAL_REASON = "v2/api/payouts/update/withdraw/reason"
        const val POST_TRANSACTION_ACTION = "v2/api/transactions/action"
        const val FETCH_WITHDRAWAL_HELP_DATA = "v2/api/dashboard/static"
        const val GET_DRAWER_DETAILS = "/v1/api/sellGold/drawerDetails"
        const val GET_KYC_ACTION = "/v1/api/sellGold/kycAction"
    }
}