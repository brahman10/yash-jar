package com.jar.app.feature_homepage.shared.util

object HomeConstants {

    object HomeCardFeatureType {
        const val LOAN_CARD = "loan_card_"
    }

    object Urls {
        const val DIWALI_HOME_VASOOLI_FLOWER_DECORATION =
            "/Images/DIWALI_2022/vasooli_flower_decoration.png"
        const val DIWALI_HOME_REFER_AND_EARN = "/Images/DIWALI_2022/refer_and_earn.png"
        const val DIWALI_HOME_LIGHTING_DECORATION =
            "/Images/DIWALI_2022/lighining_decoration_new.png"
        const val DIWALI_SINGLE_LAMP_DECORATION = "/Images/DIWALI_2022/lamp_decoration.png"
        const val DIWALI_HOME_FLOWER_DECORATION = "/Images/DIWALI_2022/flower_decoration_new.png"
        const val DIWALI_HOME_DUO_3d_LOGO = "/Images/DIWALI_2022/duo_3d_logo.png"
        const val DIWALI_HOME_BG_LOCKER_CARD = "/Images/DIWALI_2022/bg_locker.png"
        const val DIWALI_HOME_BG_GOLD = "/Images/DIWALI_2022/bg_gold_delivery.png"
        const val DIWALI_HOME_BG_GOLD_DELIVERY_CARD =
            "/Images/DIWALI_2022/bg_gold_delivery_card.png"
        const val DIWALI_HOME_BG_AUTO_SAVE = "/Images/DIWALI_2022/bg_auto_save.png"
        const val DIWALI_HOME_DIYA = "/Images/DIWALI_2022/diya.png"
        const val DIWALI_GOLD_DELIVERY_BOX = "/Images/DIWALI_2022/gold_delivery_box.png"
        const val DIWALI_BG_RANGOLI_TYPE_1 = "/Images/DIWALI_2022/bg_rangoli_type_1.png"
        const val DIWALI_BG_RANGOLI_TYPE_2 = "/Images/DIWALI_2022/rangaolirangoli_type_2.png"
        const val DIWALI_BG_REFER_AND_EARN = "/Images/DIWALI_2022/bg_refer_and_earn.png"
        const val DIWALI_HOME_HEADER_DECORATION_NEW =
            "/Images/DIWALI_2022/home_header_decoration_new.png"
        const val DIWALI_LAMP_DECORATION = "/Images/DIWALI_2022/lamp_decoration.png"

        const val JAR_DUO_LOGO = "/HamburgerMenuIcons/jar_duo.png"
        const val daily_saving_v2 = "daily_saving_v2"
        const val BHIM = "in.org.npci.upiapp"
    }

    enum class QuickActionType {
        CARD, LOCKER
    }

    object HomeFeedCustomViewTag {
        const val firstTransactionBottomContainer = "firstTransactionBottomContainer"
        const val lockerWithdrawal = "lockerWithdrawal"
        const val lockerSaveMore = "lockerSaveMore"
        const val transactionTab = "transactionTab"
    }
    object HomeFeedPosition {
        const val bottomRight = "bottomRight"
        const val topLeft = "topLeft"
    }

    object SinglePageHomeFeed {
        const val showMore = "Show More"
        const val showLess = "Show Less"
    }

    internal object Endpoints {
        const val FETCH_HAMBURGER_DATA = "v2/api/dashboard/static"
        const val FETCH_FEATURES = "v1/api/features"
        const val FETCH_HOME_PAGE_EXPERIMENTS = "v1/api/features/experiments"
        const val FETCH_STATIC_POPUP_INFO = "v2/api/dashboard/static"
        const val FETCH_UPDATE_DAILY_SAVING_AMOUNT_INFO = "v2/api/dashboard/static"
        const val FETCH_PARTNER_BANNER = "v1/api/dashboard/bannerUpdates"
        const val CLAIM_PARTNER_BONUS = "v1/api/dashboard/claim/partner/gift"
        const val FETCH_ROUND_OFF_CARD_DATA = "v1/api/payments/initialRoundoffsData"
        const val FETCH_HOME_STATIC_CARD_ORDERING = "v2/api/dashboard/static"
        const val FETCH_HELP_VIDEOS = "v2/api/dashboard/static"
        const val FETCH_UPCOMING_PRE_NOTIFICATION =
            "v2/api/dashboard/autoDebit/upcomingPreNotification"
        const val DISMISS_UPCOMING_PRE_NOTIFICATION =
            "v2/api/dashboard/autoDebit/dismissPreNotification"
        const val GET_ALL_QUICK_ACTION_CARDS = "v2/api/dashboard/getAllQuickActionCards"
        const val FETCH_FIRST_GOLD_COIN_INTRO = "v1/api/firstGoldCoinIntro"
        const val FETCH_FIRST_GOLD_COIN_LANDING_DATA = "v1/api/firstcoin/landing"
        const val FETCH_FIRST_GOLD_COIN_PROGRESS = "v1/api/firstcoin/progress"
        const val FETCH_FIRST_GOLD_COIN_TRANSITION_PAGE_DATA = "v1/api/firstcoin/staticContent"
        const val FETCH_FIRST_GOLD_COIN_ONBOARDING_DATA = "v1/api/firstcoin/onboarding"
        const val UPDATE_FIRST_COIN_DELIVERY_PROGRESS = "v1/api/firstcoin/delivery/progress"
        const val FETCH_HOME_FEED_ACTIONS = "v1/api/features/homeFeed"

        const val FETCH_DS_CARD_DATA = "v1/api/paytmCashback/showDsCard"

        const val FETCH_HOME_FEED_IMAGES = "v1/api/homefeed/iplLinks"
        const val FETCH_IN_APP_REVIEW_STATUS = "v1/api/inAppRating"
        const val UPDATE_USER_INTERACTION = "v1/api/features/userActions"
        const val SHOULD_SEND_SMS_ON_DEMAND = "v1/api/sms/onDemand"
        const val UPDATE_LOCKER_VIEW_SHOWN = "v2/api/user/locker/view/shown"
        const val FETCH_HOME_SCREEN_BOTTOM_SHEET_PROMPT = "v2/api/user/bottomSheet"
        const val VIBA_HOME_FEED = "v1/api/features/vibaHomeFeed"
        const val FETCH_APP_WALKTHROUGH = "/v1/app/walkthrough"
        const val UPDATE_APP_WALKTHROUGH_COMPLETED = "/v1/app/user/walkthrough"
        const val FETCH_USER_GOLD_BREAKDOWN = "v1/api/dashboard/breakup"
        const val FETCH_BOTTOM_NAV_STICKY_CARD = "v1/api/features/bottomStickyToast"
        const val FETCH_FEATURE_REDIRECTION_DATA = "v1/api/featureRedirection"
    }
}