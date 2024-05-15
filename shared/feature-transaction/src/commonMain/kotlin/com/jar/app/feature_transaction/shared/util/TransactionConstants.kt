package com.jar.app.feature_transaction.shared.util

object TransactionConstants {

    internal object Endpoints {
        const val FETCH_USER_GOLD_HOLDINGS = "v4/api/transactions/holdingsV2"
        const val FETCH_USER_WINNING_DETAILS = "v4/api/transactions/rewardsDetailsV2"
        const val FETCH_TRANSACTION_FILTERS = "v4/api/transactions/filters"
        const val FETCH_INVESTED_VALUE_BREAKDOWN = "v4/api/transactions/breakup/investedValueV2"
        const val FETCH_TRANSACTION_LIST = "v4/api/transactions/listTransactions"
        const val FETCH_WINNINGS_LIST = "v4/api/transactions/rewards/list"
        const val FETCH_TRANSACTION_DETAIL = "v4/api/transactions/details"
        const val FETCH_NEW_TRANSACTION_DETAILS = "v5/api/transactions/details"
        const val POST_TRANSACTION_ACTION = "v2/api/transactions/action"
        const val INVEST_WINNINGS_IN_GOLD = "v2/api/gold/invest/winnings"
        const val FETCH_PAYMENT_TRANSACTION_BREAKUP = "v1/api/wallet/transactions"
        const val FETCH_POST_SETUP_TRANSACTION_DETAILS = "v1/api/postSetup/txnData"
        const val FETCH_USER_WINNING_BREAKUP = "v4/api/transactions/rewards/breakup"
    }

    object AnalyticsKeys {
        const val Clicked_Transactions = "Clicked_Transactions"
        const val Shown_GoldTransactionScreen = "Shown_GoldTransactionScreen"
        const val Clicked_WinningsTab_GoldTransactionScreen =
            "Clicked_WinningsTab_GoldTransactionScreen"
        const val Clicked_Locker_GoldTransactionScreen = "Clicked_Locker_GoldTransactionScreen"
        const val Clicked_Filter_GoldTransactionScreen = "Clicked_Filter_GoldTransactionScreen"
        const val Clicked_Help_GoldTransactionScreen = "Clicked_Help_GoldTransactionScreen"
        const val Clicked_TransactionCard_GoldTransactionScreen =
            "Clicked_TransactionCard_GoldTransactionScreen"
        const val Clicked_InvestedValue_GoldTransactionScreen =
            "Clicked_InvestedValue_GoldTransactionScreen"
        const val Shown_WinningsScreen = "Shown_WinningsScreen"
        const val Clicked_WinningsCard_WinningsScreen = "Clicked_WinningsCard_WinningsScreen"
        const val Clicked_GoldTab_WinningsScreen = "Clicked_GoldTab_WinningsScreen"
        const val ClickedActionButton_WinningsScreen = "ClickedActionButton_WinningsScreen"
        const val Shown_FilterScreen_GoldTransactionScreen =
            "Shown_FilterScreen_GoldTransactionScreen"
        const val Clicked_FilterParameter_FilterScreen = "Clicked_FilterParameter_FilterScreen"
        const val Clicked_FilterValue_FilterScreen = "Clicked_FilterValue_FilterScreen"
        const val Removed_FilterValue_FilterScreen = "Removed_FilterValue_FilterScreen"
        const val Clicked_Clear_FilterScreen = "Clicked_Clear_FilterScreen"
        const val Clicked_Apply_FilterScreen = "Clicked_Apply_FilterScreen"
        const val Clicked_FilterApplied_FilterResultScreen =
            "Clicked_FilterApplied_FilterResultScreen"
        const val Shown_TransactionDetailsScreen = "Shown_TransactionDetailsScreen"
        const val RoundOffCard_TransactionDetailsScreen = "RoundOffCard_TransactionDetailsScreen"
        const val Clicked_TrackingLinkCard_TransactionDetailsScreen =
            "Clicked_TrackingLinkCard_TransactionDetailsScreen"
        const val Clicked_CTA_TransactionDetailsScreen = "Clicked_CTA_TransactionDetailsScreen"
        const val Clicked_CopyTransactionId_TransactionDetailsScreen =
            "Clicked_CopyTransactionId_TransactionDetailsScreen"
        const val Clicked_TransactionChevron_TransactionDetailsScreen =
            "Clicked_TransactionChevron_TransactionDetailsScreen"
        const val Clicked_ContactUs_TransactionDetailsScreen =
            "Clicked_ContactUs_TransactionDetailsScreen"
        const val Clicked_RemoveFilterValue_FilterResultScreen =
            "Clicked_RemoveFilterValue_FilterResultScreen"
        const val GoldTransactionScreen_PrimaryCtaClicked =
            "GoldTransactionScreen_PrimaryCtaClicked"

        object Parameters {
            const val cardIncluded = "cardIncluded"
            const val Winnings_Status = "Winnings_Status"
        }

        object Values {
            const val WeeklyMagicCard = "Weekly Magic Card"
        }
    }
}