package com.jar.app.core_preferences.impl

import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_preferences.util.PrefConstants
import com.jar.internal.library.jar_core_preferences.api.PreferenceApi

internal class PrefsApiImpl constructor(
    preferenceApi: PreferenceApi
) : JarPreference(preferenceApi), PrefsApi {


    override suspend fun setStringData(key: String, value: String) {
        write(key, value)
    }

    override suspend fun getStringData(key: String): String? {
        return readNullable(key)
    }

    override fun getInviteLinkExpiry() =
        readNonNullableSync<Long>(PrefConstants.INVITE_LINK_EXPIRY, 0)

    override fun setInviteLinkExpiry(inviteLinkExpiry: Long) =
        writeSync(PrefConstants.INVITE_LINK_EXPIRY, inviteLinkExpiry)

    override fun getUserInviteLink() = readNullableSync<String>(PrefConstants.USER_INVITE_LINK)

    override fun setUserInviteLink(appsFlyerReferralLink: String) =
        writeSync(PrefConstants.USER_INVITE_LINK, appsFlyerReferralLink)

    override fun getAppsFlyerReferralUserId() =
        readNonNullableSync<String>(PrefConstants.APPSFLYER_REFERRAL_USER_ID, "")

    override fun setAppsFlyerReferralUserId(appsFlyerReferralUserId: String) =
        writeSync(PrefConstants.APPSFLYER_REFERRAL_USER_ID, appsFlyerReferralUserId)

    override fun getCurrentLanguageCode(): String {
        val languageCode = readNullableSync<String>(PrefConstants.SELECTED_LANGUAGE_CODE)
        return if (!languageCode.isNullOrBlank())
            languageCode
        else
            PrefConstants.DEFAULT_LANGUAGE_CODE
    }

    override fun getCurrentLanguageName(): String {
        val languageName = readNullableSync<String>(PrefConstants.SELECTED_LANGUAGE_NAME)
        return if (!languageName.isNullOrBlank())
            languageName
        else
            PrefConstants.DEFAULT_LANGUAGE_NAME
    }

    override fun getFCMToken() = readNullableSync<String>(PrefConstants.FCM_TOKEN)

    override fun setFCMToken(fcmToken: String) = writeSync(PrefConstants.FCM_TOKEN, fcmToken)

    override fun setSelectedLanguageCode(code: String) =
        writeSync(PrefConstants.SELECTED_LANGUAGE_CODE, code)

    override fun setSelectedLanguageName(code: String) =
        writeSync(PrefConstants.SELECTED_LANGUAGE_NAME, code)

    override fun isOnboardingComplete() =
        readNonNullableSync<Boolean>(PrefConstants.IS_ONBOARDING_COMPLETE, false)

    override fun setOnboardingComplete() =
        writeSync(PrefConstants.IS_ONBOARDING_COMPLETE, true)

    //The value will remain SIGNUP for new user unless and until he re-logins/clear data/re install
    override fun getAuthType() = readNonNullableSync(PrefConstants.AUTH_TYPE, "")

    override fun setAuthType(type: String) = writeSync(PrefConstants.AUTH_TYPE, type)

    override suspend fun getUserString() = readNullable<String>(PrefConstants.USER_KEY)

    override fun getUserStringSync() = readNullableSync<String>(PrefConstants.USER_KEY)

    override suspend fun setUserString(user: String) = write(PrefConstants.USER_KEY, user)

    override fun setUserStringSync(user: String) = writeSync(PrefConstants.USER_KEY, user)

    override fun getAccessToken() = readNullableSync<String>(PrefConstants.ACCESS_TOKEN)

    override fun isLoggedIn(): Boolean {
        return !getAccessToken().isNullOrBlank()
    }

    override fun isOnBoardingStoryShown() =
        readNonNullableSync(PrefConstants.IS_ONBOARDING_SHOWN, false)

    override fun setOnBoardingStoryShown() =
        writeSync(PrefConstants.IS_ONBOARDING_SHOWN, true)

    override fun setNewOnboardingState(state: String) =
        writeSync(PrefConstants.NEW_ONBOARDING_STATE, state)

    override fun getNewOnboardingState(): String? =
        readNullableSync<String>(PrefConstants.NEW_ONBOARDING_STATE)

    override fun setOldOnboardingState(state: String) =
        writeSync(PrefConstants.OLD_ONBOARDING_STATE, state)

    override fun getOldOnboardingState(): String? =
        readNullableSync<String>(PrefConstants.OLD_ONBOARDING_STATE)

    override fun setAccessToken(token: String) =
        writeSync(PrefConstants.ACCESS_TOKEN, token)

    override fun getRefreshToken() = readNullableSync<String>(PrefConstants.REFRESH_TOKEN)

    override fun setRefreshToken(token: String) =
        writeSync(PrefConstants.REFRESH_TOKEN, token)

    override fun isJarShieldEnabled() =
        readNonNullableSync(PrefConstants.JAR_SHIELD_ENABLED, false)

    override fun setJarShieldStatus(isEnabled: Boolean) =
        writeSync(PrefConstants.JAR_SHIELD_ENABLED, isEnabled)

    override fun isRemoteConfigStale() =
        readNonNullableSync(PrefConstants.REMOTE_CONFIG_FORCE_REFRESH_KEY, false)

    override fun isSmsProcessEnabled() =
        readNonNullableSync(PrefConstants.IS_SMS_PROCESSING_ENABLED, false)

    override fun setSmsProcessStatus(status: Boolean) =
        writeSync(PrefConstants.IS_SMS_PROCESSING_ENABLED, status)

    override fun isRemoteConfigEventSent() =
        readNonNullableSync(PrefConstants.IS_REMOTE_CONFIG_EVENT_SENT, false)

    override fun setRemoteConfigEventSent(isSent: Boolean) =
        writeSync(PrefConstants.IS_REMOTE_CONFIG_EVENT_SENT, isSent)

    override fun setRemoteConfigStaleState(isStale: Boolean) =
        writeSync(PrefConstants.REMOTE_CONFIG_FORCE_REFRESH_KEY, isStale)

    override fun isDailySavingCrossPromotionCompleted() =
        readNonNullableSync(PrefConstants.IS_DAILY_SAVING_CROSS_PROMOTION_COMPLETED, false)

    override fun setDailySavingCrossPromotionShown() =
        writeSync(PrefConstants.IS_DAILY_SAVING_CROSS_PROMOTION_COMPLETED, true)

    override fun isAutoPayCrossPromotionCompleted() =
        readNonNullableSync(PrefConstants.IS_AUTOPAY_CROSS_PROMOTION_COMPLETED, false)

    override fun setAutoPayCrossPromotionShown() =
        writeSync(PrefConstants.IS_AUTOPAY_CROSS_PROMOTION_COMPLETED, true)

    //Return true only when called for first time
    override fun isFirstAutopayDisabled(): Boolean {
        val value = readNonNullableSync(PrefConstants.IS_FIRST_AUTOPAY_DISABLED, true)
        writeSync(PrefConstants.IS_FIRST_AUTOPAY_DISABLED, false)
        return value
    }

    override fun clearAll() {
        clearAllSync()
    }

    override fun getLastSurveyConsumed() =
        readNonNullableSync(PrefConstants.LAST_SURVEY_CONSUMED, "")

    override fun setLastSurveyConsumed(value: String) =
        writeSync(PrefConstants.LAST_SURVEY_CONSUMED, value)

    override fun setFirstInvestmentEventSent(value: Boolean) =
        writeSync(PrefConstants.FIRST_INVESTMENT_EVENT_SENT, value)

    override fun isFirstInvestmentEventSent() =
        readNonNullableSync(PrefConstants.FIRST_INVESTMENT_EVENT_SENT, false)

    override fun getAppVersion(): Int =
        readNonNullableSync(PrefConstants.APP_VERSION, PrefConstants.DEFAULT_APP_VERSION)

    override fun setAppVersion(appVersionCode: Int) =
        writeSync(PrefConstants.APP_VERSION, appVersionCode)

    override fun getIsAppUpdated(currentAppVersionCode: Int): Boolean {
        if (getAppVersion() == currentAppVersionCode) {
            setAppVersion(currentAppVersionCode)
            return false
        }
        return getAppVersion() != currentAppVersionCode
    }

    override fun getGoldPriceAlert(): Boolean =
        readNonNullableSync(PrefConstants.GOLD_PRICE_ALERT, false)

    override fun setGoldPriceAlert() = writeSync(PrefConstants.GOLD_PRICE_ALERT, true)

    override fun getHasShownGoldPriceAlertOnce(): Boolean =
        readNonNullableSync(PrefConstants.HAS_SHOWN_GOLD_PRICE_ALERT_ONCE, false)

    override fun setHasShownGoldPriceAlertOnce() =
        writeSync(PrefConstants.HAS_SHOWN_GOLD_PRICE_ALERT_ONCE, true)

    override fun incrementRatingDialogShownCount() {
        var current = getRatingDialogShownCount()
        current++
        readNonNullableSync(PrefConstants.SHOWN_RATING_DIALOG_COUNT, current)
    }

    override fun getRatingDialogShownCount() =
        readNonNullableSync(PrefConstants.SHOWN_RATING_DIALOG_COUNT, 0)

    override fun setWonMysteryCardCount(count: Int) {
        writeSync(PrefConstants.MYSTERY_CARD_COUNT, count)
    }

    override fun getWonMysteryCardCount() =
        readNonNullableSync(PrefConstants.MYSTERY_CARD_COUNT, 0)

    override fun setWonMysteryCardChallengeId(challengeId: String) {
        writeSync(PrefConstants.MYSTERY_CARD_CHALLENGE_ID, challengeId)
    }

    override fun getWonMysteryCardChallengeId() =
        readNonNullableSync(PrefConstants.MYSTERY_CARD_CHALLENGE_ID, "")

    override fun setFirstSuccessfulInvestment(value: Boolean) =
        writeSync(PrefConstants.FIRST_SUCCESSFUL_INVESTMENT, value)

    override fun isFirstSuccessfulInvestment() =
        readNonNullableSync(PrefConstants.FIRST_SUCCESSFUL_INVESTMENT, true)

    override fun hasShownAutoSaveVideoOnce() =
        readNonNullableSync(PrefConstants.HAS_SHOWN_AUTO_SAVE_VIDEO, false)

    override fun setShownAutoSaveVideoOnce() =
        writeSync(PrefConstants.HAS_SHOWN_AUTO_SAVE_VIDEO, true)

    override fun shouldShowTransactionOverLay() =
        readNonNullableSync(PrefConstants.SHOW_TRANSACTION_OVERLAY, true)

    override fun setShowTransactionOverLay(value: Boolean) =
        writeSync(PrefConstants.SHOW_TRANSACTION_OVERLAY, value)

    override fun shouldShowTransactionDetailOverLay() =
        readNonNullableSync(PrefConstants.SHOW_TRANSACTION_DETAIL_OVERLAY, true)

    override fun setShowTransactionDetailOverLay(value: Boolean) =
        writeSync(PrefConstants.SHOW_TRANSACTION_DETAIL_OVERLAY, value)

    override fun shouldSyncAppsFlyerAttributionData() =
        readNonNullableSync(PrefConstants.SHOULD_SYNC_APPSFLYER_ATTRIBUTION_DATA, true)

    override fun setSyncAppsFlyerAttributionData(value: Boolean) =
        writeSync(PrefConstants.SHOULD_SYNC_APPSFLYER_ATTRIBUTION_DATA, value)

    override fun shouldShowVasooliIntro() =
        readNonNullableSync(PrefConstants.SHOULD_SHOW_VASOOLI_INTRO, true)

    override fun setShouldShowVasooliIntro(value: Boolean) =
        writeSync(PrefConstants.SHOULD_SHOW_VASOOLI_INTRO, value)

    override fun isFirstSession() = readNonNullableSync(PrefConstants.IS_FIRST_SESSION, true)

    override fun setIsFirstSession(value: Boolean) =
        writeSync(PrefConstants.IS_FIRST_SESSION, value)

    override fun hasShownOnboardingCongratsScreen() =
        readNonNullableSync(PrefConstants.HAS_SHOWN_ONBOARDING_CONGRATS_SCREEN, false)

    override fun setShownOnboardingCongratsScreen(value: Boolean) =
        writeSync(PrefConstants.HAS_SHOWN_ONBOARDING_CONGRATS_SCREEN, value)

    override fun setUserPhoneNumber(number: String) =
        writeSync(PrefConstants.USER_PHONE_NUMBER, number)

    override fun getUserPhoneNumber() = readNullableSync<String>(PrefConstants.USER_PHONE_NUMBER)

    override fun setUserName(name: String) =
        writeSync(PrefConstants.USER_NAME, name)

    override fun getUserName() = readNullableSync<String>(PrefConstants.USER_NAME)

    override fun setPrimaryUpiId(primaryUpiId: String) =
        writeSync(PrefConstants.PRIMARY_UPI_ID, primaryUpiId)

    override fun getPrimaryUpiId() = readNullableSync<String>(PrefConstants.PRIMARY_UPI_ID)

    override fun setShowOnBoardingScreenForDuo(value: Boolean) =
        writeSync(PrefConstants.SHOULD_SHOW_DUO_ONBOARDING_SCREEN, value)

    override fun shouldShowDuoOnboardingScreen() =
        readNonNullableSync(PrefConstants.SHOULD_SHOW_DUO_ONBOARDING_SCREEN, false)

    override fun hasShownCustomOnboarding() =
        readNonNullableSync(PrefConstants.HAS_SHOWN_CUSTOMISED_ONBOARDING, false)

    override fun setHasShownCustomOnboarding(value: Boolean) =
        writeSync(PrefConstants.HAS_SHOWN_CUSTOMISED_ONBOARDING, value)

    override fun hasShownDigitalCoinIntro() =
        readNonNullableSync(PrefConstants.HAS_SHOWN_DIGITAL_COIN_INTRO, false)

    override fun setHasShownDigitalCoinIntro(value: Boolean) =
        writeSync(PrefConstants.HAS_SHOWN_DIGITAL_COIN_INTRO, value)

    override fun isDiwaliCampaignRunning() =
        readNonNullableSync(PrefConstants.IS_DIWALI_CAMPAIGN_RUNNING, false)

    override fun setIsDiwaliCampaignRunning(value: Boolean) =
        writeSync(PrefConstants.IS_DIWALI_CAMPAIGN_RUNNING, value)

    override fun setUserLifeCycleForMandate(value: String) =
        writeSync(PrefConstants.USER_LIFE_CYCLE_MANDATE, value)

    override fun getUserLifeCycleForMandate() =
        readNullableSync<String>(PrefConstants.USER_LIFE_CYCLE_MANDATE)

    override fun setPushNotificationContext(value: String) =
        writeSync(PrefConstants.PUSH_NOTIFICATION_CONTEXT, value)

    override fun getPushNotificationContext(): String? =
        readNullableSync(PrefConstants.PUSH_NOTIFICATION_CONTEXT)

    override fun isNewUserCheckEventFired() =
        readNonNullableSync(PrefConstants.IS_NEW_USER_CHECK_EVENT_FIRED, false)

    override fun setIsNewUserCheckEventFired(value: Boolean) =
        writeSync(PrefConstants.IS_NEW_USER_CHECK_EVENT_FIRED, value)

    override fun shouldShowHomeVerticalOverLay() =
        readNonNullableSync(PrefConstants.SHOW_HOME_VERTICAL_OVERLAY, true)

    override fun setShowHomeVerticalOverLay(value: Boolean) =
        writeSync(PrefConstants.SHOW_HOME_VERTICAL_OVERLAY, value)

    override fun isOnDemandSmsSent() =
        readNonNullableSync(PrefConstants.IS_ON_DEMAND_SMS_SENT, false)

    override fun setOnDemandSmsSent(value: Boolean) =
        writeSync(PrefConstants.IS_ON_DEMAND_SMS_SENT, value)

    override fun hasShownContactPermissionOnHomeScreen(): Boolean =
        readNonNullableSync(PrefConstants.HAS_SHOWN_CONTACT_PERMISSION_HOME_SCREEN, false)

    override fun setShownContactPermissionOnHomeScreen(value: Boolean) =
        writeSync(PrefConstants.HAS_SHOWN_CONTACT_PERMISSION_HOME_SCREEN, value)

    override fun setDuoStoryViewCount(count: Int) =
        writeSync(PrefConstants.DUO_STORY_VIEW_COUNT, count)

    override fun getDuoStoryViewCount() =
        readNonNullableSync(PrefConstants.DUO_STORY_VIEW_COUNT, 0)

    override fun isAutomaticallyDailySavingEducationScreenShown() =
        readNonNullableSync(PrefConstants.AUTOMATICALLY_DAILY_SAVING_EDUCATION_SCREEN_SHOWN, false)

    override fun setAutomaticallyDailySavingEducationScreenShown(value: Boolean) =
        writeSync(PrefConstants.AUTOMATICALLY_DAILY_SAVING_EDUCATION_SCREEN_SHOWN, value)

    override fun setIsShownHowToUseWinnings(value: Boolean) =
        writeSync(PrefConstants.HOW_TO_USE_WINNINGS, value)


    override fun getIsShownHowToUseWinnings() =
        readNonNullableSync(PrefConstants.HOW_TO_USE_WINNINGS, false)

    override fun setIsGoldLeaseLandingAnimationShow(value: Boolean) =
        writeSync(PrefConstants.IS_GOLD_LEASE_LANDING_ANIMATION_SHOW, value)

    override fun getIsGoldLeaseLandingAnimationShow() =
        readNonNullableSync(PrefConstants.IS_GOLD_LEASE_LANDING_ANIMATION_SHOW, false)

    override fun setIsSpinV2IntroShow(value: Boolean) =
        writeSync(PrefConstants.SPIN_V2_INTRO_SHOWN, true)

    override fun getIsSpinV2IntroShow() =
        readNonNullableSync(PrefConstants.SPIN_V2_INTRO_SHOWN, false)

    override fun setIsAlertShown(value: Boolean) =
        writeSync(PrefConstants.SPIN_ALERT_NUDGE, true)

    override fun getIsAlertNudgeShow() =
        readNonNullableSync(PrefConstants.SPIN_ALERT_NUDGE, false)

    override fun shouldAnimateTypeOneEpoxyModel() =
        readNonNullableSync(PrefConstants.SHOULD_ANIMATE_TYPE_ONE_EPOXY_MODEL, true)

    override fun shouldShowPostSetupScreenForNewSetup() =
        readNonNullableSync(PrefConstants.SHOULD_SHOW_POST_SETUP_SCREEN_FOR_NEW_SETUP, true)

    override fun setShouldShowPostSetupScreenForNewSetup(value: Boolean) {
        writeSync(PrefConstants.SHOULD_SHOW_POST_SETUP_SCREEN_FOR_NEW_SETUP, value)
    }

    override fun setShouldAnimateTypeOneEpoxyModel(value: Boolean) =
        writeSync(PrefConstants.SHOULD_ANIMATE_TYPE_ONE_EPOXY_MODEL, value)

    override fun shouldAnimateTypeTwoEpoxyModel() =
        readNonNullableSync(PrefConstants.SHOULD_ANIMATE_TYPE_TWO_EPOXY_MODEL, true)

    override fun setShouldAnimateTypeTwoEpoxyModel(value: Boolean) =
        writeSync(PrefConstants.SHOULD_ANIMATE_TYPE_TWO_EPOXY_MODEL, value)

    override fun hasClearedPreferencesForMigration() = readNonNullableSync(
        PrefConstants.HAS_CLEARED_PREFERENCES_FOR_MIGRATION,
        false
    )

    override fun setHasClearedPreferencesForMigration(cleared: Boolean) = writeSync(
        PrefConstants.HAS_CLEARED_PREFERENCES_FOR_MIGRATION,
        cleared
    )

    override fun setDailyInvestmentCancellationV2Date(date: String) = writeSync(
        PrefConstants.DAILY_INVESTMENT_CANCELLATION_V2_DATE,
        date
    )

    override fun getDailyInvestmentCancellationV2Date() =
        readNonNullableSync<String>(PrefConstants.DAILY_INVESTMENT_CANCELLATION_V2_DATE, "")

    override fun shouldShowCongratsCardInQuests() =
        readNonNullableSync(PrefConstants.SHOW_CONGRATS_CARD_IN_QUESTS, true)

    override fun setShouldShowCongratsCardInQuests(shouldShow: Boolean) = writeSync(
        PrefConstants.SHOW_CONGRATS_CARD_IN_QUESTS,
        shouldShow
    )

    override fun isAppWalkThroughShownToUser() = readNonNullableSync(
        PrefConstants.IS_APP_WALKTHROUGH_SHOWN_TO_USER,
        false
    )

    override fun setAppWalkThroughShownToUser(value: Boolean) = writeSync(
        PrefConstants.IS_APP_WALKTHROUGH_SHOWN_TO_USER,
        value
    )

    override fun isAppWalkThroughBeingShownToUser() = readNonNullableSync(
        PrefConstants.SHOULD_SHOW_APP_WALKTHROUGH_TO_USER,
        false
    )

    override fun setIsAppWalkThroughBeingShownToUser(value: Boolean) = writeSync(
        PrefConstants.SHOULD_SHOW_APP_WALKTHROUGH_TO_USER,
        value
    )

    override fun shouldShowSplashScreen() = readNonNullableSync(
        PrefConstants.SHOULD_SHOW_SPLASH_SCREEN,
        true
    )

    override fun setShouldShowSplashScreen(shouldShow: Boolean) = writeSync(
        PrefConstants.SHOULD_SHOW_SPLASH_SCREEN,
        shouldShow
    )



}