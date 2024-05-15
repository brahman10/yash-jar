package com.jar.app.feature_transaction_common

import com.jar.app.feature_transactions_common.shared.TransactionCategory

fun TransactionCategory.getIcon(): Int {
    return when (this) {
        TransactionCategory.Bills -> R.drawable.feature_transaction_ic_bill
        TransactionCategory.Household -> R.drawable.feature_transaction_ic_household
        TransactionCategory.Shopping -> R.drawable.feature_transaction_ic_shopping
        TransactionCategory.Kids -> R.drawable.feature_transaction_ic_shopping
        TransactionCategory.Health -> R.drawable.feature_transaction_ic_health
        TransactionCategory.Education -> R.drawable.feature_transaction_ic_education
        TransactionCategory.Leisure -> R.drawable.feature_transaction_ic_leisure
        TransactionCategory.Groceries -> R.drawable.feature_transaction_ic_groceries
        TransactionCategory.Grooming -> R.drawable.feature_transaction_ic_grooming
        TransactionCategory.Travel -> R.drawable.feature_transaction_ic_travel
        TransactionCategory.Fuel -> R.drawable.feature_transaction_ic_fuel
        TransactionCategory.Office -> R.drawable.feature_transaction_ic_office
        TransactionCategory.House_Rent -> R.drawable.feature_transaction_ic_house_rent
        TransactionCategory.EMI -> R.drawable.feature_transaction_ic_emi
        TransactionCategory.Investment -> R.drawable.feature_transaction_ic_investment
        TransactionCategory.DAILY_SAVINGS -> com.jar.app.core_ui.R.drawable.ic_daily_saving
        TransactionCategory.Misc -> R.drawable.feature_transaction_ic_misc
        TransactionCategory.SCHEDULED -> R.drawable.feature_transaction_ic_misc
        TransactionCategory.UserPayment -> com.jar.app.core_ui.R.drawable.ic_single_gold_coin
        TransactionCategory.ONE_TIME_PURCHASE -> R.drawable.feature_transaction_ic_gold_brick
        TransactionCategory.PARTNER_BONUS -> R.drawable.feature_transaction_ic_gift_box_colored
        TransactionCategory.DEFAULT -> R.drawable.feature_transaction_ic_misc
    }
}