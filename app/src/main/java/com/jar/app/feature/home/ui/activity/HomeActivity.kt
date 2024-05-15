package com.jar.app.feature.home.ui.activity

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.applinks.AppLinkData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jar.android.feature_post_setup.api.PostSetupApi
import com.jar.app.*
import com.jar.app.alias.JarDiwaliLauncherAlias
import com.jar.app.alias.JarLauncherAlias
import com.jar.app.base.data.event.*
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.*
import com.jar.app.base.ui.activity.BaseActivity
import com.jar.app.base.util.*
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_base.domain.model.card_library.StaticInfoType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.ExternalDeepLinks
import com.jar.app.core_base.util.DeepLinkHandler
import com.jar.app.core_base.util.OnDeepLinkNavigation
import com.jar.app.core_base.util.orZero
import com.jar.app.core_network.event.LogoutEvent
import com.jar.app.core_network.event.UnusualActivityDetectedEvent
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.explanatory_video.model.ExplanatoryVideoData
import com.jar.app.core_ui.extension.*
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.AppsFlyerInviteUtil
import com.jar.app.core_utils.data.BiometricUtil
import com.jar.app.core_utils.data.FileUtils
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.core_utils.data.WhatsAppUtil
import com.jar.app.databinding.ActivityHomeBinding
import com.jar.app.event.*
import com.jar.app.feature.home.domain.model.AdSourceData
import com.jar.app.feature.home.ui.fragment.SideNavDrawerViewModelAndroid
import com.jar.app.feature.home.util.HomeConstants
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature.onboarding.util.OnboardingNavigationUtil
import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2Api
import com.jar.app.feature_buy_gold_v2.shared.domain.event.InitiateBuyGoldEvent
import com.jar.app.feature.notification_list.NotificationEvents
import com.jar.app.feature_calculator.api.CalculatorApi
import com.jar.app.feature_calculator.shared.domain.model.CalculatorType
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.api.domain.event.SetupAutoPayEvent
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_gifting.api.GiftingApi
import com.jar.app.feature_goal_based_saving.api.GoalBasedSavingApi
import com.jar.app.feature_gold_lease.api.GoldLeaseApi
import com.jar.app.feature_gold_sip.api.GoldSipApi
import com.jar.app.feature_gold_sip.impl.ui.gold_sip_type_selection.SipTypeSelectionScreenData
import com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType
import com.jar.app.feature_homepage.api.data.HomePageApi
import com.jar.app.feature_homepage.api.event.HandleKnowMoreDeepLinkEvent
import com.jar.app.feature_homepage.api.event.OpenHomeScreenWeeklyMagicNotchFlow
import com.jar.app.feature_homepage.impl.domain.event.DrawBottomNavForAppWalkthrough
import com.jar.app.feature_homepage.impl.domain.event.GetBottomNavViewForWalkthroughEvent
import com.jar.app.feature_homepage.impl.util.ScrollStateProvider
import com.jar.app.feature_in_app_stories.api.InAppStoriesApi
import com.jar.app.feature_in_app_stories.impl.domain.event.HandleExternalLinkEvent
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.Clicked_StoryIcon
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.NoNetwork
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.SeenPage
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.Shown_StoryIcon
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.StoryId
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.StoryName
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.TotalPage
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.UnseenPage
import com.jar.app.feature_jar_duo.api.DuoApi
import com.jar.app.feature_jar_duo.shared.util.DuoConstants
import com.jar.app.feature_kyc.api.KycApi
import com.jar.app.feature_lending.api.LendingApi
import com.jar.app.feature_lending.impl.domain.event.LendingNavigateToSellGoldEvent
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationStatusV2
import com.jar.app.feature_lending_kyc.api.LendingKycApi
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandateErrorCodes
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_one_time_payments.shared.data.model.ManualPaymentCompletedEvent
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentEvent
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_promo_code.api.PromoCodeApi
import com.jar.app.feature_round_off.api.RoundOffApi
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_sell_gold.api.SellGoldApi
import com.jar.app.feature_sell_gold.impl.ui.model.VpaSelectionArgument
import com.jar.app.feature_sms_sync.api.SmsSyncApi
import com.jar.app.feature_spends_tracker.api.SpendsTrackerApi
import com.jar.app.feature_spin.api.SpinApi
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.app.feature_transaction.shared.domain.model.TransactionType
import com.jar.app.feature_transaction.shared.util.TransactionConstants
import com.jar.app.feature_vasooli.api.VasooliApi
import com.jar.app.feature_weekly_magic.api.WeeklyChallengeApi
import com.jar.app.weekly_magic_common.api.WeeklyChallengeCommonApi
import com.jar.app.weekly_magic_common.impl.events.RedirectToWeeklyChallengeEvent
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendHomeScreenTabInfographicType
import com.jar.feature_quests.api.QuestsApi
import com.jar.gold_price_alerts.api.GoldPriceAlertsApi
import com.jar.gold_redemption.api.GoldRedemptionApi
import com.jar.health_insurance.api.HealthInsuranceApi
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jar_core_network.api.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.myjar.app.feature_exit_survey.api.ExitSurveyApi
import com.myjar.app.feature_graph_manual_buy.api.GraphManualBuyApi
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
internal class HomeActivity :
    BaseActivity<ActivityHomeBinding>(),
    OnDeepLinkNavigation {

    companion object {
        private const val APP_UPDATE: Int = 1021
        private const val NONE = "NONE"
        const val ALLOWED = "allowed"
        const val DENIED = "denied"
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }

    override val customBindingInflater: (LayoutInflater) -> ActivityHomeBinding
        get() = ActivityHomeBinding::inflate


    @Inject
    lateinit var lendingApi: LendingApi

    @Inject
    lateinit var calculatorApi: CalculatorApi

    @Inject
    lateinit var exitSurveyApi: ExitSurveyApi

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var deviceUtils: WhatsAppUtil

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var dailyInvestmentApiRef: Lazy<DailyInvestmentApi>

    @Inject
    lateinit var goldRedemptionApiRef: Lazy<GoldRedemptionApi>

    @Inject
    lateinit var roundOffApiRef: Lazy<RoundOffApi>

    @Inject
    lateinit var buyGoldApiRef: Lazy<BuyGoldV2Api>

    @Inject
    lateinit var weeklyChallengeApiRef: Lazy<WeeklyChallengeApi>

    @Inject
    lateinit var weeklyChallengeCommonApiRef: Lazy<WeeklyChallengeCommonApi>

    @Inject
    lateinit var spinGameApiRef: Lazy<SpinApi>

    @Inject
    lateinit var questsApiRef: Lazy<QuestsApi>

    @Inject
    lateinit var goalBasedApiRef: Lazy<GoalBasedSavingApi>

    @Inject
    lateinit var coreUiApiRef: Lazy<CoreUiApi>

    @Inject
    lateinit var kycApiRef: Lazy<KycApi>

    @Inject
    lateinit var vasooliApiRef: Lazy<VasooliApi>

    @Inject
    lateinit var giftingApiRef: Lazy<GiftingApi>

    @Inject
    lateinit var lendingKycApiRef: Lazy<LendingKycApi>

    @Inject
    lateinit var goldPriceAlertsRef: Lazy<GoldPriceAlertsApi>

    @Inject
    lateinit var duoApiRef: Lazy<DuoApi>

    @Inject
    lateinit var mandatePaymentApiRef: Lazy<MandatePaymentApi>

    @Inject
    lateinit var goldSipApiRef: Lazy<GoldSipApi>

    @Inject
    lateinit var storiesApiRef: Lazy<InAppStoriesApi>

    @Inject
    lateinit var homePageApiRef: Lazy<HomePageApi>

    @Inject
    lateinit var sellGoldApiRef: Lazy<SellGoldApi>

    @Inject
    lateinit var biometricUtil: BiometricUtil

    @Inject
    lateinit var cacheEvictionUtil: CacheEvictionUtil

    @Inject
    lateinit var firebaseCrashlytics: FirebaseCrashlytics

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var appsFlyerLib: AppsFlyerLib

    @Inject
    lateinit var appsFlyerInviteUtil: AppsFlyerInviteUtil

    @Inject
    lateinit var smsSyncApi: SmsSyncApi

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var postSetupApiRef: Lazy<PostSetupApi>

    @Inject
    lateinit var goldLeaseApiRef: Lazy<GoldLeaseApi>

    @Inject
    lateinit var spendsTrackerApiRef: Lazy<SpendsTrackerApi>

    @Inject
    lateinit var healthInsuranceApiRef: Lazy<HealthInsuranceApi>

    @Inject
    lateinit var promoCodeApiRef: Lazy<PromoCodeApi>

    @Inject
    lateinit var manualBuyGraphApiRef: Lazy<GraphManualBuyApi>

    @Inject
    lateinit var networkFlow: NetworkFlow

    @Inject
    lateinit var scrollStateProvider: ScrollStateProvider

    @Inject
    lateinit var fileUtils: FileUtils

    private var deepLinkHandler = DeepLinkHandler(this)

    private var hasContactSynced: Boolean = false

    private var isShowRippleEffect = false

    private val goldLeaseApi by lazy {
        goldLeaseApiRef.get()
    }

    private val buyGoldApi by lazy {
        buyGoldApiRef.get()
    }

    private val weeklyChallengeApi by lazy {
        weeklyChallengeApiRef.get()
    }

    private val weeklyChallengeCommonApi by lazy {
        weeklyChallengeCommonApiRef.get()
    }

    private val goldPriceAlertsApi by lazy {
        goldPriceAlertsRef.get()
    }

    private val spinGameApi by lazy {
        spinGameApiRef.get()
    }

    private val questsApi by lazy {
        questsApiRef.get()
    }

    private val goalBasedSavingApi by lazy {
        goalBasedApiRef.get()
    }

    private val dailyInvestmentApi by lazy {
        dailyInvestmentApiRef.get()
    }

    private val goldRedemptionApi by lazy {
        goldRedemptionApiRef.get()
    }

    private val roundOffApi by lazy {
        roundOffApiRef.get()
    }

    private val coreUiApi by lazy {
        coreUiApiRef.get()
    }

    private val kycApi by lazy {
        kycApiRef.get()
    }

    private val vasooliApi by lazy {
        vasooliApiRef.get()
    }

    private val lendingKycApi by lazy {
        lendingKycApiRef.get()
    }

    private val giftingApi by lazy {
        giftingApiRef.get()
    }

    private val duoApi by lazy {
        duoApiRef.get()!!
    }

    private val mandatePaymentApi by lazy {
        mandatePaymentApiRef.get()
    }

    private val goldSipApi by lazy {
        goldSipApiRef.get()
    }

    private val storiesApi by lazy {
        storiesApiRef.get()
    }
    private val homePageApi by lazy {
        homePageApiRef.get()
    }

    private val sellGoldApi by lazy {
        sellGoldApiRef.get()
    }

    private val postSetupApi by lazy {
        postSetupApiRef.get()
    }

    private val spendTrackerApi by lazy {
        spendsTrackerApiRef.get()
    }

    private val healthInsuranceApi by lazy {
        healthInsuranceApiRef.get()
    }

    private val promoCodeApi by lazy {
        promoCodeApiRef.get()
    }

    private val manualBuyGraphApi by lazy {
        manualBuyGraphApiRef.get()
    }


    //This is used for retry
    private var initiatePaymentResponse: InitiatePaymentResponse? = null

    private var paymentFlowSource = NONE
    private var buyGoldContext = NONE

    private var showWeeklyChallengeAnim = false

    private var wasOnBoardingCompleted = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                analyticsHandler.postEvent(
                    EventKey.NotificationForSdk33, ALLOWED
                )
            } else {
                analyticsHandler.postEvent(
                    EventKey.NotificationForSdk33, DENIED
                )
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    getString(R.string.notification_permissions).snackBar(
                        binding.root
                    )
                    openPermissionSettings(
                        getString(R.string.notification_permissions), applicationContext
                    )
                }

            }
        }

    //runOnUiThread as callbacks in some cases come for PAYTM comes on different thread.. PAYTM SDK SUCKS..
    private var paymentListener: PaymentManager.OnPaymentResultListener? =
        object : PaymentManager.OnPaymentResultListener {
            override fun onLoading(showLoading: Boolean) {
                runOnUiThread {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                        if (showLoading) showProgressBar() else dismissProgressBar()
                    }
                }
            }

            override fun onSuccess(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
                runOnUiThread {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                        dismissProgressBar()
                        redirectForPaymentResult(fetchManualPaymentStatusResponse)
                    }
                }
            }

            override fun onError(
                message: String?,
                errorCode: String?,
                oneTimePaymentResult: OneTimePaymentResult?,
                shouldFetchPaymentStatus: Boolean,
            ) {
                runOnUiThread {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                        dismissProgressBar()
                        EventBus.getDefault()
                            .postSticky(ManualPaymentCompletedEvent(ManualPaymentStatus.FAILURE))
                        message.snackBarWithGenericFallback(binding.root)
                    }
                }
            }
        }

    private val homeViewModel: HomeActivityViewModel by viewModels()

    private val navDrawerViewModelProvider: SideNavDrawerViewModelAndroid by viewModels()

    private val navDrawerViewModel by lazy {
        navDrawerViewModelProvider.getInstance()
    }

    private val newOnboardingViewModelProvider: NewOnboardingViewModelAndroid by viewModels()

    private val newOnboardingViewModel by lazy {
        newOnboardingViewModelProvider.getInstance()
    }

    private var mandatePaymentJob: Job? = null

    private val conversionListener by lazy {
        object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                val map = mutableMapOf<String, String>()
                val adSourceData = AdSourceData(
                    mediaSource = p0?.get("media_source")?.toString(),
                    channel = p0?.get("af_channel")?.toString(),
                    adSet = p0?.get("adset")?.toString(),
                    campaign = p0?.get("campaign")?.toString(),
                    agency = p0?.get("agency")?.toString(),
                    status = p0?.get("af_status")?.toString(),
                    installTime = System.currentTimeMillis().toString(),
                    afAdType = p0?.get("af_ad_type")?.toString(),
                    deeplink = p0?.get("af_dp")?.toString(),
                )
                map["af_agency"] = adSourceData.agency.orEmpty()
                map["af_channel"] = adSourceData.channel.orEmpty()
                map["af_adset"] = adSourceData.adSet.orEmpty()
                map["af_campaign"] = adSourceData.campaign.orEmpty()
                map["af_campaign_id"] = p0?.get("campaign_id")?.toString() ?: ""
                map["af_is_first_launch"] = p0?.get("is_first_launch")?.toString() ?: ""
                map["af_media_source"] = adSourceData.mediaSource.orEmpty()
                map["af_status"] = adSourceData.status.orEmpty()
                map["af_http_referrer"] = p0?.get("http_referrer")?.toString() ?: ""
                sendEventAndRemoveListener(map)
                homeViewModel.updateAdSourceData(adSourceData)
            }

            override fun onConversionDataFail(p0: String?) {
                //No use
            }

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                //No use
            }

            override fun onAttributionFailure(p0: String?) {
                //No use
            }
        }
    }

    private val appsFlyerDeepLinkListener = DeepLinkListener { deepLinkResult ->
        if (deepLinkResult.status == DeepLinkResult.Status.FOUND) {
            deepLinkResult.deepLink?.deepLinkValue?.let {
                if (deepLinkResult.deepLink.deepLinkValue == BaseConstants.APPSFLYER_REFERRALS) {
                    val referrerId =
                        deepLinkResult.deepLink.getStringValue("deep_link_sub1").orEmpty()
                    prefs.setAppsFlyerReferralUserId(
                        referrerId
                    )
                    if (!homeViewModel.isReferralDeepLinkSynced && prefs.isLoggedIn()) {
                        updateAppsFlyerReferrerId(referrerId)
                    } else {
                        //Do Nothing
                    }
                } else {
                    /**
                    Deeplink from Appsflyer comes without prefix `dl.myjar.app/` , so adding it manually
                    Otherwise it will fail in {handleDeepLink} while splitting deeplink
                     */
                    /**
                    AppsFlyer do not allow special characters such as / in deeplink value hence
                    we will set the deeplink value in encoded format from dashboard
                    and decode it at FE before sending to handleDeepLink()
                     */
                    decodeUrl(it).takeIf { encodedDeepLinkValue -> encodedDeepLinkValue.isNotEmpty() }
                        ?.let { decodedDeepLinkValue ->
                            handleDeepLink(BaseConstants.BASE_EXTERNAL_DEEPLINK + decodedDeepLinkValue, methodCallSource = BaseConstants.HandleDeeplinkFlowSource.APPS_FLYER_DEEPLINK_LISTENER)
                        }
                }
            }
        }
    }

    private var toolbar: CustomToolbar? = null

    private var toast: Toast? = null

    private var isDeeplinkHandlingPending = false
    private var deferredDeepLinkValue: String? = null

    private var isPaymentSdkInitPending = true

    private var updateAFReferrerIdJob: Job? = null

    private var deepLinkJob: Job? = null

    private var selectedTabPosition = -1

    private var currentBottomFragmentName = "Home"

    private var newDailySavingAmount = 0f
    private var dailySavingFeatureFlow: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JarApp)
        super.onCreate(savedInstanceState)
        initOnboardingStateMachine()
        EventBus.getDefault().register(this)
        analyticsHandler.postEvent(EventKey.APP_LAUNCHED)
        if (BuildConfig.DEBUG.not()) window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )
        updateAppIcon()
        requestNotificationPermission()
        if (Random.nextBoolean()) setUserPhoneProperties()
        getUserInternetData()
        // Need to access newOnboardingViewModel here first..
        // If we directly access in onDestroy() then we are getting below error"
        // Error -> {SavedStateProvider with the given key is already registered}
        // https://console.firebase.google.com/u/1/project/changejarprod/crashlytics/app/android:com.jar.app/issues/32060f26424ef2696edb4d5c7bdac2c9?time=last-seven-days&versions=5.4.8%20(339)&sessionEventKey=6409850F015500014C06DBE69AB88FA0_1786885064892938133
        binding.bottomNavigationView.itemIconTintList = null
        if (prefs.isAppWalkThroughBeingShownToUser())
            prefs.setIsAppWalkThroughBeingShownToUser(false)
    }

    private fun updateAppIcon() {
        if (remoteConfigApi.isFestivalCampaignEnabled()) {
            packageManager.setComponentEnabledSetting(
                ComponentName(
                    this,
                    JarLauncherAlias::class.java
                ), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
            )
            packageManager.setComponentEnabledSetting(
                ComponentName(
                    this,
                    JarDiwaliLauncherAlias::class.java
                ), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
            )
        } else {
            packageManager.setComponentEnabledSetting(
                ComponentName(
                    this,
                    JarLauncherAlias::class.java
                ), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
            )
            packageManager.setComponentEnabledSetting(
                ComponentName(
                    this,
                    JarDiwaliLauncherAlias::class.java
                ), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
            )
        }
    }

    private fun initOnboardingStateMachine() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                onboardingStateMachine.initStateMachine(
                    hasSmsPermission = {
                        if (remoteConfigApi.isSmsPermissionRequired()) {
                            hasSmsPermission()
                        } else {
                            analyticsHandler.postEvent(eventName = EventKey.SMS_PERMISSION_NOT_REQUIRED)
                            true
                        }
                    }
                ).collectLatest {
                    val popUpTo =
                        navController.backQueue.find { !(it.destination is NavGraph) }?.destination
                    if (it.sideEffect == OnboardingStateMachine.SideEffect.RecreateApp) {
                        recreateApp()
                    }
                    if (it.sideEffect is OnboardingStateMachine.SideEffect.NavigateToCustomOnboarding) {
                        (it.sideEffect as OnboardingStateMachine.SideEffect.NavigateToCustomOnboarding).customOnboarding?.let {
                            handleDeepLink(it, methodCallSource = BaseConstants.HandleDeeplinkFlowSource.ONBOARDING_STATE_MACHINE)
                        } ?: run {
                            onboardingStateMachine.navigateAhead()
                        }
                    } else {
                        OnboardingNavigationUtil.getNavDirectionForOnboardingNavigation(
                            it
                        )?.let { navDirection ->
                            navigateTo(
                                navController = navController,
                                navDirections = navDirection,
                                overrideNavOptions = if (it.isBackPressRedirection)
                                    getNavOptionsMirrored(
                                        shouldAnimate = true,
                                        popUpToId = popUpTo?.id,
                                        inclusive = true
                                    )
                                else
                                    getNavOptions(
                                        shouldAnimate = true,
                                        popUpToId = popUpTo?.id,
                                        inclusive = true
                                    )
                            )
                        }
                    }
                }
            }
        }
    }


    private fun setUserPhoneProperties() {
        getPhoneAvailableStorage()
        checkWhichUiModeEnabled()
        getScreenSizeAndDpi()
        gestureEnabledCheck()
    }

    private fun getPhoneAvailableStorage() {
        val statExternal = StatFs(Environment.getExternalStorageDirectory().path)
        val statInternal = StatFs(Environment.getDataDirectory().path)
        val externalBytesAvailable = statExternal.availableBytes
        val internalBytesAvailable = statInternal.availableBytes
        val externalMegabytesAvailable = externalBytesAvailable / (1024 * 1024)
        val internalMegabytesAvailable = internalBytesAvailable / (1024 * 1024)
        analyticsHandler.setUserProperty(
            listOf(
                BaseConstants.EXTERNAL_AVAILABLE_STORAGE to externalMegabytesAvailable.toString(),
                BaseConstants.INTERNAL_AVAILABLE_STORAGE to internalMegabytesAvailable.toString(),
            )
        )
    }

    private fun checkWhichUiModeEnabled() {
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val uiMode = when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_NO -> "Light Mode"
            UiModeManager.MODE_NIGHT_YES -> "Dark Mode"
            else -> "Light"
        }
        analyticsHandler.setUserProperty(listOf(BaseConstants.UiMode to uiMode))
    }

    private fun getScreenSizeAndDpi() {
        val displayMetrics = resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val screenHeightPx = displayMetrics.heightPixels
        val screenDpi = displayMetrics.densityDpi
        val screenWidthDp = screenWidthPx / (displayMetrics.densityDpi / 160f)
        val screenHeightDp = screenHeightPx / (displayMetrics.densityDpi / 160f)
        analyticsHandler.setUserProperty(
            listOf(
                BaseConstants.ScreenDpi to screenDpi.toString(),
                BaseConstants.ScreenWidthDp to screenWidthDp.toString(),
                BaseConstants.ScreenHeightDp to screenHeightDp.toString(),
            )
        )
    }

    private fun gestureEnabledCheck() {
        analyticsHandler.setUserProperty(
            listOf(
                BaseConstants.IsGestureEnabled to this.isGestureNavigationEnabled().toString()
            )
        )
    }

    private fun getUserInternetData() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isWifiEnabled = connectivityManager.isWifiEnabled().toString()
        val internetSpeed = connectivityManager.currentInternetSpeed()
        analyticsHandler.postEvent(
            BaseConstants.User_Internet_Details, mapOf(
                BaseConstants.WifiEnabled to isWifiEnabled,
                BaseConstants.CurrentInternetSpeed to internetSpeed,
            )
        )
    }

    override fun setup() {
        init()
        setupUI()
        setupListeners()
        observeLiveData()
        if (BuildConfig.DEBUG)
            observeBackStack()
        handleDeepLinkFromIntent()
        logAnalytics(intent)
        AppLinkData.fetchDeferredAppLinkData(
            this
        ) {
            val host = it?.targetUri?.host
            val path = it?.targetUri?.path
            if (host != null && path != null) {
                val deferredDeeplink = host + path
                handleDeepLink(deepLink = deferredDeeplink, methodCallSource = BaseConstants.HandleDeeplinkFlowSource.FB_APP_LINK_DATA)
            }
        }
        if (prefs.isOnboardingComplete())
            homeViewModel.fetchFeatureRedirectionData()
    }

    private fun init() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavigation()
        homeViewModel.fetchDowntime()
    }

    private fun setupUI() {
        binding.networkSnackbar.progressBar.setIndicatorColor(
            ContextCompat.getColor(
                this, com.jar.app.core_ui.R.color.color_EB6A6E,
            )
        )
        binding.networkSnackbar.tvText.text =
            getString(R.string.could_not_connect_to_the_internet_please_try_again)
        binding.networkSnackbar.progressBar.progress = 100
        binding.networkSnackbar.ivIcon.setImageResource(R.drawable.ic_network_error)
        binding.networkSnackbar.ivIcon.isVisible = true
        binding.networkSnackbar.root.setBackgroundResource(R.drawable.bg_custom_snack_bar)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            if (prefs.isAppWalkThroughBeingShownToUser().not())
                selectBottomTabItem(it.itemId)
            return@setOnItemSelectedListener true
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
            if (prefs.isAppWalkThroughBeingShownToUser().not())
                EventBus.getDefault().post(NavItemReselectedEvent(it.itemId))
            return@setOnItemReselectedListener
        }
    }

    private fun selectBottomTabItem(destinationId: Int) {
        try {
            navController.getBackStackEntry(R.id.homePagerFragment).savedStateHandle[BaseConstants.TAB_SELECTED] =
                destinationId
            when (destinationId) {
                R.id.newHomeFragment -> HomeConstants.AnalyticsKeys.Home
                R.id.transactionFragment -> HomeConstants.AnalyticsKeys.Transactions
                R.id.accountFragment -> HomeConstants.AnalyticsKeys.Account
                else -> HomeConstants.AnalyticsKeys.Home
            }.let {
                currentBottomFragmentName = it
            }
        } catch (ex: Exception) {
            HomeConstants.AnalyticsKeys.Home
        }
    }

    private fun sendBottomNavAnalytics(id: Int) {
        when (id) {
            R.id.newHomeFragment -> EventKey.CLICKED_HOME_BOTTOM_NAV
            R.id.transactionFragment -> TransactionConstants.AnalyticsKeys.Clicked_Transactions
            R.id.accountFragment -> EventKey.Clicked_Account
            else -> null
        }?.let {
            analyticsHandler.postEvent(
                EventKey.Clicked_BottomNavTab, mapOf("Tab_Selected" to it)
            )
        }
    }

    private fun updateAppsFlyerReferrerId(referrerId: String) {
        updateAFReferrerIdJob?.cancel()
        updateAFReferrerIdJob = lifecycleScope.launch {
            homeViewModel.sendReferralId(
                referrerUserId = referrerId
            )
        }
    }

    private fun logAnalytics(intent: Intent?) {
        if (intent?.getStringExtra(BaseConstants.SOURCE) == BaseConstants.SOURCE_NOTIF) {
            analyticsHandler.postEvent(EventKey.NOTIFICATION_CLICKED)
        }
    }

    private fun setupListeners() {
        setupBackStackListener()

        binding.toolbarDefault.btnBack.setDebounceClickListener {
            onBackPressed()
        }
        binding.toolbarHome.ivNotification.setDebounceClickListener {
            navController.navigate(
                HomeNavigationDirections.actionToNotificationListFragment(), getBottomNavOptions()
            )
            val count = homeViewModel.userMetaLiveData.value?.data?.data?.notificationCount.orZero()
            analyticsHandler.postEvent(
                NotificationEvents.ClickedNotifications_Homescreen, mapOf(
                    NotificationEvents.State to if (count == 0L) NotificationEvents.NotificationState.Read else NotificationEvents.NotificationState.Read,
                    NotificationEvents.Count to count,
                    NotificationEvents.FromScreen to currentBottomFragmentName
                )
            )
        }
        binding.toolbarHome.clGoldBuyPrice.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.CLICKED_GOLD_LIVE_PRICE_HOME,
                mapOf(
                    EventKey.state to getGoldPriceTabState()
                )
            )
            if (prefs.isAppWalkThroughBeingShownToUser().not())
                goldPriceAlertsApi.openIntroScreen(EventKey.HOME_SCREEN)
        }

        binding.toolbarHome.ivHamburger.setDebounceClickListener {
            analyticsHandler.postEvent(
                HomeConstants.AnalyticsKeys.Clicked_HamburgerIcon,
                mapOf(EventKey.FromScreen to currentBottomFragmentName)
            )
//            rotateHamburger()
            if (prefs.isAppWalkThroughBeingShownToUser().not())
                binding.drawerLayout.open()
        }
        var timeStampHamburgerOpen = 0L
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                timeStampHamburgerOpen = System.currentTimeMillis()
                val menuItemsMap: MutableMap<String, String> = HashMap()
                navDrawerViewModel.hamburgerMenuFlow.value.data?.data?.hamburgerItems?.hamburgerItems?.let {
                    it.forEachIndexed { index, hamburgerItem ->
                        menuItemsMap["${HomeConstants.AnalyticsKeys.Tile} ${index + 1}"] =
                            hamburgerItem.text.orEmpty()
                    }
                }
                menuItemsMap[HomeConstants.AnalyticsKeys.Winnings_Status] =
                    navDrawerViewModel.hamburgerMenuFlow.value.data?.data?.hamburgerItems?.hamburgerHeader?.text.orEmpty()
                        .getHtmlTextValue().toString()
                analyticsHandler.postEvent(
                    HomeConstants.AnalyticsKeys.Shown_HamburgerMenu,
                    menuItemsMap
                )
            }

            override fun onDrawerClosed(drawerView: View) {
                analyticsHandler.postEvent(
                    HomeConstants.AnalyticsKeys.Exit_HamburgerMenu,
                    mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeStampHamburgerOpen)
                )
            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })

        if (prefs.shouldSyncAppsFlyerAttributionData()) appsFlyerLib.registerConversionListener(
            applicationContext, conversionListener
        )
        appsFlyerLib.subscribeForDeepLink(appsFlyerDeepLinkListener)

        binding.toolbarHome.storyRippleLayout.setDebounceClickListener {
            if (prefs.isAppWalkThroughBeingShownToUser().not()) {
                binding.networkSnackbar.root.isVisible = false
                analyticsHandler.postEvent(
                    Clicked_StoryIcon,
                    mapOf(
                        "state" to NoNetwork,
                        SeenPage to "${homeViewModel.inAppStoryFlow.value.data?.data?.seenPages}",
                        UnseenPage to "${(homeViewModel.inAppStoryFlow.value.data?.data?.totalPages ?: 0) - (homeViewModel.inAppStoryFlow.value.data?.data?.seenPages ?: 0)}",
                        StoryId to "${homeViewModel.inAppStoryFlow.value.data?.data?.storyId}",
                        StoryName to "${homeViewModel.inAppStoryFlow.value.data?.data?.storyName}"
                    )
                )
                storiesApi.openStoriesPage()
            }
        }
    }

    private fun observeBackStack() {
        uiScope.countDownTimer(100000000, 1000, onInterval = {
            val queue = navController.backQueue.map {
                it.destination
            }.filterNot {
                it is NavGraph
            }.joinToString(" > ") {
                it.displayName.split('/')[1]
            }

            Timber.d("navController.graph : $queue")

        }, onFinished = {})
    }

    private fun setupBackStackListener() {
        uiScope.launch {
            navController.currentBackStackEntryFlow.collectLatest {
                if (isActive) {
                    if (it.destination.id == R.id.homePagerFragment) {
                        homeViewModel.fetchUserMetaData()
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    } else binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    updateBottomTabVisibility(it.destination.id)
                }
            }
        }
    }

    private fun updateBottomTabVisibility(id: Int) {
        val shouldShow = shouldShowBottomTab(id)
        binding.clBottomNav.isVisible = shouldShow
        if (shouldShow) {
            binding.bottomNavigationView.selectedItemId = id
        }
    }

    private fun shouldShowBottomTab(id: Int): Boolean {
        return id == R.id.homePagerFragment
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        observeHomeScreenScroll()
        observeForceUpdateLiveData(viewRef)
        observeGenericLiveData(viewRef)
        observeInitiateManualPaymentLiveData(viewRef)
        observeGoldTrendHomeScreenTabFlow()
        observeBuyGoldLiveData(viewRef)
        observeSurveyLiveData(viewRef)
        observeKycStatusLiveData(viewRef)
        observeDownTimeLiveData(viewRef)
        observeGiftReceivedLiveData(viewRef)
        observeUserRatingLiveData(viewRef)
        observeNetworkStateLiveData(viewRef)
        observeNavDrawerCloseLiveData(viewRef)
        observeAppUpdateClick(viewRef)
        observePopupInfoLiveData(viewRef)
        observeDeviceDetailsAPI(viewRef)
        observeLendingKycProgressLiveData(viewRef)
        observeLoanApplicationsLiveData(viewRef)
        observeContactData(viewRef)
        observeSmsSyncOnDemand(viewRef)
        observeAutoPayResetRequiredLiveData(viewRef)
        observeNotificationMetaLiveData(viewRef)
        observeInAppStoryFlow()
    }

    private fun observeGoldTrendHomeScreenTabFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.goldTrendHomeScreenTabFlow.collect(
                    onSuccess = {
                        binding.toolbarHome.tvGoldPrice.setHtmlText(it.title)
                        if (it.infographic.getInfographicType() == GoldTrendHomeScreenTabInfographicType.IMAGE) {
                            Glide.with(this@HomeActivity).load(it.infographic.url).into(binding.toolbarHome.goldPriceIcon)
                            binding.toolbarHome.goldPriceLottie.isVisible = false
                            binding.toolbarHome.goldPriceIcon.isVisible = true
                            binding.toolbarHome.shimmerGoldBuyPrice.startShimmer()
                        } else {
                            binding.toolbarHome.goldPriceLottie.speed = it.infographic.getLottieSpeed()
                            binding.toolbarHome.goldPriceLottie.playLottieWithUrlAndExceptionHandling(this@HomeActivity, it.infographic.url)
                            binding.toolbarHome.goldPriceLottie.isVisible = true
                            binding.toolbarHome.goldPriceIcon.isVisible = false
                            binding.toolbarHome.shimmerGoldBuyPrice.stopShimmer()
                        }
                        toggleBuyPriceVisibilityAndFireEvent((toolbar is ToolbarHome && (toolbar as ToolbarHome).showGoldPrice.orFalse()))
                    }
                )
            }
        }
    }

    private fun observeHomeScreenScroll() {
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                scrollStateProvider.scrollStateFlow.collectLatest {
                    if (navController.currentDestination?.id.orZero() == R.id.homePagerFragment)
                        binding.clToolbarContainer.isVisible = it.scrolled.not()
                }
            }
        }
    }

    private fun observeInAppStoryFlow() {
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                homeViewModel.inAppStoryFlow.collect(
                    onLoading = {
                        binding.toolbarHome.storyRippleLayout.stopRippleAnimation()
                    },
                    onSuccess = { inAppStoryModel ->
                        val content =
                            Json.encodeToString(inAppStoryModel)//Json.encodeToString(apiResponse)
                        //TODO @Prasenjit to be changed to local db implementation later on when the db is finalised for shared data
                        fileUtils.storeContentToFile(content, "story_data.json")
                        inAppStoryModel.pages?.getOrNull(0)?.let {
                            setUpClickListenerOnStoryIconAndSentEvent(inAppStoryModel, "no story")
                            lifecycleScope.launch(Dispatchers.IO) {
                                val glideUrl = GlideUrl(
                                    it.mediaUrl,
                                    LazyHeaders.Builder()
                                        .addHeader(
                                            "Cache-Control",
                                            "max-age=" + TimeUnit.DAYS.toSeconds(20)
                                        ) // Cache for 7 days
                                        .build()
                                )
                                Glide.with(this@HomeActivity)
                                    .load(glideUrl)
                                    .listener(
                                        object : RequestListener<Drawable> {
                                            override fun onLoadFailed(
                                                e: GlideException?,
                                                model: Any?,
                                                target: Target<Drawable>?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                binding.toolbarHome.storyRippleLayout.stopRippleAnimation()
                                                binding.toolbarHome.storyView.setIsDisable(true)
                                                return false
                                            }

                                            override fun onResourceReady(
                                                resource: Drawable?,
                                                model: Any?,
                                                target: Target<Drawable>?,
                                                dataSource: DataSource?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                onInAppFirstContentDownload(inAppStoryModel)
                                                return false
                                            }
                                        }
                                    )
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .submit()
                            }
                        } ?: run {
                            setUpClickListenerOnStoryIconAndSentEvent(inAppStoryModel, "no story")
                        }
                    },
                    onError = { _, _ ->
                        binding.toolbarHome.storyRippleLayout.stopRippleAnimation()
                        binding.toolbarHome.storyView.setIsDisable(true)
                    }
                )
            }
        }
    }

    private fun setUpClickListenerOnStoryIconAndSentEvent(
        inAppStoryModel: InAppStoryModel,
        state: String = ""
    ) {
        binding.toolbarHome.storyRippleLayout.setDebounceClickListener {
            analyticsHandler.postEvent(
                Clicked_StoryIcon,
                mapOf(
                    "state" to state,
                    SeenPage to "${inAppStoryModel.seenPages}",
                    UnseenPage to "${(inAppStoryModel.totalPages ?: 0) - (inAppStoryModel.seenPages ?: 0)}",
                    StoryId to "${inAppStoryModel.storyId}",
                    StoryName to "${inAppStoryModel.storyId}"
                )
            )
            storiesApi.openStoriesPage()
        }
    }

    private fun onInAppFirstContentDownload(inAppStoryModel: InAppStoryModel) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (inAppStoryModel.isPulsating == true)
                binding.toolbarHome.storyRippleLayout.startRippleAnimation()
            val state = if (inAppStoryModel.isPulsating == true) {
                "pulsating"
            } else if (inAppStoryModel.totalPages == inAppStoryModel.seenPages) {
                "all seen"
            } else if (inAppStoryModel.seenPages == 0) {
                "all unseen"
            } else {
                "partially seen"
            }
            homeViewModel.postAnalyticsEvent(
                Shown_StoryIcon,
                mapOf(
                    "state" to state,
                    SeenPage to (inAppStoryModel.seenPages ?: ""),
                    UnseenPage to "${(inAppStoryModel.totalPages ?: 0) - (inAppStoryModel.seenPages ?: 0)}",
                    StoryId to "${inAppStoryModel.storyId}",
                    StoryName to "${inAppStoryModel.storyName}",
                    TotalPage to "${inAppStoryModel.totalPages}"
                )
            )
            binding.toolbarHome.storyView.setSegmentColorRangeAndIsDisable(
                inAppStoryModel.seenPages.orZero(),
                false,
                inAppStoryModel.totalPages.orZero()
            )
            binding.toolbarHome.storyRippleLayout.setDebounceClickListener {
                binding.toolbarHome.storyRippleLayout.stopRippleAnimation()
                analyticsHandler.postEvent(
                    Clicked_StoryIcon,
                    mapOf(
                        "state" to state,
                        SeenPage to "${inAppStoryModel.seenPages}",
                        UnseenPage to "${(inAppStoryModel.totalPages ?: 0) - (inAppStoryModel.seenPages ?: 0)}",
                        StoryId to "${inAppStoryModel.storyId}",
                        StoryName to "${inAppStoryModel.storyName}",
                        TotalPage to "${inAppStoryModel.totalPages}"
                    )
                )
                storiesApi.openStoriesPage()
            }
        }
    }

    private fun observeContactData(viewRef: WeakReference<View>) {
        homeViewModel.contactResponseLiveData.observeNetworkResponse(
            this,
            viewRef,
            onSuccess = {
                hasContactSynced = it?.isContactSynced.orFalse()
            }
        )
    }

    private fun observeSmsSyncOnDemand(viewRef: WeakReference<View>) {
        homeViewModel.smsOnDemandLiveData.observeNetworkResponse(
            this,
            viewRef,
            onSuccess = {
                if (it.sendSms && prefs.isOnDemandSmsSent().not()) {
                    smsSyncApi.startMessageSync(it.numberOfDays, true)
                    prefs.setOnDemandSmsSent(true)
                }
            }, suppressError = true
        )

    }

    private fun observeAppUpdateClick(viewRef: WeakReference<View>) {
        homeViewModel.appUpdate.observe(this) {
            it?.let {
                update(it)
            }
        }
    }

    private fun observePopupInfoLiveData(viewRef: WeakReference<View>) {
        homeViewModel.popupInfoLiveData.observeNetworkResponse(this, viewRef, onSuccess = {
            coreUiApi.openInfoDialog(it.helpWorkFlow)
        })
    }


    private fun observeDeviceDetailsAPI(viewRef: WeakReference<View>) {
        homeViewModel.updateUserDeviceDetailsLiveData.observeNetworkResponse(
            this,
            viewRef,
            onSuccess = {
                homeViewModel.isAppsFlyerIdSynced = true
            },
            onSuccessWithNullData = {
                homeViewModel.isAppsFlyerIdSynced = true
            })
    }

    private fun checkAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, IMMEDIATE, this, APP_UPDATE
                    )
                }

                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    IMMEDIATE
                ) -> {
                    homeViewModel.appUpdateAvailable(appUpdateInfo)
                }
            }
        }
    }

    private fun observeNavDrawerCloseLiveData(viewRef: WeakReference<View>) {
        homeViewModel.closeNavDrawer.observe(this) {
            if (it == true) {
                binding.drawerLayout.close()
            } else {
                binding.drawerLayout.open()
            }
        }
    }

    private fun observeNotificationMetaLiveData(viewRef: WeakReference<View>) {
        homeViewModel.userMetaLiveData.observeNetworkResponse(this, viewRef, onSuccess = {
            if (remoteConfigApi.isShowInAppStory()) {
                (it?.notificationCount.orZero() > 0).also {
                    binding.toolbarHome.notificationDot.isVisible = it
                }
            } else {
                if (it?.notificationCount.orZero() > 0) {
                    binding.toolbarHome.ivNotification.isVisible = true
                    binding.toolbarHome.tvNotificationDot.isVisible = true
                    binding.toolbarHome.tvNotificationDot.text = it?.notificationCount.toString()
                } else {
                    binding.toolbarHome.tvNotificationDot.isVisible = false
                }
            }
        })
    }

    private fun observeForceUpdateLiveData(viewRef: WeakReference<View>) {
        homeViewModel.shouldShowForceUpdateDialog.observeNetworkResponse(
            this,
            viewRef,
            onSuccess = {
                if (it.shouldShowForceUpdate && it.minVersionCode > getAppVersionCode()) navigateTo(
                    navController,
                    HomeNavigationDirections.actionToForceUpdateDialog()
                )
            }, onError = {
                if (remoteConfigApi.getMinimumSupportedVersion() > getAppVersionCode()) navigateTo(
                    navController,
                    HomeNavigationDirections.actionToForceUpdateDialog()
                )
            }, suppressError = true
        )
    }

    private fun observeUserRatingLiveData(viewRef: WeakReference<View>) {
        homeViewModel.userRatingLiveData.observeNetworkResponse(
            this,
            viewRef,
            onSuccessWithNullData = {
                if (navController.currentDestination?.id != R.id.rateUsDialog) {
                    navigateTo(
                        navController,
                        HomeNavigationDirections.actionToRateUsDialog(null, null),
                        false
                    )
                }
            },
            onSuccess = {
                if (navController.currentDestination?.id != R.id.rateUsDialog && it != null) {
                    navigateTo(
                        navController,
                        HomeNavigationDirections.actionToRateUsDialog(it.title, it.subTitle),
                        false
                    )
                }
            })
    }

    private fun observeGiftReceivedLiveData(viewRef: WeakReference<View>) {
        homeViewModel.receivedGiftLiveData.observeNetworkResponse(this, viewRef, onSuccess = {
            it.forEach {
                giftingApi.openViewGiftScreen(it)
            }
        })
    }

    private fun observeDownTimeLiveData(viewRef: WeakReference<View>) {
        homeViewModel.downtimeDetails.observeNetworkResponse(
            this,
            viewRef,
            onSuccess = {
                it?.let {
                    if (it.isAppUnderDowntimeMaintenance()) {
                        navigateTo(
                            navController,
                            HomeNavigationDirections.actionToDowntimeDialog(it.getTimeLeftTillDowntimeEndsInMillis())
                        )
                    } else if (it.isDowntimeScheduledInFuture()) {
                        uiScope.countDownTimer(
                            it.getTimeLeftTillDowntimeStartsInMillis(),
                            onFinished = {
                                navigateTo(
                                    navController,
                                    HomeNavigationDirections.actionToDowntimeDialog(it.getTimeLeftTillDowntimeEndsInMillis())
                                )
                            })
                    }
                }
            },
        )
    }

    private fun observeAutoPayResetRequiredLiveData(viewRef: WeakReference<View>) {
        homeViewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            this,
            viewRef,
            onSuccess = {
                if (it.isResetRequired)
                    updateDailySavingAndStatusScreenRedirection(
                        it.getFinalMandateAmount(),
                        it.authWorkflowType,
                        null
                    )
                else {
                    homeViewModel.updateDailySaving(newDailySavingAmount)
                    coreUiApi.openGenericPostActionStatusFragment(
                        GenericPostActionStatusData(
                            postActionStatus = PostActionStatus.ENABLED.name,
                            header = getString(com.jar.app.feature_daily_investment.R.string.feature_daily_investment_daily_investment_setup_successfully),
                            headerColorRes = com.jar.app.core_ui.R.color.color_1EA787,
                            title = getString(
                                com.jar.app.feature_daily_investment.R.string.feature_daily_investment_x_will_be_auto_saved_starting_tomorrow,
                                newDailySavingAmount.toInt()
                            ),
                            titleColorRes = com.jar.app.core_ui.R.color.white,
                            imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                            headerTextSize = 18f,
                            titleTextSize = 16f
                        )
                    ) { EventBus.getDefault().post(RefreshDailySavingEvent()) }
                }
            },
        )
    }

    private var shouldReFetchOnNetworkStateChange = false

    private fun observeNetworkStateLiveData(viewRef: WeakReference<View>) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkFlow.networkStatus.collectLatest { isOnline ->
                    binding.networkSnackbar.root.isVisible = !isOnline
                    if (isOnline) {
                        if (shouldReFetchOnNetworkStateChange) {
                            getData()
                            shouldReFetchOnNetworkStateChange = false
                        }

                    } else {
                        shouldReFetchOnNetworkStateChange = true
                        analyticsHandler.postEvent(
                            EventKey.ShownInternetErrorMessage,
                            mapOf(EventKey.MESSAGE to binding.networkSnackbar.tvText.text.toString())
                        )

                    }
                }
            }
        }
    }

    private fun getData() {
        if (prefs.isLoggedIn()) {
            syncAppsFlyerUID()
            homeViewModel.captureAppOpens()
            homeViewModel.fetchSurvey()
            homeViewModel.updateSession()
            homeViewModel.fetchUserGoldBalance()
            navDrawerViewModel.fetchHamburgerMenuItems()
            homeViewModel.fetchReceivedGift()
            homeViewModel.fetchAppVersionData()
            homeViewModel.shouldSendSmsOnDemand()
            homeViewModel.fetchContactList()
            getGoldBuyPriceTab()
            homeViewModel.fetchGoldBuyPrice()
            appsFlyerInviteUtil.getAppsFlyerInviteLink()?.let {
                homeViewModel.fetchReferEarnMsgLinks(it)
            }
            if (remoteConfigApi.isShowInAppStory())
                homeViewModel.fetchInAppStory()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateBuyGoldPriceInToolbar(event: UpdateBuyGoldPriceInToolbarEvent) {
        getGoldBuyPriceTab()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenUpiSelectionScreen(openUpiSelectionScreen: OpenUpiSelectionScreen) {
        val enteredAmount = openUpiSelectionScreen.enteredAmount
        uiScope.launch {
            navigateTo(
                navController,
                "${BaseConstants.InternalDeepLinks.SELL_GOLD_REVAMP}/upiSelectionFragment/${
                    encodeUrl(
                        serializer.encodeToString(VpaSelectionArgument(withdrawalPrice = enteredAmount))
                    )
                }"
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHomeFragmentOnCardShownEvent(event: HomeFragmentOnCardShownEvent) {
        val app = applicationContext as JarApp
        analyticsHandler.postEvent(
            com.jar.app.feature_homepage.shared.util.EventKey.Shown_HomeScreenCard_Ts, mapOf(
                EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(
                    endTimeTime = event.currentTime,
                    startTime = app.appStartTime
                )
            ), true
        )
    }

    private fun setUserData(user: User) {
        if (prefs.isLoggedIn()) {
            val userId = user.userId!!
            enableSmsProcessing()
            firebaseCrashlytics.setUserId(userId)
            appsFlyerInviteUtil.initAppsFlyerInviteLink(userId)
        }
    }

    private fun enableSmsProcessing() {
        if (hasSmsPermission()) {
            smsSyncApi.startMessageSync(homeViewModel.numberOfDaysOfSms, false)
        }
    }

    private fun observeGenericLiveData(viewRef: WeakReference<View>) {
        userLiveData.distinctUntilChanged().observe(this) {
            if (it != null) {
                setUserData(it)
                if (isPaymentSdkInitPending) {
                    isPaymentSdkInitPending = false
                    paymentManager.init(it.userId!!)
                }
            }
        }
    }

    private fun updateAppBar(it: AppBarData) {
        this.toolbar = it.toolbar
        when (it.toolbar) {
            is ToolbarHome -> {
                val it = it.toolbar as ToolbarHome
                binding.clToolbarContainer.isVisible = true
                binding.toolbarHome.root.isVisible = true
                binding.toolbarDefault.root.isVisible = false
                toggleBuyPriceVisibilityAndFireEvent(it.showGoldPrice)
                binding.toolbarHome.separator.isVisible = it.showSeparator
                if (remoteConfigApi.isShowInAppStory().not()) {
                    binding.toolbarHome.storyRippleLayout.isVisible = false
                    binding.toolbarHome.ivNotification.isVisible = it.showNotification
                    binding.toolbarHome.tvNotificationDot.text =
                        homeViewModel.userMetaLiveData.value?.data?.data?.notificationCount.toString()
                    binding.toolbarHome.tvNotificationDot.isVisible =
                        it.showNotification && homeViewModel.userMetaLiveData.value?.data?.data?.notificationCount.orZero() > 0
                } else {
                    binding.toolbarHome.storyRippleLayout.isVisible = it.showStoryIcon
                }
            }

            is ToolbarDefault -> {
                val it = it.toolbar as ToolbarDefault
                binding.clToolbarContainer.isVisible = true
                binding.toolbarHome.root.isVisible = false
                binding.toolbarDefault.root.isVisible = true
                binding.toolbarDefault.tvTitle.text = it.title.orEmpty()
                binding.toolbarDefault.btnBack.isVisible = it.showBackButton
                binding.toolbarDefault.separator.isVisible = it.showSeparator

                it.backFactorScale?.let {
                    binding.toolbarDefault.btnBack.scaleY = it
                    binding.toolbarDefault.btnBack.scaleX = it
                } ?: run {
                    binding.toolbarDefault.btnBack.scaleY = 1f
                    binding.toolbarDefault.btnBack.scaleX = 1f
                }
            }

            is ToolbarNone -> {
                binding.clToolbarContainer.isVisible = false
            }

            is CustomisableToolbarHome -> {
                val it = it.toolbar as CustomisableToolbarHome
                binding.clToolbarContainer.isVisible = true
                binding.toolbarHome.root.isVisible = true
                binding.toolbarDefault.root.isVisible = false
                toggleBuyPriceVisibilityAndFireEvent(it.showGoldPrice)
                binding.toolbarHome.separator.isVisible = it.showSeparator
                if (remoteConfigApi.isShowInAppStory().not()) {
                    binding.toolbarHome.ivNotification.isVisible = it.showNotification
                    binding.toolbarHome.tvNotificationDot.text =
                        homeViewModel.userMetaLiveData.value?.data?.data?.notificationCount.toString()
                    binding.toolbarHome.tvNotificationDot.isVisible =
                        it.showNotification && homeViewModel.userMetaLiveData.value?.data?.data?.notificationCount.orZero() > 0
                } else {
                    binding.toolbarHome.storyRippleLayout.isVisible = it.showStoryIcon
                }
            }
        }
    }

    private fun toggleBuyPriceVisibilityAndFireEvent(shouldShownInToolbar: Boolean) {
        val shouldShow = shouldShownInToolbar && homeViewModel.goldTrendHomeScreenTabFlow.value.data?.success.orFalse()
        binding.toolbarHome.clGoldBuyPrice.isVisible = shouldShow
        if (shouldShow) {
            analyticsHandler.postEvent(
                EventKey.ShownGoldLivePrice_HomeScreen,
                mapOf(
                    EventKey.state to getGoldPriceTabState()
                )
            )
        }
    }

    private fun getGoldPriceTabState() =
        homeViewModel.goldTrendHomeScreenTabFlow.value.data?.data?.pillType ?: homeViewModel.goldTrendHomeScreenTabFlow.value.data?.data?.title.orEmpty().getHtmlTextValue().toString()

    private fun getGoldBuyPriceTab() {
        homeViewModel.fetchGoldTrendTab()
    }

    private fun observeBuyGoldLiveData(viewRef: WeakReference<View>) {
        homeViewModel.buyGoldLiveData.observeNetworkResponse(
            this,
            viewRef,
            onLoading = {
                showProgressBar()
            }, onSuccess = {
                it?.let {
                    dismissProgressBar()
                    initiatePayment(it)
                }
            }, onError = {
                dismissProgressBar()
            }
        )
    }

    private fun observeSurveyLiveData(viewRef: WeakReference<View>) {
        homeViewModel.surveyLiveData.observeNetworkResponse(this, viewRef, onSuccess = {
            it?.let {
                if (it.surveyId != prefs.getLastSurveyConsumed()
                    && !it.surveyQuestions.isNullOrEmpty()
                    && isCurrentlyAtHomeFragment()
                ) {
                    getString(R.string.survey_pop_loader).snackBar(
                        binding.root,
                        lottieRes = R.raw.counter
                    )
                    uiScope.launch {
                        delay(3000)
                        prefs.setLastSurveyConsumed(it.surveyId)
                        navigateTo(
                            navController,
                            HomeNavigationDirections.actionToSurveyFragment()
                        )
                    }
                }
            }
        }
        )
    }

    private fun observeKycStatusLiveData(viewRef: WeakReference<View>) {
        homeViewModel.userKycStatusLiveData.observeNetworkResponse(
            this,
            viewRef,
            onLoading = {
                if (homeViewModel.kycFlowType != BaseConstants.KycFlowType.SELL_GOLD) showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                it?.let {
                    if (it.isVerified()) {
                        if (homeViewModel.isKycDeeplinkHandlingPending) {
                            homeViewModel.isKycDeeplinkHandlingPending = false
                            when (homeViewModel.kycFlowType) {
                                BaseConstants.KycFlowType.SELL_GOLD -> {
                                    val lendingDeepLink =
                                        BaseConstants.BASE_EXTERNAL_DEEPLINK + ExternalDeepLinks.LENDING_ONBOARDING
                                    val readyCashHamburgerItem =
                                        navDrawerViewModel.hamburgerMenuFlow.value.data?.data?.hamburgerItems?.hamburgerItems?.find { item -> item.deepLink == lendingDeepLink }
                                    if (readyCashHamburgerItem != null && homeViewModel.isLoanTaken.not()) {
                                        openLendingOnboardingFlow(BaseConstants.LendingFlowType.SELL_GOLD)
                                    } else {
                                        sellGoldApi.openSellGoldFlow()
                                    }
                                }

                                else -> {
                                    sellGoldApi.openSellGoldFlow()
                                }
                            }
                        }
                    } else {
                        sellGoldApi.openSellGoldFlow()
//                        if (homeViewModel.kycFlowType == BaseConstants.KycFlowType.SELL_GOLD) {
//                            analyticsHandler.postEvent(
//                                EventKey.Withdrawal_ID_Verification, mapOf(EventKey.Shown to it)
//                            )
//                            if (homeViewModel.isKycRequired) {
//                                sellGoldApi.openSellGoldFlow()
//                            } else {
//                                navigateTo(
//                                    navController, BaseConstants.InternalDeepLinks.SELL_GOLD
//                                )
//                            }
//                        } else {
////                            kycApi.openKYC(it, BaseConstants.KycFromScreen.DEEPLINK)
//                            sellGoldApi.openSellGoldFlow()
//                        }
                    }
                    homeViewModel.kycFlowType = BaseConstants.KycFlowType.DEFAULT
                }
            },
            onError = {
                dismissProgressBar()
            })
    }

    private fun observeLendingKycProgressLiveData(viewRef: WeakReference<View>) {
        homeViewModel.userLendingKycProgressLiveData.observeNetworkResponse(this,
            view = viewRef,
            onLoading = {},
            onSuccess = {
                if (it?.kycVerified.orFalse()) {
                    if (homeViewModel.isKycDeeplinkHandlingPending) {
                        when (homeViewModel.kycFlowType) {
                            BaseConstants.KycFlowType.SELL_GOLD -> {
                                homeViewModel.isKycDeeplinkHandlingPending = false
                                val lendingDeepLink =
                                    BaseConstants.BASE_EXTERNAL_DEEPLINK + ExternalDeepLinks.LENDING_ONBOARDING
                                val readyCashHamburgerItem =
                                    navDrawerViewModel.hamburgerMenuFlow.value.data?.data?.hamburgerItems?.hamburgerItems?.find { item -> item.deepLink.orEmpty() == lendingDeepLink }
                                if (readyCashHamburgerItem != null && homeViewModel.isLoanTaken.not()) {
                                    openLendingOnboardingFlow(BaseConstants.LendingFlowType.SELL_GOLD)
                                } else {
                                    sellGoldApi.openSellGoldFlow()
//                                    navigateTo(
//                                        navController, BaseConstants.InternalDeepLinks.SELL_GOLD
//                                    )
                                }
                            }

                            else -> {
                                homeViewModel.fetchUserKycStatus()
                            }
                        }
                    }
                } else {
                    homeViewModel.fetchUserKycStatus()
                }
            },
            onError = {})
    }

    private fun observeLoanApplicationsLiveData(viewRef: WeakReference<View>) {
        homeViewModel.loanApplicationsLiveData.observeNetworkResponse(
            this,
            viewRef,
            onLoading = {

            },
            onSuccess = {

                it?.let {
                    homeViewModel.isLoanTaken =
                        it.status == LoanApplicationStatusV2.DISBURSED.name || it.status == LoanApplicationStatusV2.FORECLOSED.name || it.status == LoanApplicationStatusV2.FAILED.name
                    homeViewModel.isKycDeeplinkHandlingPending = true
                    homeViewModel.kycFlowType = BaseConstants.KycFlowType.SELL_GOLD
                    homeViewModel.fetchUserLendingKycProgress()
                }
            },
            onError = {

                homeViewModel.isLoanTaken = false
                homeViewModel.isKycDeeplinkHandlingPending = true
                homeViewModel.kycFlowType = BaseConstants.KycFlowType.SELL_GOLD
                homeViewModel.fetchUserLendingKycProgress()
            }, suppressError = true
        )
    }

    /************ Detected RoundOff Payment ************/
    private fun initiateDetectedRoundOffsPayment(initiateDetectedRoundOffsPaymentRequest: com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest) {
        homeViewModel.initiateDetectedSpendPayment(
            initiateDetectedRoundOffsPaymentRequest, getCurrentPaymentGateway()
        )
        analyticsHandler.postEvent(
            EventKey.PaymentInitiated, mapOf(
                EventKey.IS_PAYTM_INSTALLED to isPackageInstalled(com.jar.app.feature_mandate_payment.BuildConfig.PAYTM_PACKAGE)
            )
        )
    }

    /************ End Region ************/

    private fun initiatePayment(initiatePaymentResponse: InitiatePaymentResponse) {
        initiatePaymentResponse.screenSource = paymentFlowSource
        this.initiatePaymentResponse = initiatePaymentResponse
        paymentManager.initiate(initiatePaymentResponse, paymentListener!!)
        analyticsHandler.postEvent(
            EventKey.PaymentInitiated, mapOf(
                EventKey.IS_PAYTM_INSTALLED to isPackageInstalled(com.jar.app.feature_mandate_payment.BuildConfig.PAYTM_PACKAGE)
            )
        )
    }

    private fun observeInitiateManualPaymentLiveData(viewRef: WeakReference<View>) {
        homeViewModel.initiateDetectedSpendPaymentLiveData.observeNetworkResponse(
            this,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                it?.let { initiatePaymentResponse ->
                    dismissProgressBar()
                    paymentFlowSource = BaseConstants.ManualPaymentFlowType.DetectSpendPayment
                    //Redirect To Home Screen On Initiate Manual Payment Status Success/Error
                    popBackStack(navController, R.id.homePagerFragment, false)
                    initiatePayment(initiatePaymentResponse)
                }
            },
            onError = {
                //Redirect To Home Screen On Initiate Manual Payment Status Success/Error
                popBackStack(navController, R.id.homePagerFragment, false)
                EventBus.getDefault()
                    .postSticky(ManualPaymentCompletedEvent(ManualPaymentStatus.FAILURE))
                dismissProgressBar()
            })
    }

    private fun redirectForPaymentResult(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        popBackStack(navController, R.id.homePagerFragment, false)
        dismissProgressBar()

        cacheEvictionUtil.evictHomePageCache()
        val challengeData = fetchManualPaymentStatusResponse.weeklyChallengeResponse

        when {
            fetchManualPaymentStatusResponse.sendGiftResponse != null -> {
                giftingApi.redirectToGiftStatusScreen(fetchManualPaymentStatusResponse)
            }

            fetchManualPaymentStatusResponse.goldDeliveryResponse != null -> {
                if (fetchManualPaymentStatusResponse.getManualPaymentStatus() == ManualPaymentStatus.SUCCESS) {
                    navigateTo(
                        navController, "android-app://com.jar.app/goldDeliverySuccessSteps"
                    )
                }
            }

            else -> {
                if (paymentFlowSource == BaseConstants.ManualPaymentFlowType.GoldSipManualPayment || fetchManualPaymentStatusResponse.type == BaseConstants.ManualPaymentFlowType.GoldSipManualPayment) EventBus.getDefault()
                    .post(RefreshGoldSipEvent())
                openBuyGoldOrderStatusFlow(fetchManualPaymentStatusResponse)
            }

        }

        if (fetchManualPaymentStatusResponse.getManualPaymentStatus() == ManualPaymentStatus.SUCCESS) {
            //If payment is successful check for Rating Dialog
            checkForRating()
            prefs.setFirstSuccessfulInvestment(false)
        }
    }

    private fun openBuyGoldOrderStatusFlow(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        fetchManualPaymentStatusResponse.transactionId?.let { transactionId ->
            fetchManualPaymentStatusResponse.paymentProvider?.let { paymentProvider ->
                uiScope.launch {
                    buyGoldApi.openOrderStatusFlow(
                        transactionId,
                        paymentProvider,
                        buyGoldContext,
                        fetchManualPaymentStatusResponse.oneTimeInvestment.orFalse(),
                        buyGoldContext
                    )
                }
            }
        }
    }

    private fun checkForRating() {
        if (remoteConfigApi.isShowingRatingDialog() && (prefs.getRatingDialogShownCount() < BaseConstants.MAX_RATING_ATTEMPTS) && !prefs.isFirstSuccessfulInvestment()) {
            homeViewModel.fetchUserRating()
        }
    }

    /************ End Region ************/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        paymentManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails,
                // you can request to start the update again.
                showToast(getString(R.string.update_failed))
            }
        }
        paymentManager.onActivityResult(requestCode, resultCode, data)
        mandatePaymentApi.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        checkAppUpdate()
    }

    override fun onBackPressed() {
        handleBackPress()
    }

    override fun onStart() {
        super.onStart()
        checkIfAppUpdated()
    }

    private fun handleBackPress() {
        val isBackPressHandled = paymentManager.onBackPress()
        if (!isBackPressHandled) {
            when {
                isDrawerOpen() -> homeViewModel.closeNavDrawer()
                else -> handleNewOnboardingBackPress()
            }
        }
    }

    private fun handleNewOnboardingBackPress() {
        if (onboardingStateMachine.isGoingBackAllowed()) {
            onboardingStateMachine.navigateBack()
        } else if (prefs.isOnboardingComplete().not() && isCurrentlyAtWebViewFragment().not()) {
            showCompleteOnboardingReminder()
        } else {
            if (navController.currentBackStackEntry?.destination?.id == R.id.homePagerFragment) {
                if (selectedTabPosition != 0) super.onBackPressed()
                else initializeDoubleBackPressToExit()
            } else super.onBackPressed()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        handleDeepLinkFromIntent()
    }

    override fun onDestroy() {
        if (!wasOnBoardingCompleted) {
            try {
                analyticsHandler.postEvent(
                    EventKey.Shown_homeScreen_onboardingFailed,
                    mapOf(
                        (newOnboardingViewModel.timeSpentMap.keys.toString()) to (newOnboardingViewModel.timeSpentMap.values.toString()),
                        EventKey.timeSpentOnboarding to newOnboardingViewModel.timeSpentMap.values.sumOf { it },
                        EventKey.noOfScreensShown to newOnboardingViewModel.numberOfOnBoardingScreens.toString(),

                        )
                )
            } catch (ex: Exception) {
                FirebaseCrashlytics.getInstance().log(ex.message ?: ex.localizedMessage.orEmpty())
            }
        }
        (this.applicationContext as JarApp).isAuthenticationRequestDone = false
        if (prefs.isLoggedIn()) prefs.setIsFirstSession(false)
        paymentManager.tearDown()
        mandatePaymentApi.teardown()
        weeklyChallengeCommonApi.tearDown()
        paymentListener = null
        binding.clBottomNav.clearAnimation()
        toast?.cancel()
        mandatePaymentJob = null
        toast = null
        appsFlyerLib.unregisterConversionListener()
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun isCurrentlyAtHomeFragment(): Boolean {
        return navController.currentBackStackEntry?.destination?.id == R.id.homePagerFragment
    }


    private fun isDrawerOpen() = binding.drawerLayout.isOpen

    private fun isCurrentlyAtWebViewFragment() =
        navController.currentBackStackEntry?.destination?.id == R.id.webViewFragment

    private fun handleDeepLink(
        deepLink: String?,
        shouldDelay: Boolean = true,
        fromScreen: String? = null,
        fromSection: String? = null,
        fromCard: String? = null,
        methodCallSource: String
    ) {
        /**
         * OneLink deep links would be handled by [appsFlyerDeepLinkListener]
         * */
        deepLink?.takeIf { it.isNotEmpty() }?.let {
            analyticsHandler.postEvent(
                EventKey.HANDLE_DEEPLINK,
                mapOf(
                    EventKey.Deeplink to it,
                    EventKey.FromScreen to fromScreen.orEmpty(),
                    EventKey.FromSection to fromSection.orEmpty(),
                    EventKey.FromCard to fromCard.orEmpty(),
                    EventKey.DefferedDeeplink to deferredDeepLinkValue.orEmpty(),
                    EventKey.IsLoggedIn to prefs.isLoggedIn(),
                    EventKey.IsDeeplinkHandlingPending to isDeeplinkHandlingPending,
                    EventKey.MethodCallSource to methodCallSource
                )
            )
        }
        if (
            deepLink?.contains("onelink").orFalse()
            || deepLink?.contains("click.myjar").orFalse()
            || deepLink?.contains("start.myjar").orFalse()
        ) return

        deepLinkJob?.cancel()
        deepLinkJob = uiScope.launch {
            try {
                if (shouldDelay) delay(1000)

                //Below navigation are secured.. it shouldn't be called without login
                if (prefs.isLoggedIn()) {
                    if (deepLink == deferredDeepLinkValue) {
                        isDeeplinkHandlingPending = false
                        deferredDeepLinkValue = null
                    }
                    deepLinkHandler.handleDeepLink(
                        deepLink = deepLink,
                        fromScreen = fromScreen,
                        fromSection = fromSection,
                        fromCard = fromCard
                    )
                } else {
                    //User Not Logged In Or not on home screen.... We Will Retry Again after login
                    deferredDeepLinkValue = deepLink
                    isDeeplinkHandlingPending = true
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun handleKnowMoreCtaClick(staticInfoData: StaticInfoData) {
        try {
            when (staticInfoData.getStaticInfoType()) {
                StaticInfoType.POPUP -> {
                    homeViewModel.fetchStaticPopupInfo(staticInfoData.value)
                }

                StaticInfoType.VIDEO -> {
                    coreUiApi.openExplanatoryVideoFragment(
                        explanatoryVideoData = ExplanatoryVideoData(
                            infographicUrl = staticInfoData.value,
                            deeplink = staticInfoData.deeplink
                        ),
                        onVideoStarted = {

                        },
                        onVideoEnded = {
                            staticInfoData.deeplink?.let {
                                handleDeepLink(it, methodCallSource = BaseConstants.HandleDeeplinkFlowSource.HANDLE_KNOW_MORE_CTA_VIDEO)
                            }
                        }
                    )
                }

                StaticInfoType.DEEPLINK -> {
                    handleDeepLink(staticInfoData.value, false, methodCallSource = BaseConstants.HandleDeeplinkFlowSource.HANDLE_KNOW_MORE_CTA_DEEPLINK)
                }

                StaticInfoType.IN_APP_HELP -> {
                    navigateTo(
                        navController, HomeNavigationDirections.actionToWebViewFragment(
                            shouldPostAnalyticsFromUrl = true,
                            url = remoteConfigApi.getHelpAndSupportUrl(prefs.getCurrentLanguageCode()),
                            title = "",
                            showToolbar = false,
                            flowType = BaseConstants.WebViewFlowType.IN_APP_HELP
                        )
                    )
                }

                StaticInfoType.EXTERNAL_URL -> {
                    openUrlInChromeTab(staticInfoData.value, title = "", showToolbar = true)
                }

                StaticInfoType.CUSTOM_WEB_VIEW -> {
                    navigateTo(
                        navController, HomeNavigationDirections.actionToWebViewFragment(
                            shouldPostAnalyticsFromUrl = true,
                            url = encodeUrl(staticInfoData.value),
                            title = "",
                            showToolbar = false,
                            flowType = BaseConstants.WebViewFlowType.STATIC_INFO
                        )
                    )
                }

                else -> {
                    //Ignore
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun handleDeepLinkFromIntent() {
//        al_applink_data : Deeplinks from FB
        var deepLink =
            (intent.extras?.get("al_applink_data") as? Bundle)?.getString("target_url")
                ?: intent.getStringExtra(BaseConstants.DEEPLINK_EXTRACTION_KEY)
        if (!deepLink.isNullOrBlank())
            handleDeepLink(deepLink.replace("jarapp://", "", true), methodCallSource = BaseConstants.HandleDeeplinkFlowSource.HANDLE_DEEPLINK_FROM_INTENT)
        else {
            intent.data?.let {
                deepLink = it.host + it.path
                val source = it.getQueryParameter("source")
                handleDeepLink(deepLink = deepLink.orEmpty(), fromScreen = source, methodCallSource = BaseConstants.HandleDeeplinkFlowSource.HANDLE_DEEPLINK_FROM_INTENT_DATA)
            }
        }
    }

    private fun updateDailySavingAndStatusScreenRedirection(
        finalMandateAmount: Float,
        authWorkflowType: String?,
        popUpToId: Int?
    ) {
        mandatePaymentJob =
            appScope.launch(dispatcherProvider.main) {
                dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                    mandateAmount = finalMandateAmount,
                    source = dailySavingFeatureFlow.orEmpty(),
                    authWorkflowType = authWorkflowType?.let {
                        MandateWorkflowType.valueOf(it)
                    } ?: MandateWorkflowType.PENNY_DROP,
                    newDailySavingAmount = newDailySavingAmount,
                    popUpToId = popUpToId,
                    userLifecycle = prefs.getUserLifeCycleForMandate()
                )
            }
    }

    private fun showCompleteOnboardingReminder() {
        analyticsHandler.postEvent(EventKey.BACK_BUTTON_PRESSED)
        val action = RedirectionNavigationDirections.actionToCompleteOnboardingReminderDialog()
        navigateTo(navController, action, false)
        analyticsHandler.postEvent(EventKey.Shown_BottomScreen_Exit_Onboarding)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onLogoutEvent(logoutEvent: LogoutEvent) {
        EventBus.getDefault().removeStickyEvent(logoutEvent)
        analyticsHandler.postEvent(
            event = EventKey.Logout_Event_Updated,
            values = mapOf(
                "version" to BuildConfig.VERSION_NAME,
                "flowContext" to logoutEvent.flowContext,
                "message" to logoutEvent.message.orEmpty()
            ),
            shouldPushOncePerSession = true
        )

        val message = logoutEvent.message
        if (!message.isNullOrBlank()) {
            showToast(message)
        }

        homeViewModel.logout()

        dismissProgressBar()
        //https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics#public-void-setuserid-string-id
        firebaseCrashlytics.setUserId("null")
        smsSyncApi.onUserLogout()
        //Done so that stories are not shown again. Creating app level prefs was overkill
        prefs.setNewOnboardingState(OnboardingStateMachine.State.SelectNumber.toString())
        analyticsHandler.onUserLogout()
        recreateApp()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenHealthInsuranceMemberSubmitFormEvent(event: OpenHealthInsuranceMemberSubmitFormEvent) {
        openUrlInChromeTab(event.url, title = "", showToolbar = true)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecreateAppEvent(recreateAppEvent: RecreateAppEvent) {
        recreateApp()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnusualActivityDetectedEvent(unusualActivityDetectedEvent: UnusualActivityDetectedEvent) {
        if (navController.currentDestination?.id != R.id.unusualActivityDetectedFragment) {
            navigateTo(
                navController,
                HomeNavigationDirections.actionToUnusualActivityDetectedFragment(),
                false
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiateDetectedRoundOffsPaymentEvent(initiateDetectedRoundOffsPaymentEvent: com.jar.app.feature_homepage.shared.domain.event.detected_spends.InitiateDetectedRoundOffsPaymentEvent) {
        paymentFlowSource = initiateDetectedRoundOffsPaymentEvent.source
        initiateDetectedRoundOffsPayment(initiateDetectedRoundOffsPaymentEvent.initiateDetectedRoundOffsPaymentRequest)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHandleExternalLinkEvent(externalLink: HandleExternalLinkEvent) {
        val externalLink = externalLink.url
        openUrlInChromeTab(
            externalLink, title = "", showToolbar = true
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiatePaymentEvent(initiatePaymentEvent: InitiatePaymentEvent) {
        showWeeklyChallengeAnim = initiatePaymentEvent.showWeeklyChallengeAnimation
        paymentFlowSource = initiatePaymentEvent.flowType ?: NONE
        buyGoldContext = initiatePaymentEvent.flowContextName ?: NONE
        initiatePayment(initiatePaymentResponse = initiatePaymentEvent.initiatePaymentResponse)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSmsPermissionGivenEvent(smsPermissionGivenEvent: SmsPermissionGivenEvent) {
        enableSmsProcessing()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenPaymentTransactionBreakupScreenEvent(openPaymentTransactionBreakupScreenEvent: OpenPaymentTransactionBreakupScreenEvent) {
        navigateTo(
            navController, HomeNavigationDirections.actionToPaymentTransactionBreakupFragment(
                orderId = openPaymentTransactionBreakupScreenEvent.orderId,
                type = openPaymentTransactionBreakupScreenEvent.type,
                title = openPaymentTransactionBreakupScreenEvent.title,
                description = openPaymentTransactionBreakupScreenEvent.description
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedirectToTransactionEvent(redirectToTransactionEvent: RedirectToTransactionEvent) {
        selectBottomTabItem(R.id.transactionFragment)
        EventBus.getDefault().post(
            com.jar.app.feature_transaction.impl.domain.event.TransactionFragmentPositionChangedEvent(
                when (redirectToTransactionEvent.transactionType) {
                    TransactionType.INVESTMENTS, TransactionType.WITHDRAWALS, TransactionType.NONE, TransactionType.GOLD_GIFT, TransactionType.GOLD -> BaseConstants.TransactionAdapterPosition.GOLD
                    TransactionType.PARTNERSHIPS, TransactionType.WINNINGS -> BaseConstants.TransactionAdapterPosition.WINNING
                }
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedirectToWeeklyChallengeEvent(redirectToWeeklyChallengeEvent: RedirectToWeeklyChallengeEvent) {
        weeklyChallengeApi.startWeeklyChallengeFlow(redirectToWeeklyChallengeEvent.fromScreen)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHandleDeepLinkEvent(handleDeepLinkEvent: HandleDeepLinkEvent) {
        handleDeepLink(
            deepLink = handleDeepLinkEvent.deepLink,
            shouldDelay = false,
            fromScreen = handleDeepLinkEvent.fromScreen,
            fromCard = handleDeepLinkEvent.fromCard,
            methodCallSource = BaseConstants.HandleDeeplinkFlowSource.DEEPLINK_EVENT_BUS
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHandleDeepLinkEvent(handleDirectRedirectionToDeeplinkFromHome: HandleDirectRedirectionToDeeplinkFromHome) {
        homeViewModel.featureRedirectionFlow.value.data?.data?.deeplink?.let {
            handleDeepLink(
                deepLink = it,
                fromScreen = BaseConstants.FromScreenFlows.DYNAMIC_FEATURE_REDIRECTION,
                methodCallSource = BaseConstants.HandleDeeplinkFlowSource.REDIRECTION_FROM_DEEPLINK_TO_HOME_EVENT_BUS
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHandleKnowMoreDeepLinkEvent(handleKnowMoreDeepLinkEvent: HandleKnowMoreDeepLinkEvent) {
        handleKnowMoreCtaClick(handleKnowMoreDeepLinkEvent.staticInfoData)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiateBuyGoldEvent(initiateBuyGoldEvent: InitiateBuyGoldEvent) {
        paymentFlowSource = initiateBuyGoldEvent.screenSource
        homeViewModel.buyGold(
            initiateBuyGoldEvent.amount,
            getCurrentPaymentGateway(),
            initiateBuyGoldEvent.screenSource
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOnboardingCompletedEvent(onboardingCompletedEvent: OnboardingCompletedEvent) {
        EventBus.getDefault().removeStickyEvent(onboardingCompletedEvent)
        getData()

        if (!homeViewModel.isReferralDeepLinkSynced && prefs.getAppsFlyerReferralUserId()
                .isNotEmpty()
        ) {
            updateAppsFlyerReferrerId(
                prefs.getAppsFlyerReferralUserId()
            )
            analyticsHandler.postEvent(
                EventKey.SESSION_OPEN_DEEP_LINK, mapOf(
                    EventKey.platform to BaseConstants.APPS_FLYER
                )
            )
        }
        analyticsHandler.postEvent(
            event = EventKey.Shown_homeScreen_onboardingCompleted, values = mapOf(
                EventKey.timeSpentOnboarding to newOnboardingViewModel.totalTimeSpentOnBoarding.toString(),
                EventKey.noOfScreensShown to newOnboardingViewModel.numberOfOnBoardingScreens.toString(),
                EventKey.onboardingScreens to newOnboardingViewModel.timeSpentMap.toString()
            )
        )
        wasOnBoardingCompleted = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateAppBarEvent(updateAppBarEvent: UpdateAppBarEvent) {
        updateAppBar(updateAppBarEvent.appBarData)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBottomNavItemChangedEvent(bottomNavItemChangedEvent: com.jar.app.feature_homepage.shared.domain.event.BottomNavItemChangedEvent) {
        selectedTabPosition = bottomNavItemChangedEvent.position
        val id = when (selectedTabPosition) {
            0 -> R.id.newHomeFragment
            1 -> R.id.transactionFragment
            2 -> R.id.accountFragment
            else -> -1
        }
        binding.bottomNavigationView.selectedItemId = id
        sendBottomNavAnalytics(id)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDailySavingEvent(refreshDailySavingEvent: RefreshDailySavingEvent) {
        navDrawerViewModel.fetchHamburgerMenuItems()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGoldSipEvent(refreshGoldSipEvent: RefreshGoldSipEvent) {
        navDrawerViewModel.fetchHamburgerMenuItems()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshHamburgerItemEvent(refreshHamburgerItemEvent: RefreshHamburgerItemEvent) {
        navDrawerViewModel.fetchHamburgerMenuItems()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenSavingsEvent(openSavingsFlowEvent: OpenSavingsFlowEvent) {
        uiScope.launch {
            delay(500)
            prefs.setUserLifeCycleForMandate(EventKey.UserLifecycles.Onboarding)
            when (openSavingsFlowEvent.savingsType) {
                ExperimentSavingType.Daily -> {
                    dailyInvestmentApi.openDailySavingsOnboarding(true)
                }

                ExperimentSavingType.Weekly -> {
                    goldSipApi.openGoldSipTypeSelectionScreen(
                        SipTypeSelectionScreenData(
                            getCustomString(com.jar.app.feature_gold_sip.shared.GoldSipMR.strings.feature_gold_sip_weekly_saving_plan),
                            false,
                            SipSubscriptionType.WEEKLY_SIP.name
                        )
                    )
                }

                ExperimentSavingType.Monthly -> {
                    goldSipApi.openGoldSipTypeSelectionScreen(
                        SipTypeSelectionScreenData(
                            getCustomString(com.jar.app.feature_gold_sip.shared.GoldSipMR.strings.feature_gold_sip_monthly_saving_plan),
                            false,
                            SipSubscriptionType.MONTHLY_SIP.name
                        )
                    )
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSetupAutoPayEventEvent(setupAutoPayEvent: SetupAutoPayEvent) {
        when {
            setupAutoPayEvent.shouldInvokePGDirectly -> {
                if (prefs.isFirstSession()) {
                    prefs.setIsFirstSession(false)
                }
                //Direct PG invocation is not supported yet..
            }

            setupAutoPayEvent.isDailySavingAutoPayFlow -> {
                setupDailySavingMandate(
                    amount = setupAutoPayEvent.newDailySavingAmount.orZero(),
                    flowName = setupAutoPayEvent.flowName.orEmpty(),
                    toolBarHeader = getString(com.jar.app.feature_daily_investment.R.string.feature_daily_savings),
                    toolbarHeaderIcon = com.jar.app.feature_daily_investment.R.drawable.feature_daily_investment_ic_daily_saving_tab,
                    title = getString(
                        com.jar.app.feature_daily_investment.R.string.daily_investment_lets_automate_your_rs_d,
                        setupAutoPayEvent.newDailySavingAmount.orZero().toInt()
                    ),
                    savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                    mandateAmount = setupAutoPayEvent.mandateAmount
                        ?: setupAutoPayEvent.newDailySavingAmount.orZero(),
                    authWorkFlowType = setupAutoPayEvent.authWorkFlowType
                )
            }

            setupAutoPayEvent.isRoundOffAutoPayFlow -> {
                roundOffApi.openRoundOffForAutoPaySetup(true)
            }
        }
    }

    private fun setupDailySavingMandate(
        amount: Float,
        flowName: String,
        toolBarHeader: String,
        toolbarHeaderIcon: Int,
        title: String,
        savingFrequency: String,
        mandateAmount: Float,
        authWorkFlowType: String?
    ) {
        popBackStack(navController, R.id.homePagerFragment, inclusive = false)
        appScope.launch(dispatcherProvider.main) {
            val paymentPageHeaderDetails = PaymentPageHeaderDetail(
                toolbarHeader = toolBarHeader,
                toolbarIcon = toolbarHeaderIcon,
                title = title,
                featureFlow = flowName,
                savingFrequency = savingFrequency,
                userLifecycle = null,
                mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION
            )
            val initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                mandateAmount = mandateAmount,
                authWorkflowType = if (authWorkFlowType != null) MandateWorkflowType.valueOf(
                    authWorkFlowType
                ) else if (flowName == MandatePaymentEventKey.FeatureFlows.SetupDailySaving) MandateWorkflowType.TRANSACTION else MandateWorkflowType.PENNY_DROP,
                subscriptionType = SavingsType.DAILY_SAVINGS.name,
            )
            val encodedPaymentPageHeader =
                encodeUrl(serializer.encodeToString(paymentPageHeaderDetails))
            val encodedMandateRequest =
                encodeUrl(serializer.encodeToString(initiateMandatePaymentRequest))
            val fragmentDeepLink =
                "android-app://com.jar.app/mandatePaymentPage/$encodedPaymentPageHeader/$encodedMandateRequest"

            popBackStack(navController, R.id.homePagerFragment, inclusive = false)
            lifecycleScope.launch(dispatcherProvider.main) {
                mandatePaymentApi.initiateMandatePaymentWithCustomUI(
                    customMandateUiFragmentId = com.jar.app.feature_mandate_payment.R.id.paymentPageFragment,
                    fragmentDeepLink = fragmentDeepLink,
                    paymentPageHeaderDetails = paymentPageHeaderDetails,
                    initiateMandatePaymentRequest = initiateMandatePaymentRequest
                ).collectUnwrapped(
                    onSuccess = {
                        popBackStack(navController, R.id.homePagerFragment, inclusive = false)
                        if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS)
                            homeViewModel.updateDailySaving(amount = amount)

                        dailyInvestmentApi.openDailySavingSetupStatusFragment(
                            dailySavingAmount = amount,
                            fetchAutoInvestStatusResponse = it.second,
                            mandatePaymentResultFromSDK = it.first,
                            flowName = flowName,
                            popUpToId = com.jar.app.feature_daily_investment.R.id.autoPayRedirectionFragment,
                            userLifecycle = prefs.getUserLifeCycleForMandate()
                        )

                    }, onError = { errorMessage, errorCode ->
                        if (errorCode == MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                            popBackStack(navController, R.id.homePagerFragment, inclusive = false)
                            dailyInvestmentApi.openDailySavingFlow(
                                fromSettingsFlow = false,
                                FeatureFlowData(
                                    fromScreen = flowName,
                                    fromSection = null,
                                    fromCard = null
                                )
                            )
                        } else if (errorMessage.isNotEmpty()) showToast(errorMessage)
                    })
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenSetupDailyInvestmentBottomSheetV2Event(onOpenSetupDailyInvestmentBottomSheetV2Event: OpenSetupDailyInvestmentBottomSheetV2Event) {
        uiScope.launch {
            if (onOpenSetupDailyInvestmentBottomSheetV2Event.featureFlow == BaseConstants.SinglePageHomeFeed) {
                navigateTo(
                    navController,
                    BaseConstants.InternalDeepLinks.SETUP_DAILY_INVESTMENT_BOTTOMSHEET_V2
                )
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenSetupBuyGoldV2BottomSheetEvent(onOpenSetupBuyGoldV2BottomSheetEvent: OpenSetupBuyGoldV2BottomSheetEvent) {
        if (onOpenSetupBuyGoldV2BottomSheetEvent.featureFlow == BaseConstants.SinglePageHomeFeed) {
            navigateTo(
                navController,
                BaseConstants.InternalDeepLinks.BUY_GOLD_V2_BOTTOMSHEET
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenUserGoldBreakdownScreenEvent(openUserGoldBreakdownScreenEvent: OpenUserGoldBreakdownScreenEvent) {
        navigateTo(
            navController, HomeNavigationDirections.actionToUserGoldBreakdownFragment(
                openUserGoldBreakdownScreenEvent.goldBalanceViewType.name
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenPartnerBannerListScreenEvent(openPartnerBannerListScreenEvent: OpenPartnerBannerListScreenEvent) {
        navigateTo(
            navController, HomeNavigationDirections.actionToFragmentPartnershipBonuses()
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCheckPendingDeepLinkEvent(checkPendingDeepLinkEvent: CheckPendingDeepLinkEvent) {
        if (isDeeplinkHandlingPending)
            handleDeepLink(deferredDeepLinkValue, methodCallSource = BaseConstants.HandleDeeplinkFlowSource.CHECK_PENDING_DEEPLINK_EVENT_BUS)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshRoundOffState(refreshRoundOffStateEvent: com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent) {
        homeViewModel.fetchUserMetaData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLendingOnboardingToKycEvent(lendingOnboardingToKycEvent: LendingOnboardingToKycEvent) {
        lendingOnboardingToKycEvent.progressResponse?.let {
            lendingKycApi.openLendingKycWithProgressResponse(
                flowType = lendingOnboardingToKycEvent.flowType, progressResponse = it
            )
        } ?: kotlin.run {
            lendingKycApi.openLendingKyc(flowType = lendingOnboardingToKycEvent.flowType,
                progressApiCallback = {})
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGoToHomeEvent(goToHomeEvent: GoToHomeEvent) {
        if (navController.isPresentInBackStack(R.id.homePagerFragment)) popBackStack(
            navController, R.id.homePagerFragment, false
        )
        else navigateTo(navController, BaseConstants.InternalDeepLinks.HOME)
        val destinationId = when (goToHomeEvent.bottomNavigationItem) {
            BaseConstants.HomeBottomNavigationScreen.HOME -> R.id.newHomeFragment
            BaseConstants.HomeBottomNavigationScreen.TRANSACTION -> R.id.transactionFragment
            BaseConstants.HomeBottomNavigationScreen.PROFILE -> R.id.accountFragment
            else -> R.id.newHomeFragment
        }
        selectBottomTabItem(destinationId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCancelGBSPayment(cancelGBSPayment: CancelGBSPayment) {
        navController.popBackStack()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLendingKycCompletedEvent(lendingKycCompletedEvent: LendingKycCompletedEvent) {
        popBackStack(navController, R.id.homePagerFragment, false)
        when (lendingKycCompletedEvent.type) {
            LendingRedirectionType.TYPE_REDIRECTION_LENDING_WEBVIEW -> {
                openUrlInChromeTab(
                    lendingKycCompletedEvent.microLoanDetailsUrl, title = "", showToolbar = true
                )
            }

            LendingRedirectionType.TYPE_REDIRECTION_LENDING_INAPP -> {
                openLendingOnboardingFlowFromHomeActivity(BaseConstants.LendingFlowType.KYC_DONE)
            }
        }
    }

    private fun openLendingOnboardingFlowFromHomeActivity(flowType: String) {
        if (navController.isFragmentInBackStack(com.jar.app.feature_lending.R.id.lendingEducationalIntroFragment)
                .not() && navController.isFragmentInBackStack(com.jar.app.feature_lending.R.id.lendingHostFragment)
                .not()
        ) {
            lendingApi.openLendingFlowV2(flowType = flowType, apiCallback = { _, isLoading ->
                if (isLoading) showProgressBar()
                else dismissProgressBar()
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun syncAppsFlyerUID(syncDeviceDetailsEvent: SyncDeviceDetailsEvent? = null) {
        if (!homeViewModel.isReferralDeepLinkSynced && prefs.getAppsFlyerReferralUserId()
                .isNotEmpty()
        ) {
            updateAppsFlyerReferrerId(
                prefs.getAppsFlyerReferralUserId()
            )
            analyticsHandler.postEvent(
                EventKey.SESSION_OPEN_DEEP_LINK, mapOf(
                    EventKey.platform to BaseConstants.APPS_FLYER
                )
            )
        }
        if (homeViewModel.isAppsFlyerIdSynced) return
        homeViewModel.updateDeviceDetails(
            appsFlyerId = appsFlyerLib.getAppsFlyerUID(
                applicationContext
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openBuyGoldEvent(openBuyGoldEvent: OpenBuyGoldEvent) {
        popBackStack(navController, R.id.homePagerFragment, false)
        buyGoldApi.openBuyGoldFlowWithWeeklyChallengeAmount(0f, openBuyGoldEvent.buyGoldFlowContext)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openGoldLeaseFlow(openGoldLeaseFlow: OpenGoldLeaseFlow) {
        goldLeaseApi.openGoldLeaseV2Flow(
            openGoldLeaseFlow.flowType,
            tabPosition = openGoldLeaseFlow.tabPosition
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateDSAutopayBankEvent(updateDSAutopayBankEvent: UpdateDSAutopayBankEvent) {
        if (updateDSAutopayBankEvent.isRoundOffEnabled) {
            handleDeepLink(BaseConstants.BASE_EXTERNAL_DEEPLINK + ExternalDeepLinks.PRE_DAILY_SAVING_AUTOPAY, methodCallSource = BaseConstants.HandleDeeplinkFlowSource.UPDATE_AUTOPAY_EVENT_BUS_ROUND_OFF_ENABLED)
        } else {
            handleDeepLink(BaseConstants.BASE_EXTERNAL_DEEPLINK + ExternalDeepLinks.UPDATE_DAILY_SAVING_MANDATE_SETUP + "/${updateDSAutopayBankEvent.newDSAmount.orZero()}/${updateDSAutopayBankEvent.mandateAmount.orZero()}", methodCallSource = BaseConstants.HandleDeeplinkFlowSource.UPDATE_AUTOPAY_EVENT_BUS_ROUND_OFF_NOT_ENABLED)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun askPushNotificationPermission(askPushNotificationPermissionEvent: AskPushNotificationPermissionEvent) {
        requestNotificationPermission()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshInAppStories(refereshInAppStory: RefereshInAppStory) {
        if (prefs.isLoggedIn() && remoteConfigApi.isShowInAppStory())
            homeViewModel.fetchInAppStory()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun getBottomNavViewForWalkthroughEvent(getBottomNavViewForWalkthroughEvent: GetBottomNavViewForWalkthroughEvent) {
        EventBus.getDefault().removeStickyEvent(getBottomNavViewForWalkthroughEvent)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Assuming you have the index of the tab you want to access (e.g., index 0 for the first tab)
        val tabId =
            bottomNavigationView.menu.getItem(getBottomNavViewForWalkthroughEvent.tab.position).itemId

        // Find the action view associated with the tab
        val tabView: View = bottomNavigationView.findViewById(tabId)
        EventBus.getDefault().postSticky(
            DrawBottomNavForAppWalkthrough(
                tabView,
                getBottomNavViewForWalkthroughEvent.title
            )
        )
    }

    private fun showToast(message: CharSequence) {
        toast?.cancel()
        toast = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun recreateApp() {
        //skip MissedYou screen while App recreation
        if (prefs.isJarShieldEnabled().not())
            (this.applicationContext as? JarApp)?.skipMissedYouScreen = true
        finishAffinity()
        startActivity(newIntent(this))
    }

    private fun checkIfAppUpdated() {
        if (prefs.getAccessToken().isNullOrEmpty()) return
        if (!prefs.getIsAppUpdated(BuildConfig.VERSION_CODE)) return
        prefs.setAppVersion(BuildConfig.VERSION_CODE)
    }

    fun getCurrentPaymentGateway() = paymentManager.getCurrentPaymentGateway()

    private fun sendEventAndRemoveListener(values: Map<String, String>) {
        analyticsHandler.postEvent(EventKey.AppsFlyer_Attribution_Details, values)
        appsFlyerLib.unregisterConversionListener()
        prefs.setSyncAppsFlyerAttributionData(false)
    }

    private fun update(appUpdateInfo: AppUpdateInfo) {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
            IMMEDIATE,
            // The current activity making the update request.
            this,
            // Include a request code to later monitor this update request.
            APP_UPDATE
        )
    }

    private fun requestNotificationPermission() {
        if (isAndroidSDK13()) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun openSingleHomeFeedCTA(flow: String) {
        dailyInvestmentApi.openSingleHomeFeedCTA(flow)
    }

    override fun openBuyGoldFlowWithCoupon(
        couponCode: String,
        couponType: String,
        isFromJackpotScreen: Boolean,
        buyGoldFlowContext: String
    ) {
        buyGoldApi.openBuyGoldFlowWithCoupon(
            couponCode = couponCode,
            couponType = couponType,
            isFromJackpotScreen = isFromJackpotScreen,
            buyGoldFlowContext = buyGoldFlowContext
        )
    }

    override fun openBuyGoldFlowWithPrefillAmount(
        prefillAmount: Float,
        buyGoldFlowContext: String
    ) {
        buyGoldApi.openBuyGoldFlowWithPrefillAmount(
            prefillAmount = prefillAmount,
            buyGoldFlowContext = buyGoldFlowContext
        )
    }

    override fun openBuyGoldFlowWithWeeklyChallengeAmount(
        amount: Float,
        buyGoldFlowContext: String
    ) {
        buyGoldApi.openBuyGoldFlowWithWeeklyChallengeAmount(
            amount = amount,
            buyGoldFlowContext = buyGoldFlowContext
        )
    }

    override fun openTransactionScreen(transactionType: String) {
        popBackStack(navController, R.id.homePagerFragment, false)
        uiScope.launch {
            delay(500)
            onRedirectToTransactionEvent(
                RedirectToTransactionEvent(
                    TransactionType.valueOf(
                        transactionType
                    )
                )
            )
        }
    }

    override fun openVibaWebView(
        shouldPostAnalyticsFromUrl: Boolean,
        url: String,
        title: String,
        showToolbar: Boolean
    ) {
        openUrlInChromeTab(url = url, title = title, showToolbar = showToolbar)
    }

    override fun openReferAndEarn() {
        navigateTo(
            navController,
            "android-app://com.jar.app/referAndEarn/"
        )
    }

    override fun openReferralFaqScreen() {
        navigateTo(
            navController,
            "android-app://com.jar.app/referralFaq"
        )
    }

    override fun shareReferralInvite() {
        homeViewModel.shareMessageDetails.value?.let {
            shareOnWhatsapp(
                deviceUtils.getWhatsappPackageName(),
                homeViewModel.shareMessageDetails.value.orEmpty()
            )
        } ?: run {
            getString(com.jar.app.feature_spin.R.string.failed_to_get_referral_link).snackBar(
                binding.root
            )
        }
    }

    override fun openJewelleryVoucherBrandCatalogueScreen() {
        goldRedemptionApi.openBrandCatalogueScreen()
    }

    override fun openJewelleryVoucherOpenMyOrdersScreen(tabType: String?) {
        goldRedemptionApi.openMyOrdersScreen(tabType)
    }

    override fun openJewelleryVoucherStatusScreen(voucherId: String?, orderType: String?) {
        goldRedemptionApi.openVoucherStatusScreen(voucherId, orderType)
    }

    override fun openJewelleryVoucherIntroScreen() {
        goldRedemptionApi.openIntroScreen(EventKey.HOME_SCREEN)
    }

    override fun openJewelleryVoucherPurchaseScreen(voucherId: String?) {
        if (voucherId.isNullOrBlank().not()) {
            goldRedemptionApi.openVoucherPurchaseScreen(voucherId!!)
        } else {
            goldRedemptionApi.openIntroScreen(EventKey.HOME_SCREEN)
        }
    }

    override fun openOldTransactionDetailScreen(
        orderId: String,
        txnId: String,
        sourceType: String
    ) {
        if (navController.currentBackStackEntry?.destination?.id != com.jar.app.feature_transaction.R.id.transactionDetailFragment) {
            navigateTo(
                navController,
                "android-app://com.jar.app/transactionDetail/$orderId/$txnId/${sourceType}"
            )
        }
    }

    override fun openNewTransactionDetailScreen(
        orderId: String,
        txnId: String,
        sourceType: String
    ) {
        if (navController.currentBackStackEntry?.destination?.id != com.jar.app.feature_transaction.R.id.newTransactionDetailsFragment) {
            navigateTo(
                navController,
                "android-app://com.jar.app/newTransactionDetail/$orderId/$txnId/$sourceType"
            )
        }
    }

    override fun openTransactionDetailBottomSheet(txnId: String) {
        if (navController.currentBackStackEntry?.destination?.id != com.jar.app.feature_transaction.R.id.transactionDetailFragment) {
            navigateTo(
                navController,
                "android-app://com.jar.app/transactionDetailBottomSheet/$txnId"
            )
        }
    }

    override fun openGoldDeliveryEntryScreen(productId: String?) {
        if (productId.isNullOrBlank().not()) {
            navigateTo(
                navController,
                "android-app://com.jar.app/goldCoinStore/$productId",

                )
        } else {
            navigateTo(
                navController,
                HomeNavigationDirections.actionToStoreItemDeliveryFragment()
            )
        }
    }

    override fun openGoldDeliveryCartScreen() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/goldCoinStoreCart/"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openDailySavingCancellationEntryScreen() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/dailySavingsCancellation"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openUpdateBankForDailySavingAutopay() {
        uiScope.launch {
            delay(3000)
            postSetupApi.updateDSAutopayBank()
        }
    }

    override fun updateDailySavingMandateSetup(
        newDailySavingAmount: Float,
        currentDailySavingAmount: String?,
        flowSource: String?
    ) {
        uiScope.launch {
            this@HomeActivity.newDailySavingAmount = newDailySavingAmount
            this@HomeActivity.dailySavingFeatureFlow =
                flowSource ?: MandatePaymentEventKey.FeatureFlows.UpdateDailySaving
            homeViewModel.isAutoPayResetRequired(newDailySavingAmount)
            if (flowSource.isNullOrEmpty()
                    .not() && flowSource == BaseConstants.DailySavingUpdateFlow.HOME
            )
                analyticsHandler.postEvent(
                    com.jar.app.feature_homepage.shared.util.EventKey.Clicked_UpdateDS_HomeScreen,
                    mapOf(
                        com.jar.app.feature_homepage.shared.util.EventKey.Selected_DS_amount to newDailySavingAmount,
                        com.jar.app.feature_homepage.shared.util.EventKey.Current_DS_amount to currentDailySavingAmount.orEmpty(),
                        com.jar.app.feature_homepage.shared.util.EventKey.Action to com.jar.app.feature_homepage.shared.util.EventKey.Save_Now_clicked
                    )
                )
        }
    }

    override fun openSpinsScreen() {
        if (navController.currentBackStackEntry?.destination?.id != com.jar.app.feature_spin.R.id.spinGameFragmentV2) {
            spinGameApi.openSpinFragmentV2(SpinsContextFlowType.SPINS, null)
        }
    }

    override fun openPromoCodeScreen() {
        navigateTo(
            navController, BaseConstants.InternalDeepLinks.PROMO_CODE
        )
    }

    override fun openGoldGiftingScreen() {
        giftingApi.openSendGiftScreen("DeepLink")
    }

    override fun openChangeLanguageScreen() {
        navigateTo(
            navController,
            HomeNavigationDirections.actionToChooseLanguageFragment(
                prefs.getCurrentLanguageCode(), "DeepLink"
            )
        )
    }

    override fun openSettingsPage() {
        selectBottomTabItem(R.id.accountFragment)
        EventBus.getDefault()
            .postSticky(AccountFragmentPositionChangedEvent(BaseConstants.AccountAdapterPosition.SETTINGS))
    }

    override fun openProfilePage() {
        selectBottomTabItem(R.id.accountFragment)
        EventBus.getDefault()
            .postSticky(AccountFragmentPositionChangedEvent(BaseConstants.AccountAdapterPosition.PROFILE))
    }

    override fun openAppHelpAndSupportPage() {
        val url =
            remoteConfigApi.getHelpAndSupportUrl(prefs.getCurrentLanguageCode())

        navigateTo(
            navController, HomeNavigationDirections.actionToWebViewFragment(
                shouldPostAnalyticsFromUrl = true,
                url = encodeUrl(url),
                title = "",
                showToolbar = false,
                flowType = BaseConstants.WebViewFlowType.IN_APP_HELP
            )
        )
    }

    override fun openInsuranceHelpAndSupportPage() {
        val url =
            "https://wiki.myjar.app/en/category/health-insurance/?event=Clicked_Topic_HelpHomePage&topic=Health%20Insurance"

        navigateTo(
            navController, HomeNavigationDirections.actionToWebViewFragment(
                shouldPostAnalyticsFromUrl = true,
                url = encodeUrl(url),
                title = "",
                showToolbar = false,
                flowType = BaseConstants.WebViewFlowType.INSURANCE_HELP
            )
        )
    }

    override fun openDailySavingSettings(
        fromScreen: String,
        fromSection: String?,
        fromCard: String?
    ) {
        if (navController.currentBackStackEntry?.destination?.id != com.jar.app.feature_daily_investment.R.id.editDailySavingsFragment) {
            dailyInvestmentApi.openDailySavingFlow(
                fromSettingsFlow = true,
                featureFlowData = FeatureFlowData(
                    fromScreen = fromScreen,
                    fromSection = fromSection,
                    fromCard = fromCard
                )
            )
        }
    }

    override fun openGoldPriceDetailScreen() {
        goldPriceAlertsApi.openIntroScreen(EventKey.HOME_SCREEN)
    }

    override fun openRoundOffScreen(fromScreen: String?) {
        if (navController.currentBackStackEntry?.destination?.id != com.jar.app.feature_round_off.R.id.roundOffDetailsFragment) {
            roundOffApi.openRoundOffDetails(fromScreen = fromScreen)
        }
    }

    override fun openSetupAutoPayScreenForRoundOffAndDailySaving() {
        roundOffApi.openRoundOffForAutoPaySetup(true)
    }

    override fun openRoundOffExplanation(fromScreen: String) {
        roundOffApi.openRoundOffForAutoPaySetup(fromScreen = fromScreen)
    }

    override fun openJarDuoScreen() {
        duoApi.openDuoFeature(
            fromScreen = DuoConstants.SOURCE_HOME,
            hasContactPermission = hasContactPermission()
        )
    }

    override fun openJarDuoOnboardingScreen() {
        duoApi.openDuoIntroStory(
            fromScreen = DuoConstants.SOURCE_HOME_CARD_INFO_BUTTON,
            pendingInvites = 0,
            duoGroups = 0,
            hasContactSynced = hasContactSynced
        )
    }

    override fun openOfferListScreen() {
        navigateTo(navController, "android-app://com.jar.app/offerListPage")
    }

    override fun openLendingKyc() {
        lendingKycApi.openLendingKyc(BaseConstants.LendingKycFromScreen.LENDING_CARD) { }
    }

    override fun openLendingOnboardingFlow(fromScreen: String) {
        openLendingOnboardingFlowFromHomeActivity(fromScreen)
    }

    override fun openRealTimeLendingFlow(fromScreen: String) {
        lendingApi.openRealTimeLendingFlow(fromScreen) { _, isLoading ->
            if (isLoading)
                showProgressBar()
            else dismissProgressBar()
        }
    }

    override fun openCheckCreditScoreFlow(fromScreen: String) {
        lendingApi.openCheckCreditReport(fromScreen)
    }

    override fun openGoldSipScreen() {
        goldSipApi.setupGoldSip()
    }

    override fun openGoldSipIntroScreen() {
        goldSipApi.openGoldSipIntro()
    }

    override fun openGoldSipTypeSelectionScreen(subscriptionType: String) {
        when (subscriptionType) {
            SipSubscriptionType.WEEKLY_SIP.name -> {
                goldSipApi.openGoldSipTypeSelectionScreen(
                    SipTypeSelectionScreenData(
                        toolbarHeader = getCustomString(com.jar.app.feature_gold_sip.shared.GoldSipMR.strings.feature_gold_sip_weekly_saving_plan),
                        shouldShowSelectionContainer = true,
                        sipSubscriptionType = SipSubscriptionType.WEEKLY_SIP.name
                    )
                )
            }

            SipSubscriptionType.MONTHLY_SIP.name -> {
                goldSipApi.openGoldSipTypeSelectionScreen(
                    SipTypeSelectionScreenData(
                        toolbarHeader = getCustomString(com.jar.app.feature_gold_sip.shared.GoldSipMR.strings.feature_gold_sip_monthly_saving_plan),
                        shouldShowSelectionContainer = true,
                        sipSubscriptionType = SipSubscriptionType.MONTHLY_SIP.name
                    )
                )
            }
        }
    }

    override fun openGoldSipDetailScreen() {
        goldSipApi.openGoldSipDetails()
    }

    override fun openHelpVideosListingScreen() {
        homePageApi.openHelpVideosListingScreen()
    }

    override fun openWeeklyMagicFlow(
        fromScreen: String,
        checkMysteryCardOrChallengeWin: Boolean
    ) {
        EventBus.getDefault().postSticky(
            OpenHomeScreenWeeklyMagicNotchFlow(
                fromScreen,
                checkMysteryCardOrChallengeWin
            )
        )
    }

    override fun openWithdrawalBottomSheet() {
        sellGoldApi.openWithdrawBottomSheet()
    }

    override fun openGoldLeaseFlow(flowType: String) {
        goldLeaseApi.openGoldLeaseV2Flow(flowType = flowType)
    }

    override fun openGoldLeaseFlowForNewUser(flowType: String, isNewUserLease: Boolean) {
        goldLeaseApi.openGoldLeaseV2Flow(
            flowType = flowType,
            isNewLeaseUser = isNewUserLease
        )
    }

    override fun openGoldLeaseMyOrdersScreen(flowType: String, tabPosition: Int) {
        goldLeaseApi.openGoldLeaseV2Flow(
            flowType = flowType,
            tabPosition = BaseConstants.GoldLeaseTabPosition.TAB_MY_ORDERS
        )
    }

    override fun openGoldLeasePlansScreen(flowType: String, isNewUserLease: Boolean) {
        goldLeaseApi.openGoldLeasePlans(
            flowType = flowType,
            isNewLeaseUser = isNewUserLease
        )
    }

    override fun openGoldLeaseUserLeaseDetailsScreen(flowType: String, leaseId: String) {
        goldLeaseApi.openGoldLeaseUserLeaseDetails(
            flowType = flowType,
            leaseId = leaseId
        )
    }

    override fun openGoldLeaseSummaryRetryFlow(
        flowType: String,
        leaseId: String,
        isNewUserLease: Boolean
    ) {
        goldLeaseApi.openGoldLeaseSummaryRetryFlow(
            flowType = flowType,
            leaseId = leaseId,
            isNewLeaseUser = isNewUserLease
        )
    }

    override fun openCustomWebView(
        shouldPostAnalyticsFromUrl: Boolean,
        url: String,
        title: String,
        showToolbar: Boolean,
        flowType: String
    ) {
        navigateTo(
            navController, HomeNavigationDirections.actionToWebViewFragment(
                shouldPostAnalyticsFromUrl = true,
                url = url,
                title = title,
                showToolbar = showToolbar,
                flowType = flowType
            )
        )
    }

    override fun openFirstCoinTransitionScreen() {
        homePageApi.openFirstCoinTransitionScreen()
    }

    override fun openFirstCoinProgressScreen() {
        homePageApi.openFirstCoinProgressScreen()
    }

    override fun openFirstCoinDeliveryScreen(orderId: String) {
        homePageApi.openFirstCoinDeliveryScreen(orderId)
    }

    override fun openPostSetupDetailScreen() {
        if (navController.isPresentInBackStack(com.jar.android.feature_post_setup.R.id.postSetupDetailsFragment)) {
            navController.popBackStack(R.id.homePagerFragment, false)
        }
        postSetupApi.openPostSetupDetails()
    }

    override fun openUpdateDailySavingScreen(flow: String) {
        dailyInvestmentApi.openUpdateDailySavingV2(flow)
    }

    override fun openHealthInsuranceLandingPage(fromScreen: String) {
        healthInsuranceApi.openHealthInsuranceLandingPage(fromScreen)
    }

    override fun openHealthInsurancePostPurchasePage(insuranceId: String) {
        healthInsuranceApi.openHealthInsurancePostPurchasePage(insuranceId)
    }

    override fun openHealthInsuranceAddDetailsPage() {
        healthInsuranceApi.openHealthInsuranceAddDetailsPage()
    }

    override fun openHealthInsuranceSelectPlanScreen(orderId: String) {
        healthInsuranceApi.openHealthInsuranceSelectPlanScreen(orderId)
    }

    override fun openHealthInsuranceManageScreen(orderId: String) {
        healthInsuranceApi.openHealthInsuranceManageScreen(orderId)
    }

    override fun openSpendsTracker() {
        spendTrackerApi.openSpendTrackerFlow()
    }

    override fun openPreDailySavingAutopay(flowType: String, dsAmount: Int) {
        dailyInvestmentApi.openPreDailySavingAutopay(
            flowType = flowType,
            dsAmount = dsAmount
        )
    }

    override fun openSellGoldScreen() {
        homeViewModel.fetchIfKycIsRequired()
        homeViewModel.fetchLendingProgress()
    }

    override fun openSurveyScreen() {
        homeViewModel.fetchSurvey(true)
    }

    override fun openKycScreen() {
        homeViewModel.isKycDeeplinkHandlingPending = true
        homeViewModel.fetchUserKycStatus()
    }

    override fun openDailySavingScreen(
        shouldOpenDSIntroBottomSheet: Boolean,
        fromAbandonFlow: Boolean,
        fromScreen: String,
        fromSection: String?,
        fromCard: String?
    ) {
        if (navController.currentBackStackEntry?.destination?.id != com.jar.app.feature_daily_investment.R.id.setupDailyInvestmentFragment) {
            dailyInvestmentApi.openDailySavingFlow(
                fromSettingsFlow = false,
                featureFlowData = FeatureFlowData(
                    fromScreen = fromScreen,
                    fromSection = fromSection,
                    fromCard = fromCard
                ),
                shouldOpenDSIntroBS = shouldOpenDSIntroBottomSheet,
                fromAbandonFlow = fromAbandonFlow
            )
        }
    }

    override fun openDailySavingOnboardingScreen() {
        analyticsHandler.postEvent(
            EventKey.Shown_FrequencySavingsStory_Onboarding,
            mapOf(
                EventKey.variants to onboardingStateMachine.customOnboardingData?.version.orEmpty()
            )
        )
        val infographicType = onboardingStateMachine.customOnboardingData?.infographicType?.let {
            InfographicType.valueOf(it)
        } ?: run { InfographicType.VIDEO }
        coreUiApi.openExplanatoryVideoFragment(
            explanatoryVideoData = ExplanatoryVideoData(
                infographicUrl = onboardingStateMachine.customOnboardingData?.infographicLink.orEmpty(),
                deeplink = onboardingStateMachine.customOnboardingData?.customOnboardingLink.orEmpty(),
                infographicType = infographicType,
                shouldNavigateToDeeplink = false,
                shouldShowBackButton = false,
                shouldShowReplayButton = false,
                shouldShowSkipButton = if (infographicType == InfographicType.VIDEO) true else remoteConfigApi.shouldShowSkipButtonOnDSCustomOnboardingLottie(),
                flow = BaseConstants.ONBOARDING
            ),
            onVideoStarted = {
                analyticsHandler.postEvent(
                    EventKey.Shown_FrequencySavingsStory_Onboarding,
                    mapOf(
                        EventKey.variants to onboardingStateMachine.customOnboardingData?.version.orEmpty(),
                        EventKey.state to EventKey.started,
                    ),
                    shouldPushOncePerSession = true
                )
            },
            onVideoEnded = { isSkipped ->
                analyticsHandler.postEvent(
                    EventKey.Shown_FrequencySavingsStory_Onboarding,
                    mapOf(
                        EventKey.variants to onboardingStateMachine.customOnboardingData?.version.orEmpty(),
                        EventKey.state to if (isSkipped) EventKey.skipped else EventKey.ended,
                    ),
                    shouldPushOncePerSession = true
                )
                onboardingComplete()
                dailyInvestmentApi.openDSCustomOnboardingFragment(
                    isFromOnboarding = true,
                    version = onboardingStateMachine.customOnboardingData?.version
                        ?: DailySavingConstants.DailySavingVariants.V4,
                    id = com.jar.app.core_ui.R.id.explanatoryVideoFragment,
                    fromScreen = BaseConstants.ONBOARDING
                )
            }
        )
    }

    override fun openDailySavingEducationScreen(isSetupFlow: Boolean) {
        if (navController.currentBackStackEntry?.destination?.id != com.jar.app.feature_daily_investment.R.id.dailySavingEducationFragment) {
            dailyInvestmentApi.openDailySavingEducation(isSetupFlow)
        }
    }

    override fun openGoalBasedSaving() {
        popBackStack(navController, R.id.homePagerFragment, inclusive = false)
        goalBasedSavingApi.openGoalBasedSaving()
    }

    override fun openGoalBasedSavingSettings() {
        goalBasedSavingApi.openGoalBasedSavingSettings()
    }

    override fun openQuestCouponDetails(fromScreen: String, brandCouponId: String) {
        binding.networkSnackbar.root.isVisible = false
        questsApi.openCouponDetails(fromScreen = fromScreen, brandCouponId)
    }

    override fun openQuestSplash(fromScreen: String) {
        binding.networkSnackbar.root.isVisible = false
        questsApi.openIntroScreen(fromScreen)
    }

    override fun openQuestDashboard(fromScreen: String) {
        binding.networkSnackbar.root.isVisible = false
        questsApi.openDashboard(fromScreen)
    }

    override fun openQuestAllRewards(fromScreen: String) {
        binding.networkSnackbar.root.isVisible = false
        questsApi.openRewardsScreen(fromScreen)
    }

    override fun openPromoCodeDialog() {
        promoCodeApi.openPromoCodeDialog()
    }

    override fun initiateOneTimePayment(amount: Float, paymentSource: String) {
        homeViewModel.buyGold(
            amount, paymentManager.getCurrentPaymentGateway(), paymentSource
        )
    }

    override fun openDailyInvestmentUpdateFragment() {
        dailyInvestmentApi.openDailyInvestmentUpdateFragment()
    }

    override fun openStoryByPageId(pageId: String) {
        storiesApi.openStoriesPage(pageId)
    }

    override fun openManualBuyGraph() {
        manualBuyGraphApi.openGraphManualBuy()
    }

    override fun openCalculator(fromScreen: String?, fromSection: String?) {
        calculatorApi.openCalculatorScreen(CalculatorType.EMI_CALCULATOR, fromScreen, fromSection)
    }

    override fun openExitSurvey(surveyFor: String) {
        exitSurveyApi.openExitSurvey(surveyFor)
    }

    override fun openGoldCalculator(
        fromScreen: String?,
        fromSection: String?
    ) {
        calculatorApi.openCalculatorScreen(CalculatorType.SAVINGS_CALCULATOR, fromScreen, fromSection)
    }

    private fun onboardingComplete() {
        if (!prefs.isOnboardingComplete()) {
            prefs.setOnboardingComplete()
            prefs.setNewOnboardingState(OnboardingStateMachine.State.Home.toString())
            val onboardingVariant = EventKey.NEW
            analyticsHandler.postEvent(
                EventKey.CompletedOnboarding,
                mapOf(
                    EventKey.VARIANT to onboardingVariant,
                    BaseConstants.TYPE to prefs.getAuthType()
                )
            )
        }
    }
}
