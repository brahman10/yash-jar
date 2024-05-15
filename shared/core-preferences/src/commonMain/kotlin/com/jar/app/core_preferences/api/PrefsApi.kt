package com.jar.app.core_preferences.api

interface PrefsApi {

    suspend fun setStringData(key: String, value: String)

    suspend fun getStringData(key: String): String?

    fun getInviteLinkExpiry(): Long

    fun setInviteLinkExpiry(inviteLinkExpiry: Long)

    fun getUserInviteLink(): String?

    fun setUserInviteLink(appsFlyerReferralLink: String)

    fun getAppsFlyerReferralUserId(): String

    fun setAppsFlyerReferralUserId(appsFlyerReferralUserId: String)

    fun getCurrentLanguageCode(): String

    fun getCurrentLanguageName(): String

    fun getFCMToken(): String?

    fun setFCMToken(fcmToken: String)

    fun setSelectedLanguageCode(code: String)

    fun setSelectedLanguageName(code: String)

    fun isOnboardingComplete(): Boolean

    fun setOnboardingComplete()

    fun getAuthType(): String

    fun setAuthType(type: String)

    suspend fun getUserString(): String?

    fun getUserStringSync(): String?

    suspend fun setUserString(user: String)

    fun setUserStringSync(user: String)

    fun getAccessToken(): String?

    fun isLoggedIn(): Boolean

    fun isOnBoardingStoryShown(): Boolean

    fun setOnBoardingStoryShown()

    fun setNewOnboardingState(state: String)

    fun getNewOnboardingState(): String?

    fun setOldOnboardingState(state: String)

    fun getOldOnboardingState(): String?

    fun setAccessToken(token: String)

    fun getRefreshToken(): String?

    fun setRefreshToken(token: String)

    fun isJarShieldEnabled(): Boolean

    fun setJarShieldStatus(isEnabled: Boolean)

    fun isRemoteConfigStale(): Boolean

    fun isSmsProcessEnabled(): Boolean

    fun setSmsProcessStatus(status: Boolean)

    fun isRemoteConfigEventSent(): Boolean

    fun setRemoteConfigEventSent(isSent: Boolean = true)

    fun setRemoteConfigStaleState(isStale: Boolean)

    fun isDailySavingCrossPromotionCompleted(): Boolean

    fun setDailySavingCrossPromotionShown()

    fun isAutoPayCrossPromotionCompleted(): Boolean

    fun setAutoPayCrossPromotionShown()

    fun isFirstAutopayDisabled(): Boolean

    fun clearAll()

    fun getLastSurveyConsumed(): String?

    fun setLastSurveyConsumed(value: String)

    fun setFirstInvestmentEventSent(value: Boolean)

    fun isFirstInvestmentEventSent(): Boolean

    fun getAppVersion(): Int

    fun setAppVersion(appVersionCode: Int)

    fun getIsAppUpdated(currentAppVersionCode: Int): Boolean

    fun getGoldPriceAlert(): Boolean

    fun setGoldPriceAlert()

    fun getHasShownGoldPriceAlertOnce(): Boolean

    fun setHasShownGoldPriceAlertOnce()

    fun incrementRatingDialogShownCount()

    fun getRatingDialogShownCount(): Int

    fun setWonMysteryCardCount(count: Int)

    fun getWonMysteryCardCount(): Int

    fun setWonMysteryCardChallengeId(challengeId: String)

    fun getWonMysteryCardChallengeId(): String

    fun setFirstSuccessfulInvestment(value: Boolean)

    fun isFirstSuccessfulInvestment(): Boolean

    fun hasShownAutoSaveVideoOnce(): Boolean

    fun setShownAutoSaveVideoOnce()

    fun shouldShowTransactionOverLay(): Boolean

    fun setShowTransactionOverLay(value: Boolean)

    fun shouldShowTransactionDetailOverLay(): Boolean

    fun setShowTransactionDetailOverLay(value: Boolean)

    fun shouldSyncAppsFlyerAttributionData(): Boolean

    fun setSyncAppsFlyerAttributionData(value: Boolean)

    fun shouldShowVasooliIntro(): Boolean

    fun setShouldShowVasooliIntro(value: Boolean)

    fun isFirstSession(): Boolean

    fun setIsFirstSession(value: Boolean)

    fun hasShownOnboardingCongratsScreen(): Boolean

    fun setShownOnboardingCongratsScreen(value: Boolean)

    fun setUserPhoneNumber(number: String)

    fun getUserPhoneNumber(): String?

    fun setUserName(name: String)

    fun getUserName(): String?

    fun setPrimaryUpiId(primaryUpiId: String)

    fun getPrimaryUpiId(): String?

    fun setShowOnBoardingScreenForDuo(value: Boolean)

    fun shouldShowDuoOnboardingScreen(): Boolean

    fun hasShownCustomOnboarding(): Boolean

    fun setHasShownCustomOnboarding(value: Boolean)

    fun hasShownDigitalCoinIntro(): Boolean

    fun setHasShownDigitalCoinIntro(value: Boolean)

    fun isDiwaliCampaignRunning(): Boolean

    fun setIsDiwaliCampaignRunning(value: Boolean)

    fun setUserLifeCycleForMandate(value: String)

    fun getUserLifeCycleForMandate(): String?

    fun setPushNotificationContext(value: String)

    fun getPushNotificationContext(): String?

    fun isNewUserCheckEventFired(): Boolean

    fun setIsNewUserCheckEventFired(value: Boolean)

    fun shouldShowHomeVerticalOverLay(): Boolean

    fun setShowHomeVerticalOverLay(value: Boolean)

    fun isOnDemandSmsSent(): Boolean

    fun setOnDemandSmsSent(value: Boolean)

    fun setShownContactPermissionOnHomeScreen(value: Boolean)

    fun hasShownContactPermissionOnHomeScreen(): Boolean

    fun setDuoStoryViewCount(count: Int)

    fun getDuoStoryViewCount(): Int

    fun isAutomaticallyDailySavingEducationScreenShown(): Boolean

    fun setAutomaticallyDailySavingEducationScreenShown(value: Boolean)

    fun getIsShownHowToUseWinnings(): Boolean

    fun setIsShownHowToUseWinnings(value: Boolean)

    fun getIsGoldLeaseLandingAnimationShow(): Boolean

    fun setIsGoldLeaseLandingAnimationShow(value: Boolean)

    fun getIsSpinV2IntroShow(): Boolean

    fun setIsSpinV2IntroShow(value: Boolean)

    fun setIsAlertShown(value: Boolean)

    fun getIsAlertNudgeShow(): Boolean

    fun shouldAnimateTypeOneEpoxyModel(): Boolean

    fun shouldShowPostSetupScreenForNewSetup(): Boolean

    fun setShouldShowPostSetupScreenForNewSetup(value: Boolean)

    fun setShouldAnimateTypeOneEpoxyModel(value: Boolean)

    fun shouldAnimateTypeTwoEpoxyModel(): Boolean

    fun setShouldAnimateTypeTwoEpoxyModel(value: Boolean)

    fun hasClearedPreferencesForMigration(): Boolean

    fun setHasClearedPreferencesForMigration(cleared: Boolean)

    fun setDailyInvestmentCancellationV2Date(date: String)

    fun getDailyInvestmentCancellationV2Date(): String

    fun shouldShowCongratsCardInQuests(): Boolean

    fun setShouldShowCongratsCardInQuests(shouldShow: Boolean)

    fun isAppWalkThroughShownToUser(): Boolean

    fun setAppWalkThroughShownToUser(value: Boolean)

    fun isAppWalkThroughBeingShownToUser(): Boolean

    fun setIsAppWalkThroughBeingShownToUser(value: Boolean)

    fun shouldShowSplashScreen(): Boolean

    fun setShouldShowSplashScreen(shouldShow: Boolean)
}