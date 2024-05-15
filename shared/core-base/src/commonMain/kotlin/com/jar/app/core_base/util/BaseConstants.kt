package com.jar.app.core_base.util

object BaseConstants {

    const val DEFAULT_DURATION_300_MILLIS = 300L
    const val Amount = "Amount"
    const val COUPON = "COUPON"
    const val SuggestedAmount = "SuggestedAmount"
    const val topic = "topic"
    const val PROFILE = "Profile"
    const val GoldPriceAlerts = "Gold Price Alerts"
    const val ShubhMuhuratAlerts = "Shubh Muhurat Alerts"
    const val HEALTH_INSURANCE_HOMEFEED_CAROUSEL = "HEALTH_INSURANCE_HOMEFEED_CAROUSEL"

    const val APPS_FLYER = "AppsFlyer"
    const val SinglePageHomeFeed = "SinglePageHomeFeed"
    const val RightBottomSheet = "RightBottomSheet"
    const val LeftBottomSheet = "LeftBottomSheet"

    const val DEFAULT_COUNTRY_CODE_WITH_PLUS_SIGN = "+91"


    const val Authorization = "Authorization"
    const val Bearer = "Bearer"

    const val WHATSAPP_REGULAR_PACKAGE_NAME = "com.whatsapp"
    const val WHATSAPP_BUSINESS_PACKAGE_NAME = "com.whatsapp.w4b"

    const val DEFAULT_INVESTMENT_AMOUNT = 10
    const val ON_VIDEO_ENDED = "ON_VIDEO_ENDED"
    const val ON_VIDEO_STARTED = "ON_VIDEO_STARTED"
    const val ON_GENERIC_SCREEN_DISMISSED = "ON_GENERIC_SCREEN_DISMISSED"
    const val ON_GENERIC_DIALOG_DISMISSED = "ON_GENERIC_DIALOG_DISMISSED"
    const val PAUSE_SAVING_DIALOG_DISMISSED = "PAUSE_SAVING_DIALOG_DISMISSED"
    const val PAUSE_SAVING_DIALOG_PAUSE_ACTION = "PAUSE_SAVING_DIALOG_PAUSE_ACTION"

    const val ButtonType = "buttonType"
    const val Screen = "screen"
    const val FromScreen = "FromScreen"
    const val LanguagesShown = "LanguagesShown"
    const val defaultLanguageShown = "defaultLanguageShown"
    const val DailySetupSuccess = "DailySetupSuccess"
    const val REFERRAL_TAB_INVITE = "ReferralTabInvite"
    const val REFERRAL_TAB_REFERRAL = "ReferralTabReferral"
    const val REFERRAL_TAB_FAQ = "ReferralTabFaq"
    const val TYPE = "TYPE"
    const val STATE = "STATE"
    const val ON = "ON"
    const val OFF = "OFF"
    const val SHARE = "Share"
    const val COMPRESSED_DIR = "compress"
    const val CACHE_DIR_SHARED = "shared"
    const val REWARDS_DOUBLED = "rewards_doubled"
    const val REWARDS_ = "rewards_"
    const val TEXT_PLAIN = "text/plain"
    const val FILE_PROVIDER_AUTHORITY = ".fileprovider"
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.jar.app"

    const val REGION_CODE = "IN"

    const val BASE_EXTERNAL_DEEPLINK = "dl.myjar.app/"
    const val BASE_INTERNAL_DEEPLINK = "android-app://com.jar.app/"
    const val SOURCE = "source"
    const val SOURCE_NOTIF = "notif"
    const val SELECTED_PATH = "SelectedPath"
    const val CROPPED_PATH = "croppedPath"
    const val IMAGES_DIR = "images"
    const val PART = "part"
    const val SELFIE = "selfie"
    const val Viba = "Viba"

    const val FROM_LENDING = "Lending"
    const val KEY_OTP_SUCCESS = "CreditReportOtpSuccess"
    const val FROM_OTHER = "Other"
    const val PAN = "PAN"
    const val COUPON_CODE_DISCOVERY = "coupon_code_discovery"

    const val PLOTLINE_CONSTANT = "PL_"
    const val PLOTLINE_BANK_CHECK_LIST_ITEM = "BANK_CHECK_LIST_ITEM_"
    const val FROM_HAMBURGER_MENU = "HAMBURGER_MENU"

    object HomeBottomNavigationScreen {
        const val HOME = 0
        const val TRANSACTION = 1
        const val PROFILE = 2
    }

    object ManageNotificationPosition {
        const val GOLD_PRICE_ALERT = 1
        const val AUSPICIOUS_ALERT = 2
        const val ROUND_OFF_NOTIFICATION = 3
        const val OFFER_UPDATES = 4
        const val TRANSACTION_ALERTS = 5
        const val AUTOPAY_ALERTS = 6
    }

    object SettingV2CardViewTags {
        const val PAYMENT_METHODS = "PaymentMethods"
        const val DAILY_SAVINGS = "DailySavings"
        const val ROUND_OFF = "RoundOff"
        const val GOLD_SIP = "GoldSip"
        const val NOTIFICATION_SETTINGS = "NotificationSettings"
        const val LANGUAGE = "Language"
        const val BATTERY_OPTIMIZATION = "BatteryOptimization"
        const val JAR_SECURITY_SHIELD = "JarSecurityShield"
        const val TERMS_AND_CONDITIONS = "TermsAndConditions"
        const val PRIVACY_POLICY = "PrivacyPolicy"
        const val TEST_ACTIVITY = "TextActivity"
        const val BASE_API_URL = "baseApiUrl"
    }

    object SettingsV2CardPosition {
        const val HEADER_PAYMENTS = 0
        const val PAYMENT_METHODS = 1
        const val DAILY_SAVINGS = 2
        const val ROUND_OFF = 3
        const val GOLD_SIP = 4

        const val SEPARATOR_PAYMENTS = 5
        const val HEADER_APP_SETTINGS = 6
        const val NOTIFICATION_SETTINGS = 7
        const val LANGUAGE = 8

        const val SEPARATOR_APP_SETTINGS = 9
        const val HEADER_PRIVACY_PERMISSION = 10
        const val BATTERY_OPTIMIZATION = 11
        const val JAR_SECURITY_SHIELD = 12
        const val TERMS_AND_CONDITIONS = 13
        const val PRIVACY_POLICY = 14

        const val SEPARATOR_TESTING = PRIVACY_POLICY+1
        const val BASE_API_URL = SEPARATOR_TESTING + 1
        const val TEST_ACTIVITY = BASE_API_URL + 1 // Ensure this remains at last position
    }

    object InviteCardPosition {
        const val INVITE_WHATSAPP = 1
        const val EARNING_INFO = 2
    }

    object ErrorCode {
        const val APP_UNDER_MAINTENANCE = 100
        const val ACTIVE_SESSIONS_DETECTED = 409
        const val UNUSUAL_ACTIVITY_DETECTED = 417
        const val PHONE_PE_INSTALLED_BUT_DID_NOT_LAUNCH = 900
        const val SOME_ERROR_OCCURRED_PLEASE_TRY_AGAIN = "0000"
        const val INVALID_BUY_PRICE_EXCEPTION = "421"
        const val BACK_PRESSES_FROM_PAYMENT_SCREEN = "4567"
    }

    object ErrorCodesLendingKyc {
        object Email {
            const val OTP_ATTEMPT_LIMIT_EXCEEDED = "3001"
            const val EMAIL_DOES_NOT_EXIST = "3002"
            const val OTP_EXPIRED = "4000"
            const val OTP_ENTERED_IS_INCORRECT = "4001"
            const val UNABLE_TO_VERIFY_OTP = "4003"
            const val EMAIL_ID_ENTERED_IS_ASSOCIATED_WITH_ANOTHER_ACCOUNT = "9000"
        }

        object PAN {
            const val CREDIT_REPORT_SEARCH_TAKING_LONGER_THAN_EXPECTED = "6001"
            const val OTP_ATTEMPT_LIMIT_EXHAUSTED = "6002"
            const val OTP_ATTEMPT_LIMIT_EXCEEDED = "6003"
            const val CREDIT_REPORT_DOES_NOT_EXIST = "6004"
            const val OTP_ENTERED_IS_INCORRECT = "6005"
            const val VERIFICATION_FAILED = "6006"
            const val PAN_ENTRY_LIMIT_EXCEEDED = "6008"

            // User cannot retry, contact support.
            const val PAN_ENTRY_LIMIT_EXHAUSTED = "6009"
            const val INVALID_PAN_CARD = "6010"
            const val UNABLE_TO_EXTRACT_DATA_FROM_FILE = "6011"
            const val NSDL_VERIFICATION_FAILED = "6014"
            const val NSDL_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED = "6015"
            const val PAN_CARD_ALREADY_EXIST = "6017"
            const val PAN_ENTERED_IS_ASSOCIATED_WITH_ANOTHER_ACCOUNT = "9001"
        }

        object Aadhaar {
            const val INVALID_AADHAAR = "5001"
            const val INVALID_CAPTCHA = "5002"
            const val NO_MOBILE_LINKED = "5003"
            const val UNABLE_TO_REACH_UIDAI = "5004"
            const val INVALID_OTP = "5005"
            const val INVALID_OTP_FORMAT = "5006"
            const val OTP_LIMIT_EXHAUSTED = "5007"
            const val AADHAAR_PAN_MISMATCH = "5008"
            const val AADHAAR_PAN_MISMATCH_SUPPORT = "5010"
            const val AADHAAR_ENTERED_IS_ASSOCIATED_WITH_ANOTHER_ACCOUNT = "9002"
        }

        object Selfie {
            const val NO_IMAGE_FOUND = "2001"
            const val FACE_NOT_DETECTED = "2002"
            const val EYE_IS_CLOSED = "2003"
            const val LOW_QUALITY_IMAGE = "2004"
            const val SELFIE_MATCH_FAILED = "2005"
            const val SELFIE_MATCH_RETRY_LIMIT_EXCEEDED = "2006"
            const val SELFIE_MATCH_RETRY_LIMIT_EXHAUSTED = "2007"
        }

        const val COMPLETE_PRE_REQUISITE_FOR_AADHAAR_AND_PAN_LINKAGE = "0001"
    }

    object WithdrawalType {
        const val WITHDRAWAL_TYPE_GOLD = "GOLD"
        const val WITHDRAWAL_TYPE_AMOUNT = "AMOUNT"
    }

    object ExternalDeepLinks {

        const val BUY_GOLD = "buyGold"
        const val DAILY_SAVINGS = "dailySaving"
        const val SETUP_DAILY_INVESTMENT = "setupDailyInvestment"
        const val DAILY_SAVINGS_ONBOARDING = "dailySavingOnboarding"
        const val TRANSACTIONS = "transactions"
        const val GOLD_DELIVERY = "goldCoinStore"
        const val GOLD_REDEMPTION = "goldRedemption"
        const val JEWELLERY_VOUCHERS = "jewelleryVoucher"
        const val GOLD_REDEMPTION_BRAND_CATALOGUE = "brandCatalouge"
        const val GOLD_REDEMPTION_MY_ORDERS = "myOrders"
        const val GOLD_REDEMPTION_VOUCHER_STATUS = "voucherStatus"
        const val REFER_EARN_V2 = "referAndEarnV2"
        const val GOLD_REDEMPTION_VOUCHER_PURCHASE = "voucherPurchase"
        const val GOLD_DELIVERY_CART = "goldCoinStoreCart"
        const val DAILY_SAVINGS_CANCELLATION = "dailySavingsCancellation"
        const val SURVEY = "survey"
        const val SPIN_GAME = "spinbottle"
        const val KYC = "kyc"
        const val PROMO_CODE = "enterPromo"
        const val GOLD_GIFTING = "goldGifting"
        const val CHANGE_LANGUAGE = "changeLanguage"
        const val SELL_GOLD = "sellGold"
        const val SETTINGS = "settings"
        const val HELP_SUPPORT = "helpSupport"
        const val HELP_SUPPORT_HEALTH_INSURANCE = "helpSupportHealthInsurance"
        const val REWARDS = "myWinnings"
        const val PROFILE = "profile"
        const val GOLD_PRICE_DETAIL = "goldPriceDetail"
        const val DAILY_SAVINGS_SETTINGS = "dailySavingSettings"
        const val REFER_AND_EARN = "referAndEarn"
        const val REFERRAL_FAQ = "referralFaq"
        const val REFERRAL_INVITE_SHARE = "referralInviteShare"
        const val TRANSACTION_DETAIL = "transactionDetail"
        const val NEW_TRANSACTION_DETAIL = "newTransactionDetail"
        const val ROUND_OFF = "roundOff"
        const val PRE_ROUND_OFF_AUTOPAY_SETUP = "preRoundOffAutopaySetup"
        const val KYC_DETAILS = "kycDetails"
        const val KYC_VERIFICATION = "kycVerification"
        const val LENDING_KYC_ONBOARDING = "lending-kyc-onboarding"
        const val LENDING_KYC_RESUME = "lending-kyc-resume"
        const val JAR_DUO = "jarDuo"
        const val OFFER_LIST_FRAGMENT = "offerListPage"
        const val JAR_DUO_ONBOARDING = "jarDuoOnboarding"
        const val ROUND_OFF_EXPLANATION = "roundOffExplanation"
        const val UPDATE_DAILY_SAVING_MANDATE_SETUP = "updateDailySavingMandateSetup"
        const val WEEKLY_MAGIC = "weeklyMagic"
        const val SETUP_GOLD_SIP = "setupGoldSip"
        const val GOLD_SIP_INTRO = "goldSipIntro"
        const val GOLD_SIP_TYPE_SELECTION = "goldSipTypeSelection"
        const val GOLD_SIP_DETAILS = "goldSipDetails"
        const val HELP_VIDEOS_LISTING = "helpVideosListing"
        const val LENDING_ONBOARDING = "lendingOnboarding"
        const val REAL_TIME_READY_CASH = "realTimeReadyCash"
        const val CHECK_CREDIT_SCORE = "checkCreditScore"
        const val WEB_VIEW = "webView"
        const val VIBA_WEB_VIEW = "vibaWebView"
        const val FIRST_COIN = "firstCoin"
        const val FIRST_COIN_TRANSITION = "transition"
        const val FIRST_COIN_PROGRESS = "progress"
        const val FIRST_COIN_DELIVERY = "delivery"
        const val ROUND_OFF_DETAILS = "roundOffDetails"
        const val WITHDRAWAL_HELP_BOTTOM_SHEET = "withdrawalHelpBottomSheet"
        const val GOLD_LEASE = "goldLease"
        const val GOLD_LEASE_NEW_LEASE = "goldLeaseNewLease"
        const val GOLD_LEASE_MY_ORDERS = "goldLeaseMyOrders"
        const val GOLD_LEASE_PLANS = "goldLeasePlans"
        const val GOLD_LEASE_USER_LEASE_DETAILS = "goldLeaseUserLeaseDetails"
        const val GOLD_LEASE_SUMMARY_RETRY_FLOW = "goldLeaseOrderSummary"
        const val POST_SETUP_DETAILS = "postSetupDetails"
        const val TRANSACTION_DETAIL_BOTTOM_SHEET = "transactionDetailBottomSheet"
        const val DAILY_SAVING_EDUCATION = "dailySavingEducation"
        const val SPENDS_TRACKER = "spendsTracker"
        const val UPDATE_DAILY_SAVING_V2 = "updateDailySavingV2"
        const val PRE_DAILY_SAVING_AUTOPAY = "preDailySavingAutopay"
        const val UPDATE_AUTOPAY_BANK = "updateAutopayBank"

        const val SAVING_GOAL = "savingsGoal"
        const val SAVINGS_GOALS_SETTINGS = "savingsGoalSettings"

        const val JAR_HEALTH_INSURANCE = "jarHealthInsurance"
        const val JAR_HEALTH_INSURANCE_POST_PURCHASE = "jarHealthInsurancePostPurchase"
        const val JAR_HEALTH_INSURANCE_ADD_DETAILS = "jarHealthInsuranceAddDetails"

        const val SINGLE_HOME_FEED_CTA = "singleHomeFeedCta"
        const val JAR_HEALTH_INSURANCE_SELECT_PLAN = "jarHealthInsuranceSelectPlan"
        const val JAR_HEALTH_INSURANCE_MANAGE_SCREEN = "jarHealthInsuranceManageScreen"

        const val QUEST = "quest"
        const val QUEST_COUPON_DETAILS = "brandCouponDetails"
        const val QUEST_ALL_REWARDS = "allRewards"
        const val QUEST_DASHBOARD = "dashboard"
        const val QUEST_SPLASH = "splash"

        const val STORIES = "story"
        const val IN_APP_STORY_EXTERNAL_LINK ="inAppExternalLink"
        const val EXTERNAL_PROMO_CODE = "promoCode"
        const val INITIATE_PAYMENT = "initiatePayment"
        const val DAILY_INVESTMENT_UPDATE_FRAGMENT ="updateDailySavingsV3Fragment"
        const val MANUAL_SAVING_TRACKER = "manualSavingsTracker"
        const val CALCULATOR = "intrestCalculator"
        const val SAVINGS_CALCULATOR = "savingsCalculator"

        const val EXIT_SURVEY = "featureExitSurvey"
    }

    object InternalDeepLinks {
        const val INVOICE = "android-app://com.jar.app/invoice"
        const val KYC_STATUS = "android-app://com.jar.app/kycDetails"
        const val COMPLETE_KYC = "android-app://com.jar.app/kycVerification"
        const val PROMO_CODE = "android-app://com.jar.app/claimPromoCodeFragment"
        const val SELL_GOLD = "android-app://com.jar.app/sellGold"
        const val SELL_GOLD_REVAMP = "android-app://com.jar.app/sellGoldRevamp"
        const val SETTINGS = "android-app://com.jar.app/settings"
        const val PROFILE = "android-app://com.jar.app/profile"
        const val POST_SETUP = "android-app://com.jar.app/postSetupDetails"
        const val LENDING_KYC_RESUME = "android-app://com.jar.app/lending-kyc-resume"
        const val FIRST_COIN_PROGRESS = "android-app://com.jar.app/firstCoin/progress"
        const val FIRST_COIN_TRANSITION = "android-app://com.jar.app/firstCoin/transition"
        const val FIRST_COIN_DELIVERY = "android-app://com.jar.app/firstCoin/delivery"
        const val HOME = "android-app://com.jar.app/homePagerFragment"
        const val GOLD_SIP_DETAILS = "android-app://com.jar.app/goldSipDetails"
        const val GOLD_SIP_TYPE_SELECTION = "android-app://com.jar.app/goldSipTypeSelection"
        const val GOLD_PRICE_DETAILS = "android-app://com.jar.app/goldPriceDetail"
        const val EDIT_DAILY_SAVING_AMOUNT = "android-app://com.jar.app/updateDailySavingBottomSheet"
        const val SAVINGS_GOAL = "android-app://com.jar.app/savingsGoal"
        const val SETUP_DAILY_INVESTMENT_BOTTOMSHEET_V2 = "android-app://com.jar.app/setupDailyInvestmentBottomSheetV2"
        const val BUY_GOLD_V2_BOTTOMSHEET = "android-app://com.jar.app/buyGoldV2BottomSheet"
        const val POST_SETUP_DS_CANCELLATION_BOTTOMSHEET = "android-app://com.jar.app/postSetupDSCancellationBottomSheet"
        const val ADD_DETAILS_DEEPLINK = "android-app://com.jar.app/healthInsurance/addDetails"
        const val PAYMENT_STATUS_PAGE_DEEPLINK = "android-app://com.jar.app/healthInsurance/paymentStatusPage"
        const val PAYMENT_BOTTOM_SHEET_DEEPLINK = "android-app://com.jar.app/healthInsurance/paymentBottomSheet"
        const val INSURANCE_LANDING_SCREEN = "android-app://com.jar.app/healthInsurance/LandingPage"
        const val MANUAL_BUY_GRAPH = "android-app://com.jar.app/manualSavingsTracker"
        const val BUY_GOLD_V2 = "android-app://com.jar.app/buyGoldV2"
        const val DAILY_ONBOARDING_VARIANT_FRAGMENT = "android-app://com.jar.app/dailyInvestOnboardingVariantFragment"
        const val EMI_CALCULATOR = "android-app://com.jar.app/launchingSoonOfferFragment"
        const val SELECT_PLAN_ABANDON_SCREEN_DEEPLINK = "android-app://com.jar.app/healthInsurance/selectPlanAbandonScreen"
    }

    object GiftCardType {
        const val GIFT_QUESTION = 1
        const val CONTACT_CARD = 2
        const val AMOUNT_CARD = 3
    }

    object GoldGiftStatus {
        const val UNCLAIMED = "UNCLAIMED"
        const val GIFT_UNCLAIMED = "GIFT_UNCLAIMED"
    }

    object RoundOffRequestStatus {
        const val ACCEPTED = "ACCEPTED"
        const val PROCESSING = "PROCESSING"
        const val COMPLETED = "COMPLETED"
        const val REQUEST = "REQUEST"
    }

    enum class SavingsSubscriptionSetupType {
        SETUP, UPDATE
    }

    object KycFlowType {
        const val DEFAULT = 0
        const val DELIVERY = 1
        const val SETTINGS = 2
        const val WINNINGS = 3
        const val WITHDRAWAL = 4
        const val PROFILE = 5
        const val NOTIFICATION = 6
        const val SELL_GOLD = 7
        const val MICRO_LOANS_WEB = 8
    }

    const val CDN_BASE_URL = "https://d21tpkh2l1zb46.cloudfront.net"
    const val CDN_BASE_URL_NEW = "https://cdn.myjar.app"

    object BuyGoldVariant {
        const val FAB = "FAB"
        const val CARD = "CARD"
        const val BOTH = "BOTH"
    }

    object LendingKycFromScreen {
        const val PROFILE = "Profile"
        const val LENDING_CARD = "Home"
        const val LENDING_ONBOARDING = "LendingOnboarding"
    }

    object KycFromScreen {
        const val SELL_GOLD = "SellGold"
        const val WINNINGS = "Winnings"
        const val PROFILE = "Profile"
        const val DEEPLINK = "Deeplink"
    }

    object TxnDetailsPosition {
        const val TRANSACTION_STATUS = 1
        const val ROUND_OFF = 2
        const val PRODUCT_DETAILS = 3
        const val WINNINGS_USED = 4
        const val COUPON_CODE = 5
        const val GOLD_GIFTING = 6
        const val TRANSACTION_ROUTINE = 7
        const val TRANSACTION_TRACKING = 8
        const val TRANSACTION_DETAILS = 9
        const val WEEKLY_CHALLENGE = 10
        const val CONTACT_US = 11
        const val SAFE_GOLD_BANNER = 12
        const val GOLD_LEASE_BREAKDOWN = 1
        const val SAVINGS_BREAKDOWN = 1
        const val PAUSE_TXN_DETAILS = 1
    }

    object NewTransactionDetailsPosition {
        const val TRANSACTION_HEADER = 1
        const val TRANSACTION_STATUS = 2
        const val TRANSACTION_ORDER_DETAILS = 3
        const val TRANSACTION_CONTACT_US = 4
    }

    object FilterValues {
        const val DATE_FILTER = "Date"
        const val DATE_FILTER_CUSTOM = "Custom"
        const val FILTER_All = "All"
    }

    object TransactionAdapterPosition {
        const val GOLD = 0
        const val WINNING = 1
    }

    object BreakDownType {
        const val INVESTED = "Invested"
        const val CURRENT = "Current"
        const val WINNINGS = "Winnings"
    }

    object LottieUrls {
        const val TICK = "$CDN_BASE_URL/LottieFiles/Generic/tick.json"
        const val ERROR_EXCLAMATION = "$CDN_BASE_URL/LottieFiles/Lending_Kyc/generic-error.json"
        const val SAD_EMOJI = "$CDN_BASE_URL/LottieFiles/Generic/sad-emoji.json"
        const val CONFETTI = "$CDN_BASE_URL/LottieFiles/Generic/confetti.json"
        const val PROCESSING_RUPEE = "$CDN_BASE_URL/LottieFiles/Generic/processing_rupee.json"
        const val SMALL_CHECK = "$CDN_BASE_URL/LottieFiles/Lending_Kyc/small_check.json"
        const val GOLD_PRICE_DROP = "$CDN_BASE_URL/LottieFiles/GoldPrice/gold-price_drop-new.json"
        const val CONFETTI_FROM_TOP = "$CDN_BASE_URL/LottieFiles/Generic/confetti_from_top.json"
        const val RUPEE_POST_PURCHASE_SUCCESS =
            "$CDN_BASE_URL/LottieFiles/Generic/rupee_post_purchase_success.json"

        const val NOTE_STACK =
            "$CDN_BASE_URL/LottieFiles/GoldSip/note_stack.json"

        const val GOLD_SIP_MONTHLY =
            "$CDN_BASE_URL/LottieFiles/GoldSip/savings_plan_monthly.lottie"
        const val GOLD_SIP_WEEKLY =
            "$CDN_BASE_URL/LottieFiles/GoldSip/savings_plan_weekly.lottie"

        const val DAILY_SAVING_ONBOARDING = "$CDN_BASE_URL/Onboarding/DailySavings.lottie"
        const val DAILY_3_6_12_COIN_STACK =
            "$CDN_BASE_URL/LottieFiles/DailyInvestment/months_3_6_12_coin_stack.json"
        const val HOUR_GLASS_LOADING = "$CDN_BASE_URL/LottieFiles/Generic/hour_glass_loading.lottie"
        const val DUO_MACHINE_HEADER = "$CDN_BASE_URL/LottieFiles/Duo/v2/duo_homepage_v2.json"
        const val DUO_BIKE = "$CDN_BASE_URL/LottieFiles/Duo/v2/bike.json"
        const val DUO_BIKE_SMALL = "$CDN_BASE_URL/LottieFiles/Duo/v2/bike_small_v2.lottie"
        const val GOLD_BAR = "$CDN_BASE_URL/LottieFiles/BuyGold/gold_bar_new.lottie"
        const val CONTACT_SYNCING = "$CDN_BASE_URL/LottieFiles/Duo/contacts_syncing.json"

        const val SWIPE_UP = "$CDN_BASE_URL/Homefeed/swipe_up.lottie"
        const val TREASURE_BOX_SPIN = "$CDN_BASE_URL/LottieFiles/Spins/treasure_popup_no_bg.lottie"

        // weekly challenge lotties
        const val WEEKLY_CHALLENGE_ONBOARDING1 =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/Onboarding1.json"

        const val WEEKLY_CHALLENGE_ONBOARDING2 =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/Onboarding2.json"

        const val WEEKLY_CHALLENGE_ONBOARDING3 =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/Onboarding3.json"

        const val WEEKLY_CHALLENGE_NOTIFICATION_LOOP =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/Notification_Loop_Optimized.json"

        const val WEEKLY_CHALLENGE_8_CARD =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/card_8_pop_OPTIM.json"

        const val WEEKLY_CHALLENGE_5_CARD =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/card_5_pop_OPTIM.json"

        const val WEEKLY_CHALLENGE_12_CARD =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/card_12_pop_OPTIM.json"

        const val WEEKLY_CHALLENGE_IDLE_STATE =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/Idle_State_Optimized_Square.json"
        const val WEEKLY_CHALLENGE_IDLE_STATE_VERTICAL =
            "$CDN_BASE_URL/LottieFiles/weeklyMagic/Idle_State_Optimized_Vertical.json"

        const val WEEKLY_CHALLENGE_HAT_WITHOUT_HIGHLIGHT = "$CDN_BASE_URL/LottieFiles/weeklyMagic/Hat_Idle_NoLights.lottie"

        const val CONFETTI_OPTIMISED = "$CDN_BASE_URL/LottieFiles/Generic/confetti_optimised.lottie"

        const val PULSATING_LOTTIE = "$CDN_BASE_URL/AppWalkthrough/pulsate.json"

        const val PULSATING_SELL_GOLD_LOTTIE =
            "$CDN_BASE_URL/LottieFiles/SellGold/processing_state2.lottie"
        const val CIRCULAR_LOADER_ANIMATION = "$CDN_BASE_URL_NEW/Generic/circular_loader_animation.json"
    }

    object IllustrationUrls {
        const val TICK = "$CDN_BASE_URL/Images/Generic/ic_tick.png"
        const val DEFAULT_COUPON_CODE = "$CDN_BASE_URL/CouponCodes/Default.png"
        const val DEFAULT_LENDING_SERVER_ERROR =
            "$CDN_BASE_URL/lending/illustration/server_time_out_illustrations.png"
        const val LENDING_SELL_GOLD_FLOW =
            "$CDN_BASE_URL/lending/illustration/feature_loan_lending_illustration.png"
        const val JAR_SECURITY_PARTNERS_ASSET =
            "$CDN_BASE_URL/Generic/jar_security_partners_asset.png"
        const val HEALTH_INSURANCE_HOME_SCREEN = "$CDN_BASE_URL_NEW/healthInsurance/homescreen/add.png"
        const val VASOOLI_ONBOARDING = "$CDN_BASE_URL_NEW/Vasooli-Onboarding/vasooli.png"
    }

    enum class StaticContentType {
        REFERRAL_INFO,
        AUTOPAY_GUIDE,
        APP_GUIDE_INFO,
        GENERAL_FAQS,
        BUY_GOLD_OPTIONS,
        GIFT_GOLD_OPTIONS,
        TRANSACTIONS,
        AUTOPAY_SUPPORTED_BANK_INFO,
        GOLD_FAQS,
        WITHDRAW_REASONS,
        PAUSE_SAVING_OPTIONS,
        AVATAR_INFO,
        SMS_FAQS,
        TNC,
        IS_PROMO_CODE_AVAILABLE,
        AUTO_INVEST_FAQS,
        HAMBURGER_ITEMS,
        BUY_GOLD_HELP,
        UPDATE_DS_AMOUNT,
        GOLD_SIP_INFO,
        UPDATE_SIP_ED_INFO,
        PRIMARY_UPI_ID,
        CARD_ORDER,
        ROUND_OFF_STEPS,
        DAILY_SAVINGS_STEPS,
        DAILY_SAVINGS_ABANDON_SCREEN,
        WITHDRAW_HELP,
        DAILY_SAVINGS_BENEFITS,
        DAILY_SAVINGS_FAQ,
        HELP_VIDEOS,
        KYC_DETAILS,
        KYC_FAQ_LIST,
        EXPERIAN_CONSENT,
        EXPERIAN_TNC,
        WITHDRAW_REASONS_V2,
        SELL_GOLD_PERCENTAGE,
        SELL_GOLD_HELP,
        CUSTOM_ONBOARDING,
        DAILY_SAVINGS_ABANDONED_STEPS,
        ONBOARDING_STORIES,
        DAILY_SAVINGS_ABANDON_SCREEN_V2,
        BUY_GOLD_ONBOARDING
    }

    object ManualPaymentFlowType {
        const val GoldSipManualPayment = "SIP_MANUAL_PAYMENT"
        const val DetectSpendPayment = "DetectSpendPayment"
        const val Roundups_Card = "Roundups Card"
        const val Gifting = "Gifting"
        const val OrderSummary = "OrderSummary"
        const val Milestone = "Milestone"
        const val BUY_GOLD = "Buy Gold"
        const val WEEKLY_MAGIC_FEATURE_SCREEN = "Weekly Magic Feature Screen"
        const val BUY_GOLD_SHUBH_MUHURAT = "Buy Gold - Shubh Muhurat"
        const val GOLD_LEASE_PAYMENT = "GoldLeasePayment"
        const val SinglePageHomeScreenFlow = "SinglePageHomeScreenFlow"



        const val InvestPromptHomeScreen = "InvestPromptHomeScreen"
        const val HomeScreen = "HomeScreen"
        const val PostSetupFlow = "PostSetupFlow"
        const val RoundOffFlow = "RoundOffFlow"
    }

    const val MICRO_LOAN_DETAILS_URL = "https://wiki.myjar.app/web/v1/micro-loan-details/"

    const val REMOTE_FIREBASE_FLAG = "Firebase_flag"

    /**
     * Please make sure when you add buy flow context,
     * the support for same also needs to be added from BE
     * ELSE coupon code and suggested amount APIs will fail,
     * every context have its own implementation, If your flow don't have
     * any specific implementation you can use BUY_GOLD as the default context
     * **/
    object BuyGoldFlowContext {
        const val JACKPOT_SCREEN = "JACKPOT_SCREEN"
        const val WEEKLY_CHALLENGE = "WEEKLY_CHALLENGE"
        const val HOME_SCREEN = "HOME_SCREEN" // coupons shown on home screen
        const val SINGLE_PAGE_HOME_FEED_COUPON = "SINGLE_PAGE_HOME_FEED" // coupons for single page homefeed
        const val BUY_GOLD = "BUY_GOLD"
        const val OFFERS_SECTION = "OFFERS_SECTION"
        const val SPINS_EXHAUSTED =
            "SPINS_EXHAUSTED" // once spins are exhausted, redeem winnings CTA is shown
        const val TRANSACTION_TAB = "TRANSACTION_TAB"
        const val WINNINGS_TAB = "WINNINGS_TAB"
        const val GOLD_PRICE_GRAPH = "GOLD_PRICE_GRAPH"
        const val HAMBURGER_MENU = "HAMBURGER_MENU"
        const val FLOATING_BUTTON = "FLOATING_BUTTON" // cta shown on homescreen
        const val SPENDS_DETECTED_QUICK_ACTION = "SPENDS_DETECTED_QUICK_ACTION"
        const val INVEST_PROMPT_QUICK_ACTION = "INVEST_PROMPT_QUICK_ACTION"
        const val INVEST_PROMPT_HOMEFEED = "INVEST_PROMPT_HOMEFEED"
        const val QUESTS = "QUESTS"
        const val ONBOARDING = "ONBOARDING"
        const val SINGLE_PAGE_HOMEFEED = "SINGLE_PAGE_HOMEFEED"
    }

    object LendingFlowType {
        const val SELL_GOLD = "SELL_GOLD"
        const val HAMBURGER = "HAMBURGER"
        const val HOME_CARD = "HOME_CARD"
        const val KYC_DONE = "KYC_DONE"
    }

    const val SUBSCRIPTION_TYPE_DEFAULT = "DEFAULT"

    const val EMPTY = ""
    const val USER_INVITE_LINK = "userinvitelink"

    const val EXTERNAL_AVAILABLE_STORAGE = "EXTERNAL_AVAILABLE_STORAGE"
    const val INTERNAL_AVAILABLE_STORAGE = "INTERNAL_AVAILABLE_STORAGE"

    const val User_Internet_Details = "User_Internet_Details"
    const val WifiEnabled = "WifiEnabled"
    const val CurrentInternetSpeed = "CurrentInternetSpeed"
    const val GsmNetworkType = "GsmNetworkType"
    const val UiMode = "UiMode"
    const val IsGestureEnabled = "IsGestureEnabled"
    const val ScreenWidthDp = "ScreenWidthDp"
    const val ScreenHeightDp = "ScreenHeightDp"
    const val ScreenDpi = "ScreenDpi"

    const val DEEPLINK_EXTRACTION_KEY = "deeplink"
    const val PUSH_NOTIFICATION_CONTEXT = "context"
    const val PUSH_NOTIFICATION_SOUND = "wzrk_sound"
    const val MAX_RATING_ATTEMPTS = 2
    const val TAB_SELECTED = "TAB_SELECTED"
    const val APPSFLYER_REFERRALS = "referrals"

    const val NO_OF_ONBOARDING_SCREENS = 4
    const val NO_OF_ONBOARDING_SCREENS_IN_EXPERIMENT = 3

    const val SIGNUP = "SIGNUP"
    const val LOGIN = "LOGIN"

    const val truecallerShown = "truecallerShown"
    const val otpWhatsAppShown = "otpWhatsAppShown"
    const val EXPERIAN_CONSENT = "EXPERIAN_CONSENT"
    const val PrakashRaj = "PrakashRaj"
    const val storyType = "storyType"
    const val DEFAULT_COUNTRY_CODE = "91"
    const val TRUECALLER_PACKAGE = "com.truecaller"

    const val creditConsentShown = "creditConsentShown"
    const val RESULT_NONE = 1001
    const val storyNum = "storyNum"
    const val FlowType = "FlowType"
    const val recommendedValues = "recommended values"
    const val popularAmount = "popular amount"
    const val suggestedAmount = "suggested amount"
    const val CancellationPage = "Cancellation Page"
    const val AmountBar = "Amount Bar"
    const val setup_flow_type = "setup_flow_type"
    const val is_suggested_amount = "is_suggested_amount"
    const val is_recommended_amount = "is_recommended_amount"
    const val Updated_daily_savings_amount = "Updated_daily_savings_amount"
    const val Old_daily_Savings_amount = "Old_daily_Savings_amount"
    const val UpdateAmount = "UpdateAmount"
    const val VALUE_TRUE = "true"
    const val VALUE_FALSE = "false"


    //DO NOT CHANGE THE VALUE :)
    const val WEB_APP_INTERFACE = "Android"

    const val REMOTE_CONFIG_FORCE_REFRESH_TOPIC = "REMOTE_CONFIG_FORCE_REFRESH_TOPIC"
    const val REMOTE_CONFIG_FORCE_REFRESH_KEY = "REMOTE_CONFIG_FORCE_REFRESH_KEY"
    const val FIRST_NAME = "FIRST_NAME"

    object GoldLeaseFlowType {
        const val HOME_CARD = "HOME_CARD"
        const val NEW_GOLD_FRAGMENT = "NEW_GOLD_FRAGMENT"
    }

    object DSPreAutoPayFlowType {
        const val SETUP_DS = "SETUP_DS"
        const val UPDATE_DS = "UPDATE_DS"
        const val POST_SETUP_DS = "POST_SETUP_DS"
    }

    object AccountAdapterPosition {
        const val PROFILE = 0
        const val SETTINGS = 1
    }

    object GoldLeaseTabPosition {
        const val TAB_NEW_LEASE = 0
        const val TAB_MY_ORDERS = 1
    }

    object AnimationOperation {
        const val START = "START"
        const val PAUSE = "PAUSE"
        const val RESUME = "RESUME"
        const val STOP = "STOP"

    }

    object HandleDeeplinkFlowSource {
        const val APPS_FLYER_DEEPLINK_LISTENER = "APPS_FLYER_DEEPLINK_LISTENER"
        const val ONBOARDING_STATE_MACHINE = "ONBOARDING_STATE_MACHINE"
        const val FB_APP_LINK_DATA = "FB_APP_LINK_DATA"
        const val HANDLE_KNOW_MORE_CTA_VIDEO = "HANDLE_KNOW_MORE_CTA_VIDEO"
        const val HANDLE_KNOW_MORE_CTA_DEEPLINK = "HANDLE_KNOW_MORE_CTA_DEEPLINK"
        const val HANDLE_DEEPLINK_FROM_INTENT = "HANDLE_DEEPLINK_FROM_INTENT"
        const val HANDLE_DEEPLINK_FROM_INTENT_DATA = "HANDLE_DEEPLINK_FROM_INTENT_DATA"
        const val DEEPLINK_EVENT_BUS = "HANDLE_DEEPLINK_EVENT_BUS"
        const val REDIRECTION_FROM_DEEPLINK_TO_HOME_EVENT_BUS = "HANDLE_REDIRECTION_FROM_DEEPLINK_TO_HOME_EVENT_BUS"
        const val CHECK_PENDING_DEEPLINK_EVENT_BUS = "CHECK_PENDING_DEEPLINK_EVENT_BUS"
        const val UPDATE_AUTOPAY_EVENT_BUS_ROUND_OFF_ENABLED = "UPDATE_AUTOPAY_EVENT_BUS_ROUND_OFF_ENABLED"
        const val UPDATE_AUTOPAY_EVENT_BUS_ROUND_OFF_NOT_ENABLED = "UPDATE_AUTOPAY_EVENT_BUS_ROUND_OFF_NOT_ENABLED"
    }

    object CancellationFlowVersion {
        const val v1 = "v1"
        const val v2 = "v2"
    }

    enum class ScreenFlowType {
        HOME_CARD,
        HAMBURGER,
        SETTINGS_SCREEN,
        DISABLED_DS_SCREEN,
        PRE_DAILY_SAVING_SETUP_SCREEN,
        DAILY_SAVING_ONBOARDING_STORIES_SCREEN,
    }

    object DailySavingUpdateFlow {
        const val HOME = "HOME"
        const val QUICK_ACTIONS = "QUICK_ACTIONS"
        const val POST_SETUP = "POST_SETUP"
    }

    object OnboardingVariants{
        const val ONBOARDING_STORY_0= "Onboarding-story-0"
        const val ONBOARDING_STORY_1= "Onboarding-story-1"
        const val ONBOARDING_STORY_2= "Onboarding-story-2"
    }

    object QuestFlowConstants {
        const val QUESTS = "QUESTS"
        const val QUEST_DIALOG_ACTION = "QUEST_DIALOG_ACTION"
        const val DIALOG_ACTION_TYPE = "DIALOG_ACTION_TYPE"
        const val DIALOG_ACTION_DISMISS = "ACTION_DISMISS"
        const val DIALOG_ACTION_GO_TO_QUEST = "ACTION_GO_TO_QUEST"

        const val QUEST_BRAND_COUPON = "QUEST_BRAND_COUPON"

        object QuestType {
            const val QUIZ = "QUIZ"
            const val SPINS = "SPINS"
        }
    }

    object SellGoldFlow {
        const val FROM_SELL_GOLD_REVAMP = "SellGoldRevamp"
        const val FROM_SELL_GOLD_REVAMP_PAN_ONLY = "SellGoldRevampPanOnly"
    }

    object FromScreenFlows{
        const val HOME_SCREEN = "homeScreen"
        const val DYNAMIC_FEATURE_REDIRECTION = "dynamicFeatureRedirection"
    }

    object WebViewFlowType {
        const val NO_CHROME_TAB_INSTALLED = "NoChromTabInstalled"
        const val IN_APP_HELP = "InAppHelp"
        const val VIBA_WEB_PAGE = "VibaWebPage"
        const val INSURANCE_HELP = "InsuranceHelpAndSupport"
        const val DEEPLINK = "Deeplink"
        const val STATIC_INFO = "StaticInfo"
        const val INSURANCE_LANDING = "InsuranceLanding"
    }

    object ImageUrlConstants {
        const val BG_PATTERN_TYPE_FOUR = "https://cdn.myjar.app/static/app/images/bg_pattern_type_four.webp"
        const val DELIVERY_BOTTOM = "https://cdn.myjar.app/static/app/images/delivery_bottom.webp"
        const val DELIVERY_BANNER = "https://cdn.myjar.app/static/app/images/delivery_banner.webp"
        const val ERROR_BG = "https://cdn.myjar.app/static/app/images/error_bg.webp"
        const val FEATURE_BUY_GOLD_V2_IMAGE_AUSPICIOUS_SHARE = "https://cdn.myjar.app/static/app/images/feature_buy_gold_v2_image_auspicious_share.webp"
        const val FEATURE_DAILY_INVESTMENT_SELECTED_JAR1 = "https://cdn.myjar.app/static/app/images/feature_daily_investment_selected_jar1.webp"
        const val FEATURE_DUO_BG_DUO_SPIRAL = "https://cdn.myjar.app/static/app/images/feature_duo_bg_duo_spiral.webp"
        const val FEATURE_REFER_EARN_SHARE_IMAGE = "https://cdn.myjar.app/static/app/images/feature_refer_earn_share_image.webp"
        const val FEATURE_SETTINGS_IC_JAR_SECURITY_SHIELD = "https://cdn.myjar.app/static/app/images/feature_settings_ic_jar_security_shield.webp"
        const val FEATURE_VASOOLI_NANA = "https://cdn.myjar.app/static/app/images/feature_vasooli_nana.webp"
        const val GROUP_USER_LOGO_V2 = "https://cdn.myjar.app/static/app/images/group_user_logo_v2.webp"
        const val IC_REFER_AND_EARN = "https://cdn.myjar.app/static/app/images/ic_refer_and_earn.webp"
        const val IC_SMALL_SPARKLING_BG = "https://cdn.myjar.app/static/app/images/ic_small_sparkling_bg.webp"
        const val IMAGE_AUSPICIOUS_SHARE = "https://cdn.myjar.app/static/app/images/image_auspicious_share.webp"
        const val SPARKLE_LIGHT_BG = "https://cdn.myjar.app/static/app/images/sparkle_light_bg.webp"
        const val SPINS_BG = "https://cdn.myjar.app/static/app/images/spins_bg.webp"
        const val STOP_LOCKER = "https://cdn.myjar.app/static/app/images/stop_locker.webp"
    }

    const val DEFAULT_VALUES_FOR_NO_OF_SMS_SYNC = 180
    const val LENDING_PROGRESS_CARD_FEATURE_TYPE = "progress"
    const val ONBOARDING = "ONBOARDING"
    const val GOAL_BASED_SAVING_DEEPLINK = "dl.myjar.app/savingsGoal"
    const val EXIT_SURVEY_DEEPLINK = "dl.myjarapp/featureExitSurvey"
    const val SUCCESSFULLY_SUBMITTED_EXIT_SURVEY = "successfully_submitted_exit_survey"
}