package com.jar.app.core_analytics

object EventKey {
    const val MethodCallSource = "MethodCallSource"
    const val IsLoggedIn = "IsLoggedIn"
    const val DefferedDeeplink = "DefferedDeeplink"
    const val IsDeeplinkHandlingPending = "IsDeeplinkHandlingPending"
    const val AMOUNT: String = "Amount"
    const val NAME = "name"
    const val AGE = "age"
    const val GENDER = "gender"
    const val TYPE = "type"
    const val REWARD_WON = "RewardWon"
    const val RESPONSE = "Response"
    const val ERROR = "Error"
    const val PROP_SOURCE = "source"
    const val OPTION_SELECTED = "Option selected"
    const val PROP_STATUS = "Status"
    const val ALLOWED = "allowed"
    const val DENIED = "denied"
    const val NEVER_ASK_AGAIN = "never ask again"
    const val PROP_VALUE = "Value"
    const val HOME_SCREEN = "HomeScreen"
    const val REFERRED_COUNT = "Referred Count"
    const val IS_PAYTM_INSTALLED = "isPaytmInstalled"
    const val VARIANT = "VARIANT"
    const val NEW = "NEW"
    const val TIME_STAMP = "timeStamp"
    const val variants = "variants"
    const val state = "state"
    const val platform = "platform"
    const val started = "started"
    const val ended = "ended"
    const val skipped = "skipped"
    const val value = "value"
    const val manual ="manual"
    const val SMS_PERMISSION_NOT_REQUIRED = "SMS_PERMISSION_NOT_REQUIRED"
    const val IS_FROM_CACHE = "IS_FROM_CACHE"
    const val TIME_IT_TOOK = "TIME_IT_TOOK"

    //screens launch keys
    const val APP_LAUNCHED = "App_Launched_New"

    const val AuthType = "authType"
    const val OTP = "OTP"
    const val TRUECALLER = "Truecaller"
    const val OLD_USER_LOGIN = "old_user_login"
    const val NEW_USER_SIGNUP = "new_user_signup"
    const val NEW_USER_SIGNUP_MOBILE = "new_user_signup_mobile"
    const val AUTH_SUCCESSFUL = "auth_successful"
    const val CompletedOnboarding = "CompletedOnboarding"
    const val CustomOnboardingScreen = "CustomOnboardingScreen"
    const val Deeplink = "Deeplink"
    const val HANDLE_DEEPLINK = "HandleDeeplink"

    //Buy Price Home Screen Pill Events
    const val ShownGoldLivePrice_HomeScreen = "ShownGoldLivePrice_HomeScreen"

    // Logout events
    const val LogoutConfirmationDialog = "LogoutConfirmationDialog"
    const val LogoutUnusualActivity = "LogoutUnusualActivity"
    const val LogoutEditProfileCelebration = "LogoutEditProfileCelebration"
    const val LogoutRefreshTokenExpired = "LogoutRefreshTokenExpired"

    //HomeScreen BottomSheet PromptEvents
    const val Bottomsheet_HomeScreen_PromptShown = "Bottomsheet_HomeScreen_PromptShown"
    const val Bottomsheet_HomeScreen_PromptClosed = "Bottomsheet_HomeScreen_PromptClosed"
    const val Bottomsheet_HomeScreen_PromptClicked = "Bottomsheet_HomeScreen_PromptClicked"

    const val Clicked_SettingsTab_profileScreen = "Clicked_SettingsTab_profileScreen"

    const val ClickedInvestMore_GoldLivePrice_Homescreen =
        "ClickedInvestMore_GoldLivePrice_Homescreen"
    const val Clicked_BackArrowButton_InAppHelp = "Clicked_BackArrowButton_InAppHelp"
    const val CLICKEDANOTHERMOBILENUMBER_ONBOARDING = "ClickedAnotherMobileNumber_Onboarding"
    const val CLICKEDSPINTHEWHEELBUTTON = "ClickedSpinTheWheelButton"
    const val SHOWNSPINREWARDSSCREEN = "ShownSpinRewardsScreen"
    const val CLIKEDSHARESPINREWARDS = "ClikedShareSpinRewards"
    const val NOTIFICATION_CLICKED = "Notification_Clicked"
    const val TOGGLEDPREMINDERSETUP_SETTINGSSCREEN = "ToggledReminderSetup_SettingsScreen"
    const val FETCHSMS_STARTED = "FetchSmS_Started"

    const val CLICKED_YES_LOGOUT = "Success_UserLoggedOut"

    const val INVOICE_CLICKED = "InvoicesClicked"
    const val SHOWN_SPIN_BOTTLE = "ShownSpinTheWheelScreen"

    const val CLICKED_CLOSE_AMOUNT_BREAKDOWN = "ClickedCloseinMoreInfo_GoldBreakdown"
    const val SESSION_OPEN_DEEP_LINK = "SessionOpen_DeepLink"
    const val CLICKED_VIEW_REFERRAL_BUTTON = "ClickedViewReferralsButton"
    const val SHARE_WHATSAPP_REFERRAL_SCREEN = "ClickedShareViaWhatsapp_ReferralScreen"
    const val CLICKED_OPTION_PARTIAL_PAYMENT_SCREEN = "ClickedOption_PartialPaymentsScreen"
    const val SHOWN_SURVEY = "ShownSurveyPopup"
    const val SurveyQuestion = "Survey Question"
    const val SurveyAnswer = "Survey Answer"
    const val CLICKED_SURVEY_OPTION = "ClickedOption_SurveyPopUp"
    const val ClICKED_SUBMIT_SURVEY = "ClickedSubmitAnswers_SurveyPopUp"
    const val CLICKED_HOME_BOTTOM_NAV = "ClickedHomeTab_BottomNav"
    const val Clicked_Account = "Clicked_Account"
    const val CLICKED_GOLD_LIVE_PRICE_HOME = "ClickedGoldLivePrice_HomeScreen"
    const val BACK_BUTTON_PRESSED = "BackButtonPressed"
    const val EXIT_CLICKED = "ClickedMaybeLater_BottomScreen_Onboarding"
    const val CONTINUE_CLICKED = "ClickedSignUp_BottomScreen_Onboarding"
    const val ClickedCrossButton_BottomScreen_Exit_Onboarding =
        "ClickedCrossButton_BottomScreen_Exit_Onboarding"

    const val JarSecurityShieldToggled_SettingsScreen = "JarSecurityShieldToggled_SettingsScreen"
    const val PaymentInitiated = "PaymentInitiated"
    const val FromScreen = "fromScreen"
    const val FromSection = "FromSection"
    const val FromCard = "FromCard"
    const val Screen = "Screen"
    const val Amount = "Amount"
    const val Is_New_Flow = "Is_New_Flow"
    const val New_hint_flow = "New_hint_flow"
    const val Old_hint_flow = "Old_hint_flow"
    const val SCENARIO = "scenario"

    const val FirstInvestment = "First_Investment"
    const val PromoCode = "promocode"

    const val CLICKED_GOLD_LIVE_PRICE_LATER_BOTTOM_SHEET = "ClickedDoItLater_LivePriceBottomSheet"
    const val SHOWN_RATE_US_DIALOG_PS = "ShownRateUsDialog_PS"

    const val SubhMuhurtDatesShareWithFriends_BuyGoldScreen =
        "SubhMuhurtDatesShareWithFriendsClicked_BuyGoldScreen"

    const val TIME_SPENT = "timeSpent"
    const val BUTTON_TEXT = "buttonText"
    const val NUMBER_DETECTED = "NUMBER_DETECTED"
    const val MESSAGE = "message"
    const val TEXT = "text"
    const val ENABLED = "enabled"
    const val NUMBER = "number"
    const val Is_checked = "Is_checked"
    const val SHOWN_SPLASH_SCREEN = "Shown_SplashScreen"
    const val EXIT_SPLASH_SCREEN = "Exit_SplashScreen"
    const val CLICKED_START_NOW_ONBOARDING = "Clicked_StartNow_Onboarding"
    const val ShownInternetErrorMessage = "ShownInternetErrorMessage"
    const val Shown_BottomScreen_Exit_Onboarding = "Shown_BottomScreen_Onboarding"
    const val Clicked_experian_checkbox = "Clicked_experian_checkbox"
    const val ClickedEnterPhoneNumber_LoginScreen_Onboarding =
        "ClickedEnterPhoneNumber_LoginScreen_Onboarding"
    const val ClickedGetOTP_LoginScreen_Onboarding = "ClickedGetOTP_LoginScreen_Onboarding"
    const val ClickedTermsAndConditions_LoginScreen_Onboarding =
        "ClickedTermsAndConditions_LoginScreen_Onboarding"
    const val ClickedPrivacyPolicy_LoginScreen_Onboarding =
        "ClickedPrivacyPolicy_LoginScreen_Onboarding"
    const val ClickedLanguageButton_LoginScreen_Onboarding =
        "ClickedLanguageButton_LoginScreen_Onboarding"
    const val Shown_Truecaller_Onboarding = "Shown_Truecaller_Onboarding"
    const val Clicked_ContinueWithTruecaller_Onboarding =
        "Clicked_ContinueWithTruecaller_Onboarding"
    const val Clicked_AnotherMobileNumber_Onboarding = "Clicked_AnotherMobileNumber_Onboarding"
    const val Exit_Truecaller_Onboarding = "Exit_Truecaller_Onboarding"
    const val Shown_PhoneNumberSuggestion_Onboarding = "Shown_PhoneNumberSuggestion_Onboarding"
    const val Initiated_newphonenumberhint = "Initiated_newphonenumberhint"
    const val Shown_newphonenumberhint = "Shown_newphonenumberhint"
    const val ClickedNumber_PhoneNumberSuggestion_Onboarding =
        "ClickedNumber_PhoneNumberSuggestion_Onboarding"
    const val Dismissed_PhoneNumberSuggestion_Onboarding =
        "Dismissed_PhoneNumberSuggestion_Onboarding"
    const val Shown_ContinueScreen_Onboarding = "Shown_ContinueScreen_Onboarding"
    const val ClickedContinue_ContinueScreen_Onboarding =
        "ClickedContinue_ContinueScreen_Onboarding"
    const val ClickedAnotherNumber_ContinueScreen_Onboarding =
        "ClickedAnotherNumber_ContinueScreen_Onboarding"
    const val ClickedTermsAndConditions_ContinueScreen_Onboarding =
        "ClickedTermsAndConditions_ContinueScreen_Onboarding"
    const val ClickedPrivacyPolicy_ContinueScreen_Onboarding =
        "ClickedPrivacyPolicy_ContinueScreen_Onboarding"
    const val ClickedBackButton_OTPScreen_Onboarding = "ClickedBackButton_OTPScreen_Onboarding"
    const val ShownToolTip_OTPScreen_Onboarding = "ShownToolTip_OTPScreen_Onboarding"
    const val OTP_SMSDetected = "OTP_SMSDetected"
    const val ClickedVerify_OTPScreen_Onboarding = "ClickedVerify_OTPScreen_Onboarding"
    const val ShownErrorMessage_OTPScreen_Onboarding = "ShownErrorMessage_OTPScreen_Onboarding"
    const val ClickedResendOTP_OTPScreen_Onboarding = "ClickedResendOTP_OTPScreen_Onboarding"
    const val ClickedCallToVerify_OTPScreen_Onboarding = "ClickedCallToVerify_OTPScreen_Onboarding"
    const val ClickedBackButton_SelectGenderScreen_Onboarding =
        "ClickedBackButton_SelectGenderScreen_Onboarding"
    const val ClickedSupportLink_OTPScreen_Onboarding = "ClickedSupportLink_OTPScreen_Onboarding"
    const val ClickedViewOtpOnWhatsapp_OTPScreen_Onboarding =
        "ClickedViewOtpOnWhatsapp_OTPScreen_Onboarding"
    const val Shown_EnterNameScreen_Onboarding = "Shown_EnterNameScreen_Onboarding"
    const val ClickedNext_EnterNameScreen_Onboarding = "ClickedNext_EnterNameScreen_Onboarding"
    const val ShownErrorMessage_EnterNameScreen_Onboarding =
        "ShownErrorMessage_EnterNameScreen_Onboarding"
    const val Shown_SelectAgeScreen_Onboarding = "Shown_SelectAgeScreen_Onboarding"
    const val ClickedNext_SelectAgeScreen_Onboarding = "ClickedNext_SelectAgeScreen_Onboarding"
    const val ClickedBackButton_SelectAgeScreen_Onboarding =
        "ClickedBackButton_SelectAgeScreen_Onboarding"
    const val Shown_SelectGenderScreen_Onboarding = "Shown_SelectGenderScreen_Onboarding"
    const val ClickNext_SelectGenderScreen_Onboarding = "ClickedNext_SelectGenderScreen_Onboarding"
    const val Shown_PermissionScreen_Onboarding = "Shown_PermissionScreen_Onboarding"
    const val ClickedGrantPermission_PermissionScreen_Onboarding =
        "ClickedGrantPermission_PermissionScreen_Onboarding"
    const val ClickedFAQ_PermissionScreen_Onboarding = "ClickedFAQ_PermissionScreen_Onboarding"
    const val ClickedPermission_Onboarding = "ClickedPermission_Onboarding"
    const val ShownFAQ_PermissionScreen_Onboarding = "ShownFAQ_PermissionScreen_Onboarding"
    const val ShownError_PermissionScreen_Onboarding = "ShownError_PermissionScreen_Onboarding"
    const val ShownHowScreen_AutoInvestScreen_Onboarding =
        "ShownHowScreen_AutoInvestScreen_Onboarding"
    const val ClickedContinue_LoginScreen_Onboarding = "ClickedContinue_LoginScreen_Onboarding"
    const val Shown_StartNowScreen_Onboarding = "Shown_StartNowScreen_Onboarding"
    const val Shown_StartNowScreen_Onboarding_Ts = "Shown_StartNowScreen_Onboarding_Ts"
    const val Exit_StartNow_Onboarding = "Exit_StartNowScreen_Onboarding"
    const val Exit_BottomScreen_Onboarding = "Exit_BottomScreen_Onboarding"
    const val Shown_LoginScreen_Onboarding = "Shown_LoginScreen_Onboarding"
    const val Exit_LoginScreen_Onboarding = "Exit_LoginScreen_Onboarding"
    const val Exit_LanguageScreen_Onboarding = "Exit_LanguageScreen_Onboarding"
    const val Exit_ContinueScreen_Onboarding = "Exit_ContinueScreen_Onboarding"
    const val Shown_OTPScreen_Onboarding = "Shown_OTPScreen_Onboarding"
    const val Exit_OTPScreen_Onboarding = "Exit_OTPScreen_Onboarding"
    const val Exit_EnterNameScreen_Onboarding = "Exit_EnterNameScreen_Onboarding"
    const val Exit_SelectAgeScreen_Onboarding = "Exit_SelectAgeScreen_Onboarding"
    const val Exit_SelectGenderScreen_Onboarding = "Exit_SelectGenderScreen_Onboarding"

    const val Exit_PermissionScreen_Onboarding = "Exit_PermissionScreen_Onboarding"
    const val Shown_PromoCodeScreen = "Shown_PromoCodeScreen"
    const val ClickedApply_PromoCodeScreen = "ClickedApply_PromoCodeScreen"
    const val Shown_RewardCredited_PromoCodeScreen = "Shown_RewardCredited_PromoCodeScreen"
    const val Exit_PromocodeScreen = "Exit_PromocodeScreen"
    const val ClickedLanguageButton_ContinueScreen_Onboarding =
        "ClickedLanguageButton_ContinueScreen_Onboarding"
    const val ClickedBackButton_PromoCodeScreen = "ClickedBackButton_PromoCodeScreen"
    const val Clicked_UseTruecaller_Onboarding = "Clicked_UseTruecaller_Onboarding"
    const val AppsFlyer_Attribution_Details = "Appsflyer_Attribution_Details"
    const val FinalState = "FinalState"
    const val TransactionType = "TransactionType"
    const val TransactionId = "TransactionId"
    const val Winnings_Status = "Winnings_Status"
    const val TransactionStatus = "TransactionStatus"
    const val NotificationForSdk33 = "NotificationForSdk33"
    const val NewSinglePage_AmountBSLaunched = "NewSinglePage_AmountBSLaunched"
    const val Save_Daily = "Save Daily"
    const val Save_Once = "Save Once"
    const val Coupon_Status = "coupon_status"
    const val DSCancellation_StopDSpopupShown = "DSCancellation_StopDSpopupShown"
    const val DailyInvestmentStatusScreen = "DailyInvestmentStatusScreen"
    const val DailyInvestmentStatusScreenSource = "Source"
    const val is_Permanently_Cancel_flow = "is_Permanently_Cancel_flow"

    const val Current_Running_Experiments = "Current_Running_Experiments"

    const val KEY = "Key"
    const val Shown_PostOrderScreen_DailySavings_DailySavings =
        "Shown_PostOrderScreen_DailySavings_DailySavings"

    const val Clicked_BottomNavTab = "Clicked_BottomNavTab"

    const val CardType = "CardType"
    const val FeatureType = "FeatureType"
    const val Data = "Data"

    const val Clicked_dynamicCard = "Clicked_dynamicCard"
    const val Clicked_EndIcon_dynamicCard = "Clicked_EndIcon_dynamicCard"

    const val Shown_Pricedropalert = "Shown_Pricedropalert"

    const val Action = "Action"
    const val Shown = "Shown"
    const val Goals = "Goals"
    const val number_of_goals_selected = "number_of_goals_selected"
    const val customOnboardingDeeplink = "customOnboardingDeepLink"
    const val isRequiredUpiAppsInstalled = "isRequiredUpiAppsInstalled"
    const val should_show_custom_onboarding_based_on_upi_apps = "should_show_custom_onboarding_based_on_upi_apps"
    const val SavingGoalOnboarding = "SavingGoalOnboarding"
    const val BackButtonPressed = "BackButtonPressed"
    const val Shown_selectGoalScreen_Onboarding = "Shown_selectGoalScreen_Onboarding"
    const val Click_GoalScreen_Onboarding = "Click_GoalScreen_Onboarding"

    const val ClickedNext_SelectGoalScreen_Onboarding = "ClickedNext_SelectGoalScreen_Onboarding"
    const val Shown_HelpVideoList_Homefeed = "Shown_HelpVideoList_Homefeed"

    const val IS_NEW_BUY_GOLD_FLOW = "is_new_buy_gold_flow"
    const val CURRENT_USER_LANGUAGE = "current_user_language"

    const val NEW_USER_CHECK = "New_User_Check"

    const val InAppRatingPopup_Shown = "InAppRatingPopup_Shown"

    const val Shown_OtherSignup_Options = "Shown_OtherSignup_Options"

    const val timeSpentOnboarding = "timeSpentOnboarding"
    const val noOfScreensShown = "noOfScreensShown"
    const val onboardingScreens = "onboardingScreens"
    const val Shown_homeScreen_onboardingCompleted = "Shown_homeScreen_onboarding_Completed"
    const val Shown_homeScreen_onboardingFailed = "Shown_homeScreen_onboarding_Failed"
    const val Withdrawal_ID_Verification = "Withdrawal_ID_Verification"

    const val DSCancellation_NewUserEducationStoriesShown =
        "DSCancellation_NewUserEducationStoriesShown"
    const val DSCancellation_NewUserEducationStoriesClicked =
        "DSCancellation_NewUserEducationStoriesClicked"
    const val Shown_FrequencySavingsStory_Onboarding = "Shown_FrequencySavingsStory_Onboarding"
    const val Shown_startnowcta_Onboardingstories = "Shown_startnowcta_Onboardingstories"
    const val Next_Btn = "Next_Btn"
    const val ONBOARDING_RESOURCE_READY = "Onboarding_Resource_Ready"
    const val ONBOARDING_API_RESPONSE_RECEIVED = "Onboarding_API_response_receieved"
    const val ONBOARDING_API_RESPONSE_ERROR = "Onboarding_API_response_error"
    const val ClickedWithdraw_BSClicked = "ClickedWithdraw_BSClicked"
    const val ClickedWithdraw_BSShown = "ClickedWithdraw_BSShown"
    const val Button_Type = "Button_Type"
    const val Close = "close"
    const val DSSetup_ShownSuccessAnimation = "DSSetup_ShownSuccessAnimation"
    const val ERROR_MESSAGE = "Error_Message"

    object UserLifecycles {
        const val Onboarding = "Onboarding"
        const val HomeFeed = "HomeFeed"
        const val Settings = "Settings"
        const val TransactionsTab = "TransactionsTab"
        const val WebFlow = "WebFlow"
        const val Hamburger = "Hamburger"
        const val FirstCoin = "FirstCoin"
        const val DS_Setup_Success = "DS Setup Success"
        const val InAppNotifications = "InAppNotifications"
    }

    object AutopayBottomSheet {
        const val Shown_AutopayBottomSheet = "Shown_AutopayBottomSheet"
        const val Clicked_AutopayBottomSheet = "Clicked_AutopayBottomSheet"
        const val Clicked_AutopayBottomSheetClose = "Clicked_AutopayBottomSheetClose"
        const val CloseType = "CloseType"
        const val CloseTypeIcon = "CloseIcon"
        const val CloseTypeDrag = "CloseTypeDrag"
        const val State = "State"
        const val Button = "Button"
        const val MysteryCard = "Mystery card"
        const val MysteryCardHero = "Mystery card Hero"
        const val NoMysteryCard = "No mystery card"
        const val WeeklymagicWon = "Weekly magic won"
        const val Spin_Count = "spins_count"
        const val WeeklyMagic = "Weekly magic"
        const val WeeklyMagicNew = "Weekly magic New"
        const val OnlyWeeklyMagic = "Only_WeeklyMagic"
        const val OnlyWeeklyMagicNew = "Only_WeeklyMagicNew"
        const val OnlMysteryCard = "Only_MysteryCard"
        const val OnlMysteryCardHero = "Only_MysteryCardHero"
        const val SpinMysteryCard = "SpinMysteryCard"
        const val MysteryCardSpin = "MysteryCardSpin"
        const val WeeklyMagicSpin = "WeeklyMagicSpin"
        const val SpinWeeklyMagic = "SpinWeeklyMagic"
    }


    object TransactionsV2 {
        const val Clicked_Transactions = "Clicked_Transactions"
        const val Clicked_Locker_ManageLease = "Clicked_Locker_ManageLease"
        const val Shown_GoldTransactionScreen = "Shown_GoldTransactionScreen"
        const val Clicked_WinningsTab_GoldTransactionScreen =
            "Clicked_WinningsTab_GoldTransactionScreen"
        const val Clicked_SellGold_GoldTransactionScreen = "Clicked_SellGold_GoldTransactionScreen"
        const val Clicked_SendGift_GoldTransactionScreen = "Clicked_SendGift_GoldTransactionScreen"
        const val Clicked_Locker_GoldTransactionScreen = "Clicked_Locker_GoldTransactionScreen"
        const val Clicked_Filter_GoldTransactionScreen = "Clicked_Filter_GoldTransactionScreen"
        const val Clicked_Help_GoldTransactionScreen = "Clicked_Help_GoldTransactionScreen"
        const val Clicked_TransactionCard_GoldTransactionScreen =
            "Clicked_TransactionCard_GoldTransactionScreen"
        const val Clicked_InvestedValue_GoldTransactionScreen =
            "Clicked_InvestedValue_GoldTransactionScreen"
        const val Shown_WinningsScreen = "Shown_WinningsScreen"
        const val Clicked_ConvertToGold_WinningsScreen = "Clicked_ConvertToGold_WinningsScreen"
        const val Clicked_Locker_WinningsScreen = "Clicked_Locker_WinningsScreen"
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
        const val Shown_TransactionDetailsBottomSheet = "Shown_TransactionDetailsBottomSheet"
        const val Clicked_TransactionDetailsBS_FailedPayment =
            "Clicked_TransactionDetailsBS_FailedPayment"
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
        const val Chevron = "Chevron"
        const val GoldInLocker = "Gold in Locker"
        const val Clicked_MyWinnings_WinningsScreenChevron =
            "Clicked_MyWinnings_WinningsScreenChevron"
        const val Clicked_TotalWinningsReceived_WinningsScreen =
            "Clicked_TotalWinningsReceived_WinningsScreen"
        const val Clicked_ActionButton_WinningsScreen = "Clicked_ActionButton_WinningsScreen"
        const val Clicked_Close_Winningsbreakdown = "Clicked_Close_Winningsbreakdown"
        const val Button = "Button"

        object paramters {
            const val cardIncluded = "cardIncluded"
            const val status = "status"
            const val amount = "amount"
            const val gold_amount = "gold_amount"
            const val button_type = "button_type"
        }

        object values {
            const val WeeklyMagicCard = "Weekly Magic Card"
            const val shown = "shown"
            const val back = "back"
            const val cross = "cross"
        }
    }

    object Lending {
        const val isFromLending = "isFromLending"
    }

    const val Logout_Event_Updated = "Logout_Event_Updated"
    const val AvailableUpiApps = "AvailableUpiApps"

    const val USER_EXPERIOR_SESSION_STARTED = "USER_EXPERIOR_SESSION_STARTED"
    const val SESSION_URL = "SESSION_URL"
}