package com.jar.app.feature_gold_redemption.shared.util

internal object GoldRedemptionConstants {

    object Endpoints {
        const val URL_INTRO = "v1/api/goldRedemption/intro"
        const val URL_INTRO_2 = "v1/api/goldRedemption/intro2"
        const val URL_FAQS = "v1/api/goldRedemption/faqs"
        const val URL_LISTING_SCREEN_STATIC = "v1/api/goldRedemption/listingScreenStatic"
        const val URL_GET_PRODUCTS = "v1/api/goldRedemption/getProducts"
        const val URL_USER_VOUCHERS = "v1/api/goldRedemption/userVouchers"
        const val URL_GET_PRODUCT = "v1/api/goldRedemption/getProduct"
        const val URL_INITIATE_ORDER = "v1/api/goldRedemption/initiateOrder"
        const val URL_USER_VOUCHER = "v1/api/goldRedemption/userVoucher/viewDetails"
        const val URL_GET_ALL_STATES = "v1/api/goldRedemption/getAllStates"
        const val URL_GET_ALL_CITIES = "v1/api/goldRedemption/getAllCities"
        const val URL_GET_ALL_STORES = "v1/api/goldRedemption/getAllStores"
        const val URL_ABANDON_SCREEN = "v1/api/goldRedemption/abandonScreen"
        const val URL_INITIATE_PAYMENT = "v1/api/goldRedemption/initiatePayment"
        const val URL_TRANSACTIONS = "v1/api/goldRedemption/transactions/seeDetails"
        const val URL_PAYMENT_HISTORY = "v1/api/goldRedemption/paymentHistory"
        const val URL_GET_USER_VOUCHERS_COUNT = "v1/api/goldRedemption/getUserVouchersCount"
        const val URL_GET_PENDING_ORDERS = "v1/api/goldRedemption/bottomCardClickable/payment"
    }

}

object GoldRedemptionAnalyticsKeys {
    const val Redemption_HomeScreenLaunced = "Redemption_HomeScreenLaunced"
    const val Redemption_CardShown = "Redemption_CardShown"
    const val Redemption_CardShown_Ts = "Redemption_CardShown_Ts"
    const val Redemption_ExploreButtonClicked = "Redemption_ExploreButtonClicked"
    const val Redemption_VouchersScreenLaunched = "Redemption_VouchersScreenLaunched"
    const val Redemption_VoucherClicked = "Redemption_VoucherClicked"
    const val Redemption_VPurchaseScreenLaunched = "Redemption_VPurchaseScreenLaunched"
    const val Redemption_VAmountChangeClicked = "Redemption_VAmountChangeClicked"
    const val Redemption_VAmountChanged = "Redemption_VAmountChanged"
    const val Redemption_VoucherQuantityChanged = "Redemption_VoucherQuantityChanged"
    const val Redemption_MoreInfoClicked = "Redemption_MoreInfoClicked"
    const val Redemption_MaxLimitMsgShown = "Redemption_MaxLimitMsgShown"
    const val Redemption_PurchaseScreenProceedClicked = "Redemption_PurchaseScreenProceedClicked"
    const val Redemption_PaymentReceivedStatusScreenLaunched =
        "Redemption_PaymentReceivedStatusScreenLaunched"
    const val Redemption_VoucherPurchaseStatusScreenCTAClicked =
        "Redemption_VoucherPurchaseStatusScreenCTAClicked"
    const val Redemption_VoucherPurchaseStatusScreenLaunched =
        "Redemption_VoucherPurchaseStatusScreenLaunched"
    const val Redemption_OnlineRedemptionLinkClicked = "Redemption_OnlineRedemptionLinkClicked"
    const val Redemption_OfflineRedemptionCheckCliked = "Redemption_OfflineRedemptionCheckCliked"
    const val Redemption_VStateBSOpened = "Redemption_VStateBSOpened"
    const val Redemption_VStateBSTyped = "Redemption_VStateBSTyped"
    const val Redemption_VStateBSStateSelected = "Redemption_VStateBSStateSelected"
    const val Redemption_VStateBSCitySelected = "Redemption_VStateBSCitySelected"
    const val Redemption_MyOrdersTabClicked = "Redemption_MyOrdersTabClicked"
    const val Redemption_MyOrdersScreenShown = "Redemption_MyOrdersScreenShown"
    const val Redemption_MyOrdersVoucherClicked = "Redemption_MyOrdersVoucherClicked"
    const val Redemption_MyOrdersVoucherPinCopied = "Redemption_MyOrdersVoucherPinCopied"
    const val Redemption_ContactSupportClicked = "Redemption_ContactSupportClicked"
    const val Redemption_BackClicked = "Redemption_BackClicked"
    const val Redemption_ShareClicked = "Redemption_ShareClicked"
    const val Redemption_HomeFAQClicked = "Redemption_HomeFAQClicked"
    const val Redemption_VoucherSuccessFeedbackSelected =
        "Redemption_VoucherSuccessFeedbackSelected"
    const val Redemption_VoucherSuccessFeedbackSubmitted =
        "Redemption_VoucherSuccessFeedbackSubmitted"
    const val Redemption_MyOrdersScreenPmtHistoryButtonClicked =
        "Redemption_MyOrdersScreenPmtHistoryButtonClicked"
    const val Redemption_MyOrdersScreenPmtHistoryClicked =
        "Redemption_MyOrdersScreenPmtHistoryClicked"

    const val Redemption_VoucherPurchaseStatusScreenShown = "Redemption_VoucherPurchaseStatusScreenShown"
    const val Redemption_PaymentReceivedStatusScreenShown = "Redemption_PaymentReceivedStatusScreenShown"

    const val VoucherTitle = "VoucherTitle"
    const val goldBenefitPercentage = "goldBenefitPercentage"
    const val minimumVoucherAmount = "minimumVoucherAmount"
    const val voucherTab = "voucherTab"

    const val VOUCHER_TITLE = "VOUCHER_TITLE"
    const val VOUCHER_TYPE = "VOUCHER_TYPE"
    const val VOUCHER_TAB = "VOUCHER_TAB"
    const val GOLD_BENEFIT_PERCENTAGE = "GOLD_BENEFIT_PERCENTAGE"
    const val MINIMUM_VOUCHER_AMOUNT = "MINIMUM_VOUCHER_AMOUNT"
    const val GOLD_BENEFIT = "GOLD_BENEFIT"
    const val REDEMPTION_AVAILABILITY = "REDEMPTION_AVAILABILITY"
    const val VOUCHER_QUANTITY = "VOUCHER_QUANTITY"


    const val AMOUNT_CHANGED_FROM = "AMOUNT_CHANGED_FROM"
    const val AMOUNT_CHANGED_TO = "AMOUNT_CHANGED_TO"

    const val CHANGE_TYPE = "CHANGE_TYPE"
    const val CHANGE_FROM = "CHANGE_FROM"
    const val CHANGE_TO = "CHANGE_TO"
    const val VOUCHER_AMOUNT = "VOUCHER_AMOUNT"
    const val DECREASE = "DECREASE"
    const val INCREASE = "INCREASE"
    const val MOREINFO_TYPE = "MOREINFO_TYPE"

    const val GOLD_BENEFIT_AMOUNT = "GOLD_BENEFIT_AMOUNT"

    const val CTA_BUTTON = "CTA_BUTTON"
    const val SCREEN_STATUS = "SCREEN_STATUS"
    const val PAID_PLATFORM = "PAID_PLATFORM"
    const val ORDER_ID = "ORDER_ID"
    const val GO_TO_MY_ORDERS = "GO_TO_MY_ORDERS"
    const val GO_TO_CONTINUE_SHOPPING = "GO_TO_CONTINUE_SHOPPING"
    const val RETRY = "RETRY"
    const val REFRESH = "REFRESH"


    const val SOURCE_SCREEN = "SOURCE_SCREEN"

    const val SOURCE = "SOURCE"

    const val MY_VOUCHER = "MY_VOUCHER"
    const val VOUCHER_PURCHASE = "VOUCHER_PURCHASE"
    const val VOUCHER_DETAIL = "VOUCHER_DETAIL"
    const val TYPED_TEXT = "TYPED_TEXT"

    const val STATE = "STATE"
    const val CITY = "CITY"
    const val NO_OF_STORES = "NO_OF_STORES"
    const val NO_OF_CITIES = "NO_OF_CITIES"

    const val ACTIVE_VOUCHERS_COUNT = "ACTIVE_VOUCHERS_COUNT"
    const val EXPIRED_VOUCHERS_COUNT = "EXPIRED_VOUCHERS_COUNT"

    const val VOUCHER_STATUS = "VOUCHER_STATUS"
    const val MY_ORDERS_TAB = "MY_ORDERS_TAB"


    const val LAUNCH_SOURCE = "LAUNCH_SOURCE"

    const val MyVScreen = "MyVScreen"
    const val Purchase_Screen = "Purchase_Screen"
    const val VouchersHomeScreen = "VouchersHomeScreen"
    const val VOUCHER_BONUS = "VOUCHER_BONUS"

    const val TRANSACTION_CARD_TYPE = "TRANSACTION_CARD_TYPE"
    const val FAQ_TITLE = "FAQ_TITLE"
    const val BACK_BUTTON = "BACK_BUTTON"
    const val CARD_NAME = "CARD_NAME"
    const val BRAND_PARTNERS = "BRAND_PARTNERS"
    const val HOW_DO_VOUCHERS_WORK = "HOW_DO_VOUCHERS_WORK?"
    const val GET_UP_TO_10_EXTRA_GOLD_IN_YOUR_LOCKER = "GET_UP_TO_10%_EXTRA_GOLD_IN_YOUR_LOCKER"
    const val FAQS = "FAQS"
    const val ShownCheckoutGoldDelivery = "ShownCheckoutGoldDelivery"
}