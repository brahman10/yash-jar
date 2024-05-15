package com.jar.app.feature_gold_lease.shared.util

object GoldLeaseEventKey {

    object Screens {
        const val LISTING_SCREEN = "Listing  Screen"
        const val LEASE_DETAILS_SCREEN = "Lease Details Screen"
        const val SUMMARY_SCREEN = "Summary Screen"
        const val NEW_LEASES_SCREEN = "New Leases Screen"
        const val MY_ORDER_SCREEN = "My Order Screen"
        const val LEASE_ORDER_DETAILS_SCREEN = "Lease Order Details Screen"
        const val LEASE_KYC = "Lease Kyc"
        const val POST_ORDER = "Post Order Screen"
    }

    object Properties {
        const val USER_TYPE = "user_type"
        const val BUTTON_TYPE = "button_type"
        const val ELEMENT_TYPE_RANDOM = "element_type_random"
        const val TITLE = "title"
        const val POSITION = "position"
        const val FROM_FLOW = "from_flow"
        const val GOLD_IN_LOCKER = "gold_in_locker"
        const val TAB = "tab"
        const val SELECTED_EARNINGS = "selected_earnings"
        const val SELECTED_JEWELLER_TITLE = "selected_jeweller_title"
        const val SELECTED_FILLING_STAGE = "selected_filling_stage"
        const val SELECTED_LOCK_IN_PERIOD = "selected_lock_in_period"
        const val SELECTED_MINIMUM_QUANTITY = "selected_minimum_quantity"
        const val SELECTED_JAR_BONUS = "selected_jar_bonus"
        const val DATA = "data"
        const val LAUNCH_TYPE = "launch_type"
        const val GOLD_SELECTION = "gold_selection"
        const val QUANTITY_ENTERED = "quantity_entered"
        const val AMOUNT_ENTERED = "amount_entered"
        const val WEIGHTS_SELECTED = "weights_selected"
        const val JAR_SAVINGS_TOGGLE = "jar_savings_toggle"
        const val LOCKER_GOLD_USED = "locker_gold_used"
        const val NON_LOCKER_GOLD_QUANTITY = "non_locker_gold_quantity"
        const val NON_LOCKER_GOLD_PRICE = "non_locker_gold_price"
        const val EARNINGS = "earnings"
        const val LOCK_IN = "lock_in"
        const val VALUE_RS = "Value_Rs"
        const val VALUE_GM = "Value_Gm"
        const val TOTAL_GOLD_QTY = "total_gold_qty"
        const val TOTAL_GOLD_PRICE = "total_gold_price"
        const val PAN_CARD_STATUS = "pan_card_status"
        const val TOTAL_EARNINGS = "total_earnings"
        const val LEASED_GOLD = "leased_gold"
        const val EARNINGS_ADDED = "earnings_added"
        const val LEASE_STATUS = "lease_status"
        const val LEASE_TITLE = "lease_title"
        const val YEARLY_EARNINGS = "yearly_earnings"
        const val GOLD_X_EARNINGS = "goldx_earnings"
        const val EARNINGS_THIS_MONTH = "earnings_this_month"
        const val ADDITIONAL_GOLD = "additional_gold"
        const val EARNINGS_ADDED_TO_LOCKER = "earnings_added_to_locker"
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
        const val TRANSACTION_TYPE = "transaction_type"
        const val GOLD_AMOUNT = "gold_amount"
        const val TRANSACTION_STAGE = "transaction_stage"
        const val SCREEN_NAME = "screen_name"
        const val DEFAULT_TAB = "default_tab"
        const val ONGOING_LEASE_COUNT = "ongoing_lease_count"
    }

    object Values {
        const val NEW = "New"
        const val REPEAT = "Repeat"
        const val GOLD = "Gold"
        const val GOLDX = "GoldX"
        const val GET_STARTED = "Get Started"
        const val OFFERINGS_TILE = "Offerings Tile"
        const val GOLD_IN_LOCKER = "Gold In Locker"
        const val CLOSED_CARD = "Closed Card"
        const val JAR_BONUS_TAG = "Jar Bonus Tag"
        const val GUARANTEED_TAG = "Guaranteed Tag"
        const val JEWELLER_LOGO = "Jeweller Logo"
        const val SELECT = "Select"
        const val INFO_ICON = "info_icon"
        const val FILTER = "Filter"
        const val LOCK_IN_PERIOD = "Lock In Period"
        const val JEWELLER_TITLE = "Jeweller Title"
        const val MINIMUM_QUANTITY = "Minimum Quantity"
        const val DIRECT = "direct"
        const val BACK_BUTTON_CLICKED = "back_button_clicked"
        const val PROCEED_BUTTON = "proceed_button"
        const val ON = "On"
        const val OFF = "Off"
        const val IN_RS = "In Rs"
        const val IN_GM = "In Gm"
        const val OPTION_PILL = "option_pill"
        const val VERIFIED = "verified"
        const val VERIFY = "verify"
        const val TICK = "Tick"
        const val TNC = "TnC"
        const val RISK_FACTOR = "Risk Factor"
        const val JEWELLER_INFO = "Jeweller Info"
        const val PRICE_INFO = "Price Info"
        const val START_NEW_LEASE = "Start New Lease"
        const val LEASE_INFORMATION = "Lease Information"
        const val AGREEMENT = "Agreement"
        const val TRANSACTION = "Transaction"
        const val LEASE_CARD_CHEVRON = "Lease Card Chevron"
        const val LOCKER_TOGGLE = "LOCKER_TOGGLE"
        const val TAB_NEW_LEASE = "TAB_NEW_LEASE"
        const val TAB_MY_ORDERS = "TAB_MY_ORDERS"
    }

    object CommonEvents {
        const val Lease_FAQButtonClicked = "Lease_FAQButtonClicked"
        const val Lease_ContactSupport = "Lease_ContactSupport"
        const val Lease_BackButtonClicked = "Lease_BackButtonClicked"
    }

    object LeasePostOrderScreen {
        const val Lease_GoldLeaseSuccessful = "Lease_GoldLeaseSuccessful"
        const val Lease_GoldLeaseFailed = "Lease_GoldLeaseFailed"
        const val Lease_GoldLeaseProcessing = "Lease_GoldLeaseProcessing"
    }

    object UserLeaseDetailsScreen {
        const val Lease_OrderDetailsScreenLaunched = "Lease_OrderDetailsScreenLaunched"
        const val Lease_OrderDetailsScreenClicked = "Lease_OrderDetailsScreenClicked"
    }

    object LeaseMyOrdersScreen {
        const val Lease_InfoScreenMyOrdersShown = "Lease_InfoScreenMyOrdersShown"
        const val Lease_InfoScreenMyOrdersClicked = "Lease_InfoScreenMyOrdersClicked"
        const val Lease_InfoScreenOngoingLeasesCardClicked = "Lease_InfoScreenOngoingLeasesCardClicked"
    }

    object LeaseSummaryScreen {
        const val Lease_SummaryScreenShown = "Lease_SummaryScreenShown"
        const val Lease_SummaryScreenClicked = "Lease_SummaryScreenClicked"
    }

    object LeaseOrderDetails {
        const val Lease_MainScreenLaunched = "Lease_MainScreenLaunched"
        const val Lease_MainScreenClicked = "Lease_MainScreenClicked"
        const val Lease_MainScreen_InputError = "Lease_MainScreen_InputError"
    }

    object GoldLeaseNewLeaseScreen {
        const val Lease_InfoScreenShown = "Lease_InfoScreenShown"
        const val Lease_InfoScreenShown_Ts = "Lease_InfoScreenShown_Ts"
        const val Lease_InfoScreenClicked = "Lease_InfoScreenClicked"
    }

    object LeasePlansScreen {
        const val Lease_ListingsScreenShown = "Lease_ListingsScreenShown"
        const val Lease_ListingsScreenClicked = "Lease_ListingsScreenClicked"
        const val Lease_ListingsScreenRandomClick = "Lease_ListingsScreenRandomClick"
    }

    object Faq {
        //Events
        const val Lease_FAQClicked = "Lease_FAQClicked"

        //Props
        const val faq_type = "faq_type"
    }

    object KycVerification {
        //Events
        const val Lease_VerificationBSShown = "Lease_VerificationBSShown"
        const val Lease_ConfirmandVerifyDetailsClicked = "Lease_ConfirmandVerifyDetailsClicked"
        const val Lease_PANVerifSuccess = "Lease_PANVerifSuccess"
        const val Lease_PANVeriffailed = "Lease_PANVeriffailed"
        const val Lease_PANVerifTryAgainClicked = "Lease_PANVerifTryAgainClicked"
        const val Lease_PANVerifContactSupportClicked = "Lease_PANVerifContactSupportClicked"
        const val Lease_PANVerifScreenBackClicked = "Lease_PANVerifScreenBackClicked"
        const val Lease_Verification = "Lease_Verification"

        //Props
        const val pan_fill_type = "pan_fill_type"
        const val email_fill_type = "email_fill_type"
        const val valEntered = "valEntered"
        const val value = "value"

        //Values
        const val manual = "manual"
        const val automatic = "automatic"
        const val PAN = "PAN"
        const val Name = "Name"
        const val Email = "Email"
        const val DOB = "DOB"
    }
}