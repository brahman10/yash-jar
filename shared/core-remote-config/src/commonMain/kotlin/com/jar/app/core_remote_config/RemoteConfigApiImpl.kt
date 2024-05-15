package com.jar.app.core_remote_config

import com.jar.internal.library.jar_core_remote_config.api.ConfigApi
import kotlin.reflect.KClass

internal class RemoteConfigApiImpl constructor(
    private val configApi: ConfigApi
) : RemoteConfigApi {

    private fun <T : Any> read(param: RemoteConfigParam, returnType: KClass<T>): T {
        val value: Any = when (returnType) {
            String::class -> configApi.getString(param.key)
            Boolean::class -> configApi.getBoolean(param.key)
            Long::class -> configApi.getLong(param.key)
            Int::class -> configApi.getLong(param.key).toInt()
            Double::class -> configApi.getDouble(param.key)
            Float::class -> configApi.getDouble(param.key).toFloat()
            else -> {
                throw IllegalArgumentException("Unsupported cast")
            }
        }
        @Suppress("UNCHECKED_CAST")
        return (value as T)
    }

    private inline fun <reified T : Any> read(param: RemoteConfigParam): T = read(param, T::class)

    /** Override Methods **/

    override fun getWhatsappNumber() = read<String>(RemoteConfigParam.WHATSAPP_NUMBER)

    override fun shouldAuthenticateOnSell() =
        read<Boolean>(RemoteConfigParam.SHOULD_AUTHENTICATE_ON_SELL)

    override fun getMinimumGoldForDelivery() = read<Float>(RemoteConfigParam.MIN_GOLD_FOR_DELIVERY)

    override fun shouldShowGoldDeliveryCardOnHome() =
        read<Boolean>(RemoteConfigParam.SHOW_GOLD_DELIVERY_CARD)

    override fun getMaxDeviceLimit() = read<Int>(RemoteConfigParam.MAX_DEVICE_LIMIT)

    override fun shouldSkipLocalCache() = read<Boolean>(RemoteConfigParam.SHOULD_SKIP_LOCAL_CACHE)

    override fun getMinimumSellAmount() = read<Int>(RemoteConfigParam.MIN_SELL_AMOUNT)

    override fun getTermsAndConditionsUrl() =
        read<String>(RemoteConfigParam.TERMS_AND_CONDITIONS_URL)

    override fun getPrivacyPolicyUrl() = read<String>(RemoteConfigParam.PRIVACY_POLICY_POLICY)

    override fun showHelpAndSupport() = read<Boolean>(RemoteConfigParam.SHOW_HELP_SUPPORT)

    override fun getHelpAndSupportUrl(languageCode: String): String {
        val baseUrl = read<String>(RemoteConfigParam.HELP_AND_SUPPORT_URL)
        return if (isHelpAndSupportMultiLang()) "$baseUrl/$languageCode/" else baseUrl
    }

    override fun isHelpAndSupportMultiLang() =
        read<Boolean>(RemoteConfigParam.HELP_SUPPORT_MULTI_LANG)

    override fun getInitiateTruecallerDialogTimeInSeconds() =
        read<Long>(RemoteConfigParam.INITIATE_TRUECALLER_DIALOG_TIME)

    override fun isRatingDialogInApp() = read<String>(RemoteConfigParam.RATING_DIALOG)

    override fun isShowingRatingDialog() = read<Boolean>(RemoteConfigParam.SHOW_RATING_DIALOG)

    override fun getPaymentGateway() = read<String>(RemoteConfigParam.PAYMENT_GATEWAY)

    override fun getMinimumGoldBuyAmount() = read<Float>(RemoteConfigParam.MINIMUM_GOLD_BUY_AMOUNT)

    override fun getMaximumGoldBuyAmount() = read<Float>(RemoteConfigParam.MAXIMUM_GOLD_BUY_AMOUNT)

    override fun getNoOfDaysForPastMessages() =
        read<Int>(RemoteConfigParam.NO_OF_DAYS_FOR_PAST_MESSAGES)

    override fun getNoOfDaysForHistoricalMessages() =
        read<Int>(RemoteConfigParam.NO_OF_DAYS_FOR_HISTORICAL_MESSAGES)

    override fun isGiftingEnabled() = read<Boolean>(RemoteConfigParam.FEATURE_GIFTING)

    override fun getMinimumSupportedVersion() =
        read<Long>(RemoteConfigParam.MINIMUM_SUPPORTED_VERSION)

    override fun shouldUseNewStoryView() = read<Boolean>(RemoteConfigParam.USE_NEW_STORY_VIEW)

    override fun getUpiAppsLogoUrl() = read<String>(RemoteConfigParam.UPI_APPS_LOGO_URL)

    override fun getBankAppsLogoUrl() = read<String>(RemoteConfigParam.BANK_APPS_LOGO_URL)

    //Returns comma separated string of supported UPI apps
    override fun getSupportedUpiApps() = read<String>(RemoteConfigParam.SUPPORTED_UPI_APPS)

    override fun getReferralEarningDescription() =
        read<String>(RemoteConfigParam.REFERRAL_EARNING_DESCRIPTION)

    override fun isOnboardingReminderNotifiEnabled() =
        read<Boolean>(RemoteConfigParam.ONBOARDING_REMINDER_NOTIFICATION)

    override fun getDoubleSavingsTitle() = read<String>(RemoteConfigParam.DOUBLE_SAVINGS_TITLE)

    override fun getDoubleSavingsDescription() =
        read<String>(RemoteConfigParam.DOUBLE_SAVINGS_DESCRIPTION)

    override fun shouldUseFinart() = read<Boolean>(RemoteConfigParam.SHOULD_USE_FINART)

    override fun shouldShowUpiCollectFlow() =
        read<Boolean>(RemoteConfigParam.SHOULD_SHOW_UPI_COLLECT_FLOW)

    override fun shouldInvokeAutopayAfterInitialDailySavingSetup() =
        read<Boolean>(RemoteConfigParam.SHOULD_INVOKE_AUTOPAY_AFTER_INITIAL_DAILY_SAVING_SETUP)

    override fun getMinimumAmountToInitiatePennyDropInAutoInvest() =
        read<Int>(RemoteConfigParam.MIN_AMOUNT_TO_INITIATE_PENNY_DROP_IN_AUTOPAY)

    override fun getWorkflowTypeForAutoInvest() =
        read<String>(RemoteConfigParam.WORKFLOW_TYPE_FOR_AUTOINVEST)

    override fun shouldShowExplanatoryVideoForAutoInvest() =
        read<Boolean>(RemoteConfigParam.SHOULD_SHOW_VIDEO_FOR_AUTOINVEST)

    override fun getBuyGoldVariant() =
        read<String>(RemoteConfigParam.BUY_GOLD_VARIANT)

    override fun getRoundOffAmount() = read<Int>(RemoteConfigParam.ROUND_OFF_AMOUNT)

    override fun setupAutopayExperimentV2() =
        read<Boolean>(RemoteConfigParam.SETUP_AUTOPAY_EXPERIMENT_V2)

    override fun setupAutopayAmount() =
        read<Int>(RemoteConfigParam.SETUP_AUTOPAY_AMOUNT)

    override fun getGoldTransactionScreenPrimaryCta() =
        read<String>(RemoteConfigParam.GOLD_TRANSACTION_SCREEN_PRIMARY_CTA)

    override fun isVasooliReminderSelf() =
        read<Boolean>(RemoteConfigParam.IS_VASOOLI_REMINDER_SELF)

    override fun isOnboardingAutoPayExperimentRunning() =
        read<Boolean>(RemoteConfigParam.IS_ONBOARDING_DAILY_SAVING_AUTOPAY_EXPERIMENT_RUNNING)

    override fun getAutoPayProvider() = read<String>(RemoteConfigParam.AUTOPAY_PROVIDER)

    override fun getMandateSupportedUpiApps() =
        read<String>(RemoteConfigParam.MANDATE_SUPPORTED_UPI_APPS)

    override fun shouldFilterMandateApps() =
        read<Boolean>(RemoteConfigParam.SHOULD_FILTER_MANDATE_APPS)

    override fun shouldShowGooglePayIfNoOtherAppsForMandate() =
        read<Boolean>(RemoteConfigParam.SHOULD_SHOW_GOOGLE_PAY_FOR_MANDATE_IF_NO_OTHER_APP_PRESENT)

    override fun isGooglePaySupportingAllBanksForMandate() =
        read<Boolean>(RemoteConfigParam.IS_GOOGLE_PAY_SUPPORTING_ALL_BANKS_FOR_MANDATE)

    override fun isDailySavingSingleCardExperimentRunning() =
        read<Boolean>(RemoteConfigParam.IS_DAILY_SAVING_SINGLE_CARD_EXPERIMENT_RUNNING)

    override fun getSetupAutopaySingleCardExperimentType() =
        read<String>(RemoteConfigParam.GET_SETUP_AUTOPAY_SINGLE_CARD_EXPERIMENT_RUNNING_STATE)

    override fun isRoundOffCardExperimentRunning() =
        read<Boolean>(RemoteConfigParam.IS_ROUND_OFF_CARD_EXPERIMENT_RUNNING)

    override fun shouldHideDigitalCoinSkipButton() =
        read<Boolean>(RemoteConfigParam.SHOULD_HIDE_DIGITAL_COIN_SKIP_BUTTON)

    override fun isOnboardingSavingPlanExperimentRunning() =
        read<Boolean>(RemoteConfigParam.IS_ONBOARDING_SAVING_PLAN_EXPERIMENT_RUNNING)

    override fun shouldEnableSellGoldIdentificationVerification() =
        read<Boolean>(RemoteConfigParam.SHOULD_ENABLE_SELL_GOLD_IDENTIFICATION_VERIFICATION)

    override fun shouldUseRecommendedValueAsHint() =
        read<Boolean>(RemoteConfigParam.SHOULD_USE_RECOMMENDED_VALUE_AS_HINT)

    override fun shouldShowQuickActions() =
        read<Boolean>(RemoteConfigParam.SHOULD_SHOW_QUICK_ACTIONS)

    override fun shouldShowPreNotificationCard() =
        read<Boolean>(RemoteConfigParam.SHOULD_SHOW_PRE_NOTIFICATION_CARD)

    override fun getSipSubscriptionType() =
        read<String>(RemoteConfigParam.SIP_SUBSCRIPTION_TYPE)

    override fun getQuickActionExperimentType() =
        read<String>(RemoteConfigParam.QUICK_ACTION_EXPERIMENT_TYPE)

    override fun isSkipAgeAndGenderExperimentRunning() =
        read<Boolean>(RemoteConfigParam.IS_ONBOARDING_SKIP_AGE_AND_GENDER_EXPERIMENT_RUNNING)

    override fun shouldShowSpinsV2(): Boolean {
        return read(RemoteConfigParam.SHOULD_SHOW_SPINS_V2)
    }

    override fun shouldShowDailySavingExperimentV2() =
        read<Boolean>(RemoteConfigParam.SHOULD_SHOW_DAILY_SAVINGS_EXPERIMENT_V2)

    override fun isLanguageExperimentRunning() =
        read<Boolean>(RemoteConfigParam.IS_LANGUAGE_EXPERIMENT_RUNNING)

    override fun isShowNewDSBottomSheet() =
        read<Boolean>(RemoteConfigParam.SHOW_NEW_DAILY_SAVING_BOTTOMSHEET)

    override fun takeReadContactPermission() =
        read<Boolean>(RemoteConfigParam.SHOULD_ASK_READ_CONTACT_PERMISSION)

    override fun updateDsV2MonthCount() =
        read<Int>(RemoteConfigParam.UPDATE_DS_V2_MONTH_COUNT)

    override fun isDetectPhoneNumberNewApi() =
        read<Boolean>(RemoteConfigParam.IS_DETECT_PHONE_NUMBER_NEW_API)

    override fun shouldHaveWABtnOnTopInRefer() =
        read<Boolean>(RemoteConfigParam.SHOULD_HAVE_WA_BTN_ON_TOP_IN_REFER)

    override fun isMandateBottomSheetExperimentRunning() =
        read<Boolean>(RemoteConfigParam.IS_MANDATE_BOTTOM_SHEET_EXPERIMENT_RUNNING)

    override fun shouldShowDailySavingsV2Flow() =
        read<Boolean>(RemoteConfigParam.SHOULD_SHOW_DAILY_SAVINGS_V2_FLOW)

    override fun dailySavingsOnboardingFlowVariant() =
        read<String>(RemoteConfigParam.DAILY_SAVINGS_ONBOARDING_EXPERIMENT)

    override fun getBuyGoldCtaDrawableLink() =
        read<String>(RemoteConfigParam.BUY_GOLD_CTA_DRAWABLE_LINK)

    override fun shouldByPassCustomOnboardingBasedOnUpiApps() =
        read<Boolean>(RemoteConfigParam.SHOULD_BY_PASS_CUSTOM_ONBOARDING_BASED_ON_UPI_APPS)

    override fun isSmsPermissionRequired() =
        read<Boolean>(RemoteConfigParam.IS_SMS_PERMISSION_REQUIRED)

    override fun shouldShowSkipButtonOnDSCustomOnboardingLottie() =
        read<Boolean>(RemoteConfigParam.SHOULD_SHOW_SKIP_BUTTON_ON_DS_CUSTOM_ONBOARDING_LOTTIE)

    override fun shouldUseOTL() =
        read<Boolean>(RemoteConfigParam.SHOULD_USE_OTL)

    override fun isShowInAppStory() =
        read<Boolean>(RemoteConfigParam.IS_SHOW_IN_APP_STORY)

    override fun getMediaUrlForSoundInAppStory() =
        read<String>(RemoteConfigParam.IN_APP_STORY_MEDIA_URL)

    override fun getDirectRedirectionDeeplink() =
        read<String>(RemoteConfigParam.DIRECT_REDIRECTION_DEEPLINK)

    override fun isOneStepDSExperimentRunning() =
        read<Boolean>(RemoteConfigParam.ONE_STEP_DS_EXPERIMENT)

    override fun getGoalSelectionFragmentVariant() =
        read<Int>(RemoteConfigParam.GOAL_SELECTION_FRAGMENT_VARIANT)

    override fun shouldEnablePinlessDigilocker() =
        read<Boolean>(RemoteConfigParam.SHOULD_ENABLE_PINLESS_DIGILOCKER)

    override fun getDiwaliSplashScreenAsset() =
        read<String>(RemoteConfigParam.SPLASH_SCREEN_DIWALI_ASSET)

    override fun getDiwaliHomeScreenLampAsset() =
        read<String>(RemoteConfigParam.DIWALI_ASSET_HOME_SCREEN_LAMP)

    override fun getFestivalHomeScreenAsset() =
        read<String>(RemoteConfigParam.DIWALI_ASSET_HOME_SCREEN_FLOWERS)

    override fun getFestivalBuyGoldScreenAsset() =
        read<String>(RemoteConfigParam.DIWALI_ASSET_BUY_GOLD_FLOWERS)

    override fun isFestivalCampaignEnabled() =
        read<Boolean>(RemoteConfigParam.IS_DIWALI_CAMPAIGN_ENABLED)

    override fun getFestivalDsScreenAsset() =
        read<String>(RemoteConfigParam.DIWALI_ASSET_DAILY_SAVINGS_BANNER)
}