package com.jar.app.core_remote_config

interface RemoteConfigApi {

    fun getWhatsappNumber(): String

    fun shouldAuthenticateOnSell(): Boolean

    fun getMinimumGoldForDelivery(): Float

    fun shouldShowGoldDeliveryCardOnHome(): Boolean

    fun getMaxDeviceLimit(): Int

    fun shouldSkipLocalCache(): Boolean

    fun getMinimumSellAmount(): Int

    fun getTermsAndConditionsUrl(): String

    fun getPrivacyPolicyUrl(): String

    fun showHelpAndSupport(): Boolean

    fun getHelpAndSupportUrl(languageCode: String): String

    fun isHelpAndSupportMultiLang(): Boolean

    fun getInitiateTruecallerDialogTimeInSeconds(): Long

    fun isRatingDialogInApp(): String

    fun isShowingRatingDialog(): Boolean

    fun getPaymentGateway(): String

    fun getMinimumGoldBuyAmount(): Float

    fun getMaximumGoldBuyAmount(): Float

    fun getNoOfDaysForPastMessages(): Int

    fun getNoOfDaysForHistoricalMessages(): Int

    fun isGiftingEnabled(): Boolean

    fun getMinimumSupportedVersion(): Long

    fun shouldUseNewStoryView(): Boolean

    fun getUpiAppsLogoUrl(): String

    fun getBankAppsLogoUrl(): String

    fun getSupportedUpiApps(): String

    fun getReferralEarningDescription(): String

    fun isOnboardingReminderNotifiEnabled(): Boolean

    fun getDoubleSavingsTitle(): String

    fun getDoubleSavingsDescription(): String

    fun shouldUseFinart(): Boolean

    fun shouldShowUpiCollectFlow(): Boolean

    fun shouldInvokeAutopayAfterInitialDailySavingSetup(): Boolean

    fun getMinimumAmountToInitiatePennyDropInAutoInvest(): Int

    fun getWorkflowTypeForAutoInvest(): String

    fun shouldShowExplanatoryVideoForAutoInvest(): Boolean

    fun getBuyGoldVariant(): String

    fun getRoundOffAmount(): Int

    fun setupAutopayExperimentV2(): Boolean

    fun setupAutopayAmount(): Int

    fun getGoldTransactionScreenPrimaryCta(): String

    fun isVasooliReminderSelf(): Boolean

    fun isOnboardingAutoPayExperimentRunning(): Boolean

    fun getAutoPayProvider(): String

    fun getMandateSupportedUpiApps(): String

    fun shouldFilterMandateApps(): Boolean

    fun shouldShowGooglePayIfNoOtherAppsForMandate(): Boolean

    fun isGooglePaySupportingAllBanksForMandate(): Boolean

    fun isDailySavingSingleCardExperimentRunning(): Boolean

    fun getSetupAutopaySingleCardExperimentType(): String

    fun isRoundOffCardExperimentRunning(): Boolean

    fun shouldHideDigitalCoinSkipButton(): Boolean

    fun isOnboardingSavingPlanExperimentRunning(): Boolean

    fun shouldEnableSellGoldIdentificationVerification(): Boolean

    fun shouldUseRecommendedValueAsHint(): Boolean

    fun shouldShowQuickActions(): Boolean

    fun shouldShowPreNotificationCard(): Boolean

    fun getSipSubscriptionType(): String

    fun getQuickActionExperimentType(): String

    fun isLanguageExperimentRunning(): Boolean

    fun shouldShowDailySavingExperimentV2(): Boolean

    fun isSkipAgeAndGenderExperimentRunning(): Boolean

    fun shouldShowSpinsV2(): Boolean

    fun isShowNewDSBottomSheet(): Boolean

    fun takeReadContactPermission(): Boolean

    fun updateDsV2MonthCount(): Int

    fun isDetectPhoneNumberNewApi(): Boolean

    fun shouldHaveWABtnOnTopInRefer(): Boolean

    fun isMandateBottomSheetExperimentRunning(): Boolean

    fun shouldShowDailySavingsV2Flow(): Boolean

    fun dailySavingsOnboardingFlowVariant(): String

    fun getBuyGoldCtaDrawableLink(): String

    fun shouldByPassCustomOnboardingBasedOnUpiApps(): Boolean

    fun isSmsPermissionRequired(): Boolean

    fun shouldShowSkipButtonOnDSCustomOnboardingLottie(): Boolean

    fun shouldUseOTL(): Boolean

    fun isShowInAppStory(): Boolean

    fun getMediaUrlForSoundInAppStory(): String

    fun getDirectRedirectionDeeplink(): String

    fun isOneStepDSExperimentRunning(): Boolean

    fun getGoalSelectionFragmentVariant(): Int

    fun shouldEnablePinlessDigilocker(): Boolean

    fun getDiwaliSplashScreenAsset(): String

    fun getDiwaliHomeScreenLampAsset(): String

    fun getFestivalHomeScreenAsset(): String

    fun getFestivalBuyGoldScreenAsset(): String

    fun isFestivalCampaignEnabled(): Boolean

    fun getFestivalDsScreenAsset(): String
}