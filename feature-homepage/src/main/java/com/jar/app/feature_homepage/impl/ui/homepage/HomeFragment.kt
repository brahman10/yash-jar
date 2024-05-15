package com.jar.app.feature_homepage.impl.ui.homepage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyViewHolder
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.OnModelBuildFinishedListener
import com.app.feature_in_app_review.util.InAppReviewUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clevertap.android.sdk.CleverTapAPI
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.jar.app.base.data.event.*
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarHome
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.domain.model.card_library.StaticInfoType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.DynamicCardUtil
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.toJsonElement
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.dynamic_cards.base.CustomLinearCarousalModel
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2Api
import com.jar.app.feature_buy_gold_v2.shared.domain.event.InitiateBuyGoldEvent
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.api.event.HandleKnowMoreDeepLinkEvent
import com.jar.app.feature_homepage.api.event.OpenHomeScreenWeeklyMagicNotchFlow
import com.jar.app.feature_homepage.databinding.FeatureHomepageFragmentHomeBinding
import com.jar.app.feature_homepage.impl.domain.event.DrawBottomNavForAppWalkthrough
import com.jar.app.feature_homepage.impl.domain.event.GetBottomNavViewForWalkthroughEvent
import com.jar.app.feature_homepage.impl.domain.model.ScrollState
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.*
import com.jar.app.feature_homepage.impl.util.ExoplayerCachingUtil
import com.jar.app.feature_homepage.impl.util.HomeSpaceItemDecoration
import com.jar.app.feature_homepage.impl.util.ScrollStateProvider
import com.jar.app.feature_homepage.impl.util.showcase.WalkthroughManager
import com.jar.app.feature_homepage.shared.domain.model.AppWalkthroughSection
import com.jar.app.feature_homepage.shared.domain.model.FestivalCampaignData
import com.jar.app.feature_homepage.shared.domain.model.PreNotificationDismissalType
import com.jar.app.feature_homepage.shared.domain.model.RefreshFirstCoinEvent
import com.jar.app.feature_homepage.shared.domain.model.SectionType
import com.jar.app.feature_homepage.shared.util.EventKey
import com.jar.app.feature_homepage.shared.util.HomeConstants
import com.jar.app.feature_lending.api.LendingApi
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_one_time_payments.shared.data.model.ManualPaymentCompletedEvent
import com.jar.app.feature_payment.impl.domain.BackPressedOnPaymentPageEvent
import com.jar.app.feature_round_off.api.RoundOffApi
import com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent
import com.jar.app.feature_sell_gold.api.SellGoldApi
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.app.weekly_magic_common.api.WeeklyChallengeCommonApi
import com.jar.app.weekly_magic_common.impl.events.RedirectToWeeklyChallengeEvent
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.threeten.bp.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@RuntimePermissions
@AndroidEntryPoint
class HomeFragment : BaseFragment<FeatureHomepageFragmentHomeBinding>() {

    @Inject
    lateinit var lendingApi: LendingApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var cleverTapAPI: CleverTapAPI

    @Inject
    lateinit var buyGoldApi: BuyGoldV2Api

    @Inject
    lateinit var sellGoldApi: SellGoldApi

    @Inject
    lateinit var roundOffApi: RoundOffApi

    @Inject
    lateinit var weeklyChallengeCommonApi: WeeklyChallengeCommonApi

    @Inject
    lateinit var paymentApi: MandatePaymentApi

    @Inject
    lateinit var exoplayerCachingUtilRef: Lazy<ExoplayerCachingUtil>

    @Inject
    lateinit var inAppReviewUtil: InAppReviewUtil

    @Inject
    lateinit var scrollStateProvider: ScrollStateProvider

    private val viewModelProvider by viewModels<HomeFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var controller: HomeEpoxyController? = null

    private var layoutManager: LinearLayoutManager? = null

    private var verticalAnimationJob: Job? = null

    private val spaceItemDecoration = HomeSpaceItemDecoration(
        16.dp, 10.dp,
        escapeEdges = true,
        ignorePaddingViewHolders = listOf(
            CurrentGoldInvestmentCardEpoxyModel::class.java,
            HelpVideosCardEpoxyModel::class.java,
            ContentHeaderEpoxyModel::class.java,
            HeaderSectionEpoxyModel::class.java,
            CustomLinearCarousalModel::class.java,
            TypeOneEpoxyModel::class.java,
            TypeTwoEpoxyModel::class.java,
            SinglePageHomeFeedEpoxyModel::class.java,
            TypeThreeEpoxyModel::class.java,
            CustomLinearCarousalModel::class.java,
            VibaCardEpoxyModel::class.java,
        )
    )

    private var contextRef: WeakReference<Context>? = null

    private var timerJob: Job? = null

    private var scrollJob: Job? = null
    private var appWalkThroughScrollJob: Job? = null

    private val triggerWeeklyHomeFlow = AtomicBoolean(false)
    private var weeklyHomeFlowParams: Pair<String, Boolean>? = null

    private var exoPlayer: ExoPlayer? = null
    private var isFirstRun = true
    private var walkthroughIndex = 0
    private var isStepSkiped = false
    private var scrollState = RecyclerView.SCROLL_STATE_IDLE
    private var targetViewList = ArrayList<View>()

    private val walkthroughManager by lazy {
        WalkthroughManager(
            requireActivity(),
            onNextClicked = {
                ++walkthroughIndex
                showWalkthroughAndProceedToNextStep()
            }, onSkipClicked = { isManuallySkiped ->
                if (isManuallySkiped)
                    viewModel.postAppWalkthroughEvents(
                        viewModel.appWalkthroughSectionList[walkthroughIndex].getSectionType(),
                        EventKey.Skip
                    )
                dismissWalkthrough()
            },
            onCustomWalkThroughAnimationStart = { isFadeInAnimation, duration ->
                setStatusBarColorWithAnimation(
                    initialColorRes = if (isFadeInAnimation) com.jar.app.core_ui.R.color.bgColor else com.jar.app.core_ui.R.color.black_opacity_90,
                    finalColorRes = if (isFadeInAnimation) com.jar.app.core_ui.R.color.black_opacity_90 else com.jar.app.core_ui.R.color.bgColor,
                    animationTime = duration + 200
                )
            }
        )
    }
    private val onModelBuildFinishedListener = OnModelBuildFinishedListener {
        scrollJob?.cancel()
        scrollJob = uiScope.launch {
            delay(100)
            layoutManager?.onRestoreInstanceState(viewModelProvider.scrollState)
            if (viewModel.goldBalanceLiveData.value?.data?.data != null && viewModel.shouldScrollToTop) {
                binding.recyclerView.smoothScrollToPosition(0)
                viewModel.shouldScrollToTop = false
            }
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureHomepageFragmentHomeBinding
        get() = FeatureHomepageFragmentHomeBinding::inflate

    override fun setupAppBar() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(EventKey.Shown_HomeScreen)
        onboardingComplete()
        contextRef = WeakReference(context)
        setupUI()
        setupListeners()
        observeLiveData()
        EventBus.getDefault().post(CheckPendingDeepLinkEvent())
        cleverTapAPI.resumeInAppNotifications()
        setUserNameProperty()
        EventBus.getDefault().postSticky(OnboardingCompletedEvent())
    }

    private fun onboardingComplete() {
        if (!prefs.isOnboardingComplete()) {
            prefs.setOnboardingComplete()
            val onboardingVariant = com.jar.app.core_analytics.EventKey.NEW
            EventBus.getDefault().post(AskPushNotificationPermissionEvent())
            analyticsHandler.postEvent(
                com.jar.app.core_analytics.EventKey.CompletedOnboarding,
                mapOf(
                    com.jar.app.core_analytics.EventKey.VARIANT to onboardingVariant,
                    BaseConstants.TYPE to prefs.getAuthType()
                )
            )
        }
    }

    private fun setUserNameProperty() {
        if (prefs.isLoggedIn()) {
            val userString = prefs.getUserStringSync()
            if (userString.isNullOrBlank().not()) {
                val user = serializer.decodeFromString<User?>(userString!!)
                analyticsHandler.setUserProperty(
                    listOf(BaseConstants.FIRST_NAME to user?.firstName.orEmpty())
                )
            }
        }
    }

    private fun shouldShowScrollOverlay() {
        if (prefs.shouldShowHomeVerticalOverLay()) {
            uiScope.launch {
                verticalAnimationJob?.cancel()
                verticalAnimationJob = uiScope.countDownTimer(
                    totalMillis = 10000,
                    onFinished = {
                        if (layoutManager?.findFirstCompletelyVisibleItemPosition() == 0 && prefs.shouldShowHomeVerticalOverLay()) {
                            // Its at top
                            binding.clScrollMoreContainer.isVisible = true
                            binding.pullToScrollLottie.playAnimation()
                            prefs.setShowHomeVerticalOverLay(false)
                        }
                    }
                )
            }
        }
    }

    @SuppressLint("Range")
    private fun setupUI() {
        layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setItemSpacingPx(0)
        binding.recyclerView.addItemDecorationIfNoneAdded(spaceItemDecoration)
        val visibilityTracker = EpoxyVisibilityTracker()
        visibilityTracker.partialImpressionThresholdPercentage = 50
        visibilityTracker.attach(binding.recyclerView)
        controller = HomeEpoxyController(
            uiScope,
            festivalCampaignData = FestivalCampaignData(
                isFestivalCampaignEnabled = remoteConfigManager.isFestivalCampaignEnabled(),
                bannerImage = remoteConfigManager.getFestivalHomeScreenAsset(),
                lampImage = remoteConfigManager.getDiwaliHomeScreenLampAsset()

            ),
            onCardShown = { eventData ->
                if (viewModel.homeFragmentLaunchCount == 0) {
                    viewModel.homeFragmentLaunchCount += 1
                    EventBus.getDefault().post(
                        HomeFragmentOnCardShownEvent(
                            currentTime = System.currentTimeMillis(),
                        )
                    )
                }
                if (prefs.isAppWalkThroughBeingShownToUser().not()) {
                    analyticsHandler.postEvent(
                        EventKey.Shown_HomeScreenCard,
                        eventData.map,
                        shouldPushOncePerSession = true
                    )
                }
            },
            onVibaCardClick = { primaryActionData, eventData ->
                analyticsHandler.postEvent(
                    EventKey.Clicked_HomeScreenCard,
                    eventData.map
                )
                EventBus.getDefault()
                    .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.VIBA_WEB_VIEW + "/" + primaryActionData.value))
            },
            onShowMoreCtaClick = { eventData ->
                analyticsHandler.postEvent(
                    EventKey.Clicked_HomeScreenCard,
                    eventData.map
                )
            },
            onPrimaryCtaClick = { primaryActionData, eventData ->
                val flowScreen =
                    primaryActionData.featureType + "_" + primaryActionData.cardType + "_HomeScreen"
                analyticsHandler.postEvent(
                    EventKey.Clicked_HomeScreenCard,
                    eventData.map
                )
                prefs.setUserLifeCycleForMandate(flowScreen)
                if (
                    DynamicCardUtil.getCardTypeToUpdateUserInteraction()
                        .find { it == primaryActionData.cardType } != null
                ) {
                    uiScope.launch {
                        viewModel.updateUserInteraction(
                            primaryActionData.order,
                            primaryActionData.featureType
                        )
                        viewModel.fetchHomeFeedActions()
                    }
                    if (primaryActionData.cardType == DynamicCardType.HOMEFEED_TYPE_FIVE) {
                        // primaryActionData.data is 0.0F if the user is low intent
                        if (primaryActionData.data != (0.0F).toJsonElement()) {
                            viewModel.isAutoPayResetRequired(primaryActionData.data?.jsonPrimitive?.floatOrNull.orZero())
                        } else {
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent(
                                    deepLink = primaryActionData.value,
                                    fromScreen = flowScreen,
                                    fromSection = eventData.fromSection,
                                    fromCard = eventData.fromCard
                                )
                            )
                        }
                    }
                    if (primaryActionData.type == PrimaryActionType.DEEPLINK) {
                        EventBus.getDefault().post(
                            HandleDeepLinkEvent(
                                deepLink = primaryActionData.value,
                                fromScreen = flowScreen,
                                fromSection = eventData.fromSection,
                                fromCard = eventData.fromCard
                            )
                        )

                    } else if (primaryActionData.type == PrimaryActionType.IN_APP_BROWSER)
                        openUrlInChromeTab(
                            url = primaryActionData.value,
                            title = "",
                            showToolbar = true
                        )
                } else {
                    EventBus.getDefault().post(
                        HandleDeepLinkEvent(
                            deepLink = primaryActionData.value,
                            fromScreen = flowScreen,
                            fromSection = eventData.fromSection,
                            fromCard = eventData.fromCard
                        )
                    )
                }
            },
            onEndIconClick = { staticInfoData, eventData ->
                if (staticInfoData.getStaticInfoType() == StaticInfoType.CUSTOM_ACTION_DISMISS_SAVING) {
//                    viewModel.dismissSavingPopup(SavingsType.valueOf(staticInfoData.value))
                } else if (staticInfoData.getStaticInfoType().name == StaticInfoType.CUSTOM_ACTION_DISMISS_REFER_EARN.name) {
                    viewModel.updateUserInteraction(
                        staticInfoData.value.toIntOrNull() ?: 0,
                        eventData.map[DynamicCardEventKey.FeatureType] ?: ""
                    )
                    viewModel.fetchHomeFeedActions()
                    analyticsHandler.postEvent(
                        EventKey.Clicked_HomeScreenCard, mapOf<String, String>(
                            DynamicCardEventKey.CardTitle to eventData.map[DynamicCardEventKey.CardTitle].orEmpty(),
                            DynamicCardEventKey.CardDescription to eventData.map[DynamicCardEventKey.Footer].orEmpty(),
                            DynamicCardEventKey.FeatureType to eventData.map[DynamicCardEventKey.FeatureType].orEmpty(),
                        )
                    )
                } else {
                    if (staticInfoData.value.contains(BaseConstants.ExternalDeepLinks.LENDING_KYC_RESUME))
                        analyticsHandler.postEvent(EventKey.Clicked_Button_LoanCard)
                    EventBus.getDefault().post(HandleKnowMoreDeepLinkEvent(staticInfoData))
                }
//                if (eventData.featureType == BaseConstants.COUPON_CODE_DISCOVERY) {
//                    analyticsHandler.postEvent(
//                        EventKey.Clicked_CouponCard_homefeed,
//                        mapOf(
//                            DynamicCardEventKey.CardType to eventData.cardType,
//                            EventKey.FeatureType to eventData.featureType,
//                            EventKey.COUPON_CODE to eventData.data.toString()
//                        )
//                    )
//                } else
                analyticsHandler.postEvent(
                    EventKey.Clicked_EndIcon_HomeScreenCard,
                    eventData.map,
                )
            },
            onAmountClick = {
                val data = viewModel.detectedSpendInfoLiveData.value?.data?.data?.fullPaymentInfo
                if (viewModel.isManualPaymentLoading.not()) {
                    analyticsHandler.postEvent(EventKey.Clickedinfo_Investroundoff)
                    val event = OpenPaymentTransactionBreakupScreenEvent(
                        orderId = data?.orderId,
                        type = null,
                        title = requireContext().getString(
                            R.string.feature_homepage_rupee_x_in_double,
                            data?.txnAmt.orZero()
                        ),
                        description = data?.description.orEmpty()
                    )
                    EventBus.getDefault().post(event)
                }
            },
            onPromptInvestClick = { amount: Float, cardEventData: CardEventData ->
                EventBus.getDefault()
                    .post(
                        InitiateBuyGoldEvent(
                            amount,
                            BaseConstants.BuyGoldFlowContext.INVEST_PROMPT_HOMEFEED
                        )
                    )
                analyticsHandler.postEvent(
                    EventKey.Clicked_HomeScreenCard,
                    cardEventData.map,
                )
            },
            onDetectedSpendsInvestClick = { eventData ->
                val data = viewModel.detectedSpendInfoLiveData.value?.data?.data?.fullPaymentInfo
                if (viewModel.isManualPaymentLoading.not() && data?.txnAmt != null) {
                    val encoded =
                        encodeUrl(
                            serializer.encodeToString(
                                com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest(
                                    txnAmt = data.txnAmt,
                                    orderId = data.orderId
                                )
                            )
                        )
                    navigateTo("android-app://com.jar.app/detectedSpendStepsFragment/$encoded")
                }
                analyticsHandler.postEvent(
                    EventKey.Clicked_HomeScreenCard,
                    eventData.map,
                )
            },
            onPartPaymentClick = {
                if (viewModel.isManualPaymentLoading.not()) {
                    val encoded =
                        encodeUrl(
                            serializer.encodeToString(viewModel.detectedSpendInfoLiveData.value?.data?.data!!)
                        )
                    navigateTo(
                        "android-app://com.jar.app/partialPaymentFragment/$encoded"
                    )
                }
            },
            onGoldBreakdownClick = {
                EventBus.getDefault().post(OpenUserGoldBreakdownScreenEvent(it))
                analyticsHandler.postEvent(
                    EventKey.ClickedDashboardMoreinfo_HomeScreen, values = hashMapOf(
                        "FEATURE_EXPERIMENT_BALANCE_VIEW" to it.name
                    )
                )
            },
            onClaimPartnerBonusClick = {
                viewModel.claimBonus(it.orderId)
            },
            onShowAllPartnerBonusClick = {
                EventBus.getDefault().post(OpenPartnerBannerListScreenEvent())
            },
            onSliderMoved = {
                analyticsHandler.postEvent(
                    EventKey.Clicked_UpdateDS_HomeScreen, mapOf(
                        EventKey.Selected_DS_amount to it,
                        EventKey.Action to EventKey.Slider_moved
                    )
                )
            },
            onGoldSipManualPaymentClicked = { cardEventData, goldSipData ->
                EventBus.getDefault().post(
                    com.jar.app.feature_homepage.shared.domain.event.detected_spends.InitiateDetectedRoundOffsPaymentEvent(
                        com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest(
                            goldSipData.amount.toFloat(),
                            goldSipData.orderId.orEmpty(),
                            isPartial = false,
                        ),
                        source = BaseConstants.ManualPaymentFlowType.GoldSipManualPayment
                    )
                )
                analyticsHandler.postEvent(
                    EventKey.HomePage_Clicked_PayNowGoldSip, mapOf(
                        EventKey.Amount to goldSipData.amount,
                        EventKey.SipType to goldSipData.subscriptionType
                    )
                )
            },
            footerCtaClick = { preNotifyData, eventData ->
                analyticsHandler.postEvent(
                    EventKey.Clicked_HomeScreenCard,
                    eventData.map,
                )
                EventBus.getDefault()
                    .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.POST_SETUP_DETAILS))
            },
            neverShowAgainCtaClicked = { preNotifyData, eventData ->
                analyticsHandler.postEvent(EventKey.Clicked_HomeScreenCard, eventData.map)
                viewModel.dismissUpcomingPreNotification(
                    PreNotificationDismissalType.NEVER_SHOW_AGAIN,
                    preNotifyData.preNotifyAutopay.preNotificationIds
                )
            },
            dismissCtaClick = { preNotifyData, eventData ->
                analyticsHandler.postEvent(EventKey.Clicked_HomeScreenCard, eventData.map)
                viewModel.dismissUpcomingPreNotification(
                    PreNotificationDismissalType.DISMISS,
                    preNotifyData.preNotifyAutopay.preNotificationIds
                )
            },
            onFirstCoinClick = { deeplink, eventData ->
                analyticsHandler.postEvent(EventKey.Clicked_HomeScreenCard, eventData.map)
                navigateTo(deeplink)
            },
            hasContactPermission = requireContext().hasContactPermission(),
            onMagicHatNotchClick = {
                analyticsHandler.postEvent(
                    EventKey.Clicked_HomeScreenCard,
                    mapOf(DynamicCardEventKey.FeatureType to WeeklyMagicConstants.AnalyticsKeys.Values.Weekly_Magic)
                )
                startWeeklyHomeFlow(BaseConstants.BuyGoldFlowContext.HOME_SCREEN)
            },
            invokeNextChallenge = {
                viewModel.fetchWeeklyChallengeMetaData()
            },
            onFirstTransactionAnimationEnd = {
                analyticsHandler.postEvent(EventKey.Shown_CongratsFirstLocker)
                viewModel.updateLockerViewShown()
            }
        )

        binding.recyclerView.setControllerAndBuildModels(controller!!)

        controller?.addModelBuildListener(onModelBuildFinishedListener)

        uiScope.launch {
            delay(1) //Intentionally done..
            layoutManager?.onRestoreInstanceState(viewModelProvider.scrollState) //StateRestorationPolicy doesn't work with epoxy :(
        }

        if (remoteConfigManager.takeReadContactPermission() && !prefs.hasShownContactPermissionOnHomeScreen()) {
            refreshContactsWithPermissionCheck()
            prefs.setShownContactPermissionOnHomeScreen(true)
        }

        if (prefs.isAppWalkThroughShownToUser())
            shouldShowScrollOverlay()
    }

    private fun dismissWalkthrough() {
        walkthroughIndex = viewModel.appWalkthroughSectionList.size + 1
        walkthroughManager.dismissWalkthrough()
        prefs.setIsAppWalkThroughBeingShownToUser(false)
        scrollNestedScrollViewToTop()
        viewModel.updateAppWalkThroughCompleted()
    }

    private fun showWalkthroughAndProceedToNextStep() {
        uiScope.launch {
            if (walkthroughIndex < viewModel.appWalkthroughSectionList.size) {
                val section = viewModel.appWalkthroughSectionList[walkthroughIndex]
                val header = section.text
                val title = section.subText

                when (section.getSectionType()) {
                    SectionType.INTRO -> {
                        viewModel.postAppWalkthroughEvents(section.getSectionType(), EventKey.Next)
                        walkthroughManager.showWalkthroughWelcomeScreen(
                            header!!, title, section.footer!!
                        )
                    }

                    SectionType.LOCKER -> {
                        section.getFeatureType()?.forEachIndexed { index, featureType ->
                            binding.recyclerView.findViewWithTag<View>(featureType)?.let {
                                it.findViewById<View>(R.id.clLockerContainer)
                                    ?.let {
                                        showWalkthroughOverlay(
                                            section.getSectionType(), header, title, listOf(it)
                                        )
                                    }
                            }
                        }
                    }

                    SectionType.REDESIGN_LOCKER -> {
                        section.getFeatureType()?.forEachIndexed { index, featureType ->
                            binding.recyclerView.findViewWithTag<View>(featureType)?.let {
                                it.findViewById<View>(R.id.btnFirstTransactionAction)
                                    ?.let {
                                        showWalkthroughOverlay(
                                            section.getSectionType(), header, title, listOf(it)
                                        )
                                    }
                            }
                        }
                    }

                    SectionType.WITHDRAW_BUTTON -> {
                        section.getFeatureType()?.forEachIndexed { index, featureType ->
                            binding.recyclerView.findViewWithTag<View>(featureType)?.let {
                                it.findViewById<View>(R.id.btn1)
                                    ?.let {
                                        showWalkthroughOverlay(
                                            section.getSectionType(), header, title, listOf(it)
                                        )
                                    }
                            }
                        }
                    }

                    SectionType.SETUP_AUTOMATIC_SAVINGS -> {
                        showWalkthroughOverlay(
                            section.getSectionType(), header, title, updateTargetViewList(section)
                        )
                    }

                    SectionType.QUICK_ACTIONS -> {
                        showWalkthroughOverlay(
                            section.getSectionType(), header, title, updateTargetViewList(section)
                        )
                    }

                    SectionType.QUICK_ACTIONS_SINGLE_CARD -> {
                        showWalkthroughOverlay(
                            section.getSectionType(), header, title, updateTargetViewList(section)
                        )
                    }

                    SectionType.REWARDS -> {
                        showWalkthroughOverlay(
                            section.getSectionType(), header, title, updateTargetViewList(section)
                        )
                    }

                    SectionType.RECOMMENDED_FOR_YOU -> {
                        showWalkthroughOverlay(
                            section.getSectionType(), header, title, updateTargetViewList(section)
                        )
                    }

                    SectionType.DOCK -> {
                        EventBus.getDefault()
                            .postSticky(
                                GetBottomNavViewForWalkthroughEvent(section.getTab(), title)
                            )
                    }

                    SectionType.OUTRO -> {
                        viewModel.postAppWalkthroughEvents(section.getSectionType(), EventKey.Next)
                        walkthroughManager.showWalkthroughCompletedScreen(header!!, title)
                    }

                    SectionType.NONE -> {
                        dismissWalkthrough()
                    }
                }
            }
        }
    }

    private fun updateTargetViewList(section: AppWalkthroughSection): List<View> {
        val tempList = mutableListOf<View>()
        var shouldScroll = true
        binding.root.findViewById<NestedScrollView>(R.id.dynamicNestedScrollView)
            ?.let { nestedScrollView ->
                section.getFeatureType()?.forEachIndexed { index, featureType ->
                    binding.recyclerView.findViewWithTag<View>(featureType)?.let {
                        if (shouldScroll) {
                            shouldScroll = false
                            val y =
                                if (it.parent is Carousel) (it.parent as Carousel).y.toInt() else if (it.y.toInt() > 300) it.y.toInt() else 0

                            nestedScrollView.smoothScrollTo(
                                0,
                                y - 50.dpToPx(),
                                800
                            )
                        }
                        tempList.add(it)
                    }
                }
            }
        return tempList
    }

    private fun showWalkthroughOverlay(
        sectionType: SectionType,
        header: String?,
        title: String,
        targetViewList: List<View>,
        shouldHighlighBottomTab: Boolean = false
    ) {
        uiScope.launch {
            if (shouldHighlighBottomTab) {
                walkthroughManager.showWalkthroughForBottomTab(
                    title = title, targetView = targetViewList
                )
                viewModel.postAppWalkthroughEvents(sectionType, EventKey.Next)
            } else if (targetViewList.isEmpty() && walkthroughIndex > 0) {
                ++walkthroughIndex
                isStepSkiped = true
                showWalkthroughAndProceedToNextStep()
            } else {
                if (walkthroughIndex != 1)
                    delay(200)
                walkthroughManager.showWalkthrough(
                    shouldHideWelcomeScreen = walkthroughIndex == 1,
                    shouldShowSkipButton = false,
                    header = header,
                    title = title,
                    targetView = targetViewList
                )
                viewModel.postAppWalkthroughEvents(sectionType, EventKey.Next)
            }
        }
    }

    private val onScrollChangeListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                checkScrollState(
                    layoutManager?.findFirstVisibleItemPosition().orZero() > 0,
                    dy
                )
                if (dy > 0) {
                    // Scrolling up
                    if (binding.clScrollMoreContainer.isVisible)
                        binding.clScrollMoreContainer.isVisible = false
                    prefs.setShowHomeVerticalOverLay(false)
                } else {
                    // Scrolling down
                }
                val position = layoutManager?.findFirstCompletelyVisibleItemPosition()
                if (position != null) {
                    val temp = binding.recyclerView.getChildAt(position)
                    if (temp != null && (prefs.shouldAnimateTypeOneEpoxyModel() || prefs.shouldAnimateTypeTwoEpoxyModel())) {
                        val holder = binding.recyclerView.getChildViewHolder(temp)
                        if (holder != null && holder is EpoxyViewHolder) {
                            val model = holder.model
                            uiScope.launch {
                                if (model is CustomLinearCarousalModel) {
                                    when (model.models()[0]) {
                                        is TypeOneEpoxyModel -> {
                                            if (prefs.shouldAnimateTypeOneEpoxyModel() && model.models().size > 2) {
                                                model.scrollToDx(300)
                                                delay(1000)
                                                model.scrollToDx(-350)
                                                prefs.setShouldAnimateTypeOneEpoxyModel(false)
                                            }
                                        }

                                        is TypeTwoEpoxyModel -> {
                                            if (prefs.shouldAnimateTypeTwoEpoxyModel() && model.models().size > 2) {
                                                model.scrollToDx(300)
                                                delay(1000)
                                                model.scrollToDx(-350)
                                                prefs.setShouldAnimateTypeTwoEpoxyModel(false)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!controller?.cards.isNullOrEmpty()) {
                    // Show fab if we have crossed 9(including header items) items (basically quickActions)
                    if (layoutManager != null) {
                        if (layoutManager!!.findFirstVisibleItemPosition() > 1) {
                            TransitionManager.beginDelayedTransition(binding.btnFab)
                            binding.btnFab.visibility = View.VISIBLE
                        } else {
                            val transition = Fade()
                            transition.addTarget(binding.buyGoldFabText)
                            TransitionManager.beginDelayedTransition(binding.btnFab, transition)
                            binding.btnFab.visibility = View.INVISIBLE
                        }
                    }
                }

                val shouldEnabledSwipeRefresh = recyclerView.canScrollVertically(-1).not()
                        && viewModel.goldBalanceLiveData.value?.data?.data?.firstTransactionLockerDataObject?.showGoldBalanceAnimation.orFalse()
                    .not()
                binding.swipeRefresh.isEnabled = shouldEnabledSwipeRefresh
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollState = newState
                if (newState == RecyclerView.SCROLL_STATE_IDLE && controller?.cards?.size.orZero() > 1) {
                    viewModelProvider.scrollState = layoutManager?.onSaveInstanceState()
                }
            }
        }

    private fun setupListeners() {

        binding.recyclerView.addOnScrollListener(onScrollChangeListener)

        binding.swipeRefresh.setOnRefreshListener {
            if (viewModel.goldBalanceLiveData.value?.data?.data?.firstTransactionLockerDataObject?.showGoldBalanceAnimation.orFalse()
                    .not()
            )
                getData(true)
            EventBus.getDefault().post(RefereshInAppStory())

        }

        binding.recyclerView.setOnTouchListener { _, motionEvent ->
            val shouldEnableScroll = walkthroughIndex <= viewModel.appWalkthroughSectionList.size
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> binding.clScrollMoreContainer.isVisible = false
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> binding.clScrollMoreContainer.isVisible =
                    false
            }
            if (viewModel.appWalkthroughSectionList.isNotEmpty())
                shouldEnableScroll
            else
                viewModel.goldBalanceLiveData.value?.data?.data?.firstTransactionLockerDataObject?.showGoldBalanceAnimation.orFalse()
        }

        binding.clScrollMoreContainer.setDebounceClickListener {
            binding.clScrollMoreContainer.isVisible = false
        }

        binding.btnFab.setDebounceClickListener {
            if (prefs.isAppWalkThroughBeingShownToUser().not()) {
                buyGoldApi.openBuyGoldFlowWithWeeklyChallengeAmount(
                    0f,
                    BaseConstants.BuyGoldFlowContext.FLOATING_BUTTON
                )
                analyticsHandler.postEvent(EventKey.CLICKED_BUY_GOLD_HOME_FAB)
            }
        }
    }

    private fun checkScrollState(isScrolled: Boolean, dy: Int) {
        lifecycleScope.launch {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && (scrollState == RecyclerView.SCROLL_STATE_SETTLING || scrollState == RecyclerView.SCROLL_STATE_IDLE)) {
                val scrollState = ScrollState(scrolled = isScrolled, offsetY = dy)
                scrollStateProvider.updateScrollState(scrollState)
            }
        }
    }

    private fun startWeeklyHomeFlow(
        fromScreen: String,
        checkMysteryCardOrChallengeWin: Boolean = false
    ) {
        viewModel.weeklyChallengeMetaDataLiveData.value?.data?.data?.takeIf { !it.challengeId.isNullOrBlank() }
            ?.let { dataFromServer ->
                if (dataFromServer.userOnboarded == false) {
                    weeklyChallengeCommonApi.showWeeklyChallengeOnBoardingDialog(true, fromScreen)
                    findNavController().currentBackStackEntry?.savedStateHandle
                        ?.getLiveData<Boolean>(WeeklyMagicConstants.ON_BOARDING_ANIMATION_FINISHED)
                        ?.observe(this) {
                            uiScope.launch {
                                if (checkMysteryCardOrChallengeWin) {
                                    showMysteryCardOrChallengeWonFlow(dataFromServer, fromScreen)
                                } else {
                                    checkForPreviousChallengeStoryViewedStatus(
                                        dataFromServer,
                                        fromScreen
                                    )
                                }
                            }
                        }
                } else {
                    if (checkMysteryCardOrChallengeWin) {
                        showMysteryCardOrChallengeWonFlow(dataFromServer, fromScreen)
                    } else {
                        checkForPreviousChallengeStoryViewedStatus(dataFromServer, fromScreen)
                    }
                }
            } ?: kotlin.run {
            weeklyHomeFlowParams = Pair(fromScreen, checkMysteryCardOrChallengeWin)
            triggerWeeklyHomeFlow.set(true)
            viewModel.fetchWeeklyChallengeMetaData()
        }
    }

    private fun checkForPreviousChallengeStoryViewedStatus(
        dataFromServer: WeeklyChallengeMetaData,
        fromScreen: String
    ) {
        if (!dataFromServer.prevWeekChallengeId.isNullOrBlank()
            && dataFromServer.prevWeekStoryViewedStatus != true
        ) {
            weeklyChallengeCommonApi.showPreviousWeekChallengeStory(
                dataFromServer.prevWeekChallengeId!!,
                fromScreen
            )
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.getLiveData<Boolean>(WeeklyMagicConstants.ON_STORY_MODE_FINISHED)
                ?.observe(this) {
                    uiScope.launch {
                        showWeeklyHomeScreen(fromScreen)
                    }
                }
        } else {
            showWeeklyHomeScreen(fromScreen)
        }
    }

    private fun showMysteryCardOrChallengeWonFlow(
        dataFromServer: WeeklyChallengeMetaData,
        fromScreen: String
    ) {
        if (dataFromServer.showWinAnimation(
                prefs.getWonMysteryCardCount(),
                prefs.getWonMysteryCardChallengeId() ?: ""
            )
        ) {
            weeklyChallengeCommonApi.showMysteryCardOrChallengeWonScreen(
                dataFromServer.challengeId!!,
                false,
                fromScreen,
                true
            )
        } else {
            showWeeklyHomeScreen(fromScreen)
        }
    }

    private fun showWeeklyHomeScreen(fromScreen: String) {
        EventBus.getDefault().postSticky(RedirectToWeeklyChallengeEvent(fromScreen))
    }

    private fun getData(isRefreshed: Boolean = false) {
        if (prefs.isAppWalkThroughShownToUser()) {
            viewModel.fetchHomePageData(isRefreshed)
            EventBus.getDefault().post(HandleDirectRedirectionToDeeplinkFromHome())
        } else {
            prefs.setIsAppWalkThroughBeingShownToUser(false)
            viewModel.fetchAppWalkThroughData()
        }
        EventBus.getDefault().post(RefreshHamburgerItemEvent())
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.homeScreenPromptLiveData.collect(
                    onSuccess = {
                        it?.let { homeScreenPrompt ->
                            if (homeScreenPrompt.showBottomSheet.orFalse()) {
                                val encodedHomeScreenPromptData =
                                    encodeUrl(serializer.encodeToString(homeScreenPrompt))
                                navigateTo("android-app://com.jar.app/homeScreenPromptBottomSheetFragment/$encodedHomeScreenPromptData")
                            }
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.detectedSpendInfoLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            detectedSpendsData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.weeklyChallengeMetaDataLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            weeklyMagicHomeCard = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.promoCodeUserSettingsLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            promoCodeData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.buyGoldUserSettingsLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            buyGoldData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.goldBalanceLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            goldBalanceData = it
                        )
                        if (it.data?.firstTransactionLockerDataObject?.txnCount.orZero() > 1)
                            viewModel.updateLockerViewShown()
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.dailyInvestmentStatusLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            dailyInvestmentStatusData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.spendTrackerHomeCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            spendTrackerData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.dailSavingGoalHomeCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            dailySavingGoalData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.healthInsuranceHomeCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            healthInsuranceCardData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.giftingUserSettingsLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            giftingData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.lendingHomeCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            lendingHomeCard = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.healthInsuranceSingleCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            healthInsuranceSingleHomeCard = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.roundOffCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            roundOffCardData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.duoCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            duoCardData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.vasooliCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            vasooliCardData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.goldDeliveryUserSettingsLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            goldDeliverData = it
                        )
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.spinsMetaDataLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            spinsMetaData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.homePageExperimentsLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            homePageExperimentsData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.autoInvestPauseLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            autoInvestPauseData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.dailyInvestPauseLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            dailyInvestPauseData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userLendingKycProgressLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            userKycProgress = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.weeklyChallengeMetaDataLiveData.collect(
                    onSuccess = {
                        it?.let {
                            if (triggerWeeklyHomeFlow.get()) {
                                triggerWeeklyHomeFlow.set(false)
                                startWeeklyHomeFlow(
                                    weeklyHomeFlowParams?.first ?: "",
                                    weeklyHomeFlowParams?.second ?: false
                                )
                            }
                        }
                    },
                    onError = { it, _ ->
                        it.snackBar(binding.containerContent)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.roundOffPauseLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            roundOffPauseData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.partnerBannerLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            bannerData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userMetaLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            userMetaData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.couponCodesLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            couponCodeDiscoveryData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.vibaHorizontalCardFlowData.collectUnwrapped(
                        onSuccess = {
                            viewModel.mergeApiResponse(
                                vibaHorizontalCardData = it
                            )
                        }
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.helpVideosLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            helpVideosData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.bottomNavStickyCardDataFlow.collectUnwrapped(
                    onSuccess = { cardData ->

                        if (cardData.data != null) {

                            val contextRef = WeakReference(binding.root.context)

                            val glide = Glide.with(requireContext())

                            glide
                                .load(cardData.data?.cardMeta?.startIcon)
                                .into(binding.ivJarShield)

                            glide
                                .load(cardData.data?.cardMeta?.endIcon)
                                .into(binding.ivCross)

                            binding.clBottomNavStickyCard.visibility = View.VISIBLE

                            analyticsHandler.postEvent(
                                EventKey.Shown_Hc_Sticky_Event,
                                mapOf(
                                    EventKey.Feature_Type to cardData.data?.featureType.orEmpty(),
                                    EventKey.Cta_Text to cardData.data?.cardMeta?.cta?.text?.convertToString(
                                        contextRef
                                    ).toString()
                                )
                            )

                            binding.tvHeading.text =
                                (cardData?.data?.cardMeta?.title)?.convertToString(contextRef)

                            binding.tvSubHeader.text =
                                (cardData?.data?.cardMeta?.description)?.convertToString(contextRef)

                            binding.tvCtaHeading.text =
                                (cardData?.data?.cardMeta?.cta?.text)?.convertToString(contextRef)

                            binding.clCta.setOnClickListener {
                                analyticsHandler.postEvent(
                                    EventKey.Clicked_HC_Sticky,
                                    mapOf(
                                        EventKey.Feature_Type to cardData.data?.featureType.orEmpty(),
                                        EventKey.Cta_Text to cardData.data?.cardMeta?.cta?.text?.convertToString(
                                            contextRef
                                        ).toString(),
                                        EventKey.Button_Type to EventKey.Resume
                                    )
                                )

                                cardData.data?.cardMeta?.cta?.deepLink?.let { it1 ->
                                    EventBus.getDefault().post(HandleDeepLinkEvent(deepLink = it1))
                                }
                            }

                            binding.ivCross.setOnClickListener {

                                analyticsHandler.postEvent(
                                    EventKey.Clicked_HC_Sticky,
                                    mapOf(
                                        EventKey.Feature_Type to cardData.data?.featureType.orEmpty(),
                                        EventKey.Button_Type to EventKey.Cancel,
                                        EventKey.Cta_Text to cardData.data?.cardMeta?.cta?.text?.convertToString(
                                            contextRef
                                        ).toString()
                                    )
                                )

                                binding.clBottomNavStickyCard.visibility = View.GONE
                                viewModel.updateUserInteraction(
                                    cardData?.data?.order.orZero(),
                                    cardData?.data?.featureType.orEmpty()
                                )
                            }
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.listLiveData.collectLatest {
                    controller?.cards = it
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchUpdateDailySavingAmountLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            updateDailySavingInfo = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userGoldSipDetailsLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            userGoldSipDetails = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.goldLeaseHomeCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            goldLeaseHomeCardData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.duoContactMetaDataLiveData.collect {
                    viewModel.mergeApiResponse(
                        duoContactsMetaData = it
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchInAppReviewStatusLiveData.collect(
                    onSuccess = {
                        if (it.showRatingScreen) {
                            inAppReviewUtil.showInAppReview(requireActivity())
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchPreNotificationLiveData.collectUnwrapped(
                    onSuccess = {
                        uiScope.launch {
                            delay(1000)
                            dismissProgressBar()
                            viewModel.mergeApiResponse(
                                preNotifyAutopay = it
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.dismissPreNotificationLiveData.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        viewModel.fetchUpcomingPreNotification()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchQuickActionButtonsLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            quickActionButtonResponse = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchFirstCoinHomeScreenDataLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            firstCoinHomeScreenData = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchDailySavingsCardLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            dailySavingCardData = it.data
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.updateUserInteractionLiveData.collect(
                    onSuccess = {}
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.claimedBonusLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        context?.showToast(getString(R.string.feature_homapage_claimed))
                        viewModel.fetchPartnerBanners()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        context?.showToast(getString(R.string.feature_homapage_claimed))
                        viewModel.fetchPartnerBanners()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.hasVideoLiveData.collectLatest {
                    if (exoPlayer == null && it == true) {
                        val cacheDataSourceFactory =
                            exoplayerCachingUtilRef.get().mCacheDataSourceFactory
                        exoPlayer = ExoPlayer.Builder(requireContext())
                            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
                            .build()
                        controller?.exoPlayer = exoPlayer
                        controller?.cacheDataSourceFactory = cacheDataSourceFactory
                        controller?.scope = uiScope
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.autoInvestedGoldData.collect(
                    onSuccess = {
                        if (viewModel.isAutopayInfoShown.not() && it?.header != null) {
                            viewModel.isAutopayInfoShown = true
                            val postPaymentRewardsString = encodeUrl(serializer.encodeToString(it))
                            navigateTo("android-app://com.jar.app/AutoSaveNotificationFragment/$postPaymentRewardsString")
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchHomeFeedActionsUseCaseLiveData.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            homeFeedActions = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.homeFeedImageLiveData.collect(
                    onSuccess = { data ->
                        uiScope.launch {
                            //Done intentionally
                            if (isFirstRun)
                                delay(2000)
                            isFirstRun = false
                            val glide = Glide.with(requireContext())
                            data.forEach {
                                it.value?.let { url ->
                                    glide.load(url)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(
                                            if (it.key == HomeConstants.HomeFeedPosition.bottomRight)
                                                binding.ivBottomRight
                                            else
                                                binding.ivTopLeft
                                        )
                                }
                            }
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isAutoPayResetRequiredFlow.collect(
                    onSuccess = {
                        if (it.isResetRequired) {
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent(
                                    BaseConstants.BASE_EXTERNAL_DEEPLINK
                                            + BaseConstants.ExternalDeepLinks.UPDATE_DAILY_SAVING_MANDATE_SETUP
                                            + "/${viewModel.newDsAmount}/${
                                        it.getFinalMandateAmount().orZero()
                                    }"
                                            + "/${MandatePaymentEventKey.FeatureFlows.DailySavingAbandon}"
                                )
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateDailySavingCardFlow.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergeApiResponse(
                            updateDailySavingData = it
                        )
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchAppWalkThroughFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        it?.appWalkthroughSections?.let {
                            prefs.setIsAppWalkThroughBeingShownToUser(true)
                            addNestedScrollView()
                            delay(500)
                            dismissProgressBar()
                            showWalkthroughAndProceedToNextStep()
                        } ?: kotlin.run {
                            dismissProgressBar()
                            viewModel.fetchHomePageData()
                        }
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        viewModel.fetchHomePageData()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateAppWalkThroughCompletedFlow.collect(
                    onSuccess = {}
                )
            }
        }
    }

    private fun addNestedScrollView() {
        val recyclerView = binding.recyclerView

        // Wrap the RecyclerView inside a NestedScrollView
        val nestedScrollView = NestedScrollView(requireContext())
        val layoutParams = recyclerView.layoutParams
        nestedScrollView.layoutParams = layoutParams
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.itemAnimator = null

        // Remove the RecyclerViewPlaceholder parent
        recyclerView.parent?.let { parent ->
            if (parent is ViewGroup) {
                parent.removeView(recyclerView)
            }
        }

        recyclerView.layoutParams.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        // Add the RecyclerView to the NestedScrollView
        nestedScrollView.addView(binding.recyclerView)

        // Find the parent layout and add the NestedScrollView
        val parentLayout = binding.containerContent
        nestedScrollView.id = R.id.dynamicNestedScrollView
        binding.recyclerView.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        parentLayout.addView(nestedScrollView)
        setNestedScrollViewScrollListener()
        viewModel.fetchHomePageData()
        prefs.setAppWalkThroughShownToUser(true)
    }

    private fun setNestedScrollViewScrollListener() {
        binding.recyclerView.removeOnScrollListener(onScrollChangeListener)
        binding.root.findViewById<NestedScrollView>(R.id.dynamicNestedScrollView)?.let {
            it.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                // ScrollStateProvider For ScrollView
                lifecycleScope.launch {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && scrollState != MotionEvent.ACTION_MOVE) {
                        val scrolled = scrollY > 100
                        scrollStateProvider.updateScrollState(
                            ScrollState(scrolled = scrolled, offsetY = scrollY)
                        )
                    }
                }

                if (!controller?.cards.isNullOrEmpty() && prefs.isAppWalkThroughBeingShownToUser()
                        .not()
                ) {
                    // Show fab if we have scrolled a bit
                    if (layoutManager != null && prefs.isAppWalkThroughShownToUser()) {
                        if (scrollY > 500) {
                            TransitionManager.beginDelayedTransition(binding.btnFab)
                            binding.btnFab.visibility = View.VISIBLE
                        } else {
                            val transition = Fade()
                            transition.addTarget(binding.buyGoldFabText)
                            TransitionManager.beginDelayedTransition(binding.btnFab, transition)
                            binding.btnFab.visibility = View.INVISIBLE
                        }
                    }
                }
            })

            it.setOnTouchListener { view, motionEvent ->
                val shouldEnableScroll =
                    walkthroughIndex <= viewModel.appWalkthroughSectionList.size
                scrollState = motionEvent.action
                shouldEnableScroll
            }

        }
    }

    private fun scrollNestedScrollViewToTop() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
            binding.root.findViewById<NestedScrollView>(R.id.dynamicNestedScrollView)
                ?.smoothScrollTo(0, 0, 500)
    }

    private fun checkAndCancelTimerJob() {
        if (timerJob?.isActive == true) {
            timerJob?.cancel()
            timerJob = null
        }
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun refreshContacts() {
        analyticsHandler.postEvent(EventKey.Contact_Refreshed)
        viewModelProvider.fetchLocalContactsAndUploadToServer()
    }

    override fun onDestroyView() {
        viewModelProvider.scrollState = layoutManager?.onSaveInstanceState()
        controller = null
        binding.recyclerView.adapter = null
        contextRef = null
        layoutManager = null
        binding.recyclerView.layoutManager = null
        verticalAnimationJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarHome(
                        showGoldPrice = true,
                        showStoryIcon = true,
                        showNotification = true
                    )
                )
            )
        )
        checkScrollState(
            layoutManager?.findFirstVisibleItemPosition().orZero() > 0,
            binding.recyclerView.scrollY
        )
        exoPlayer?.play()
        if (viewModel.appWalkthroughSectionList.isEmpty())
            setStatusBarColor(com.jar.app.core_ui.R.color.bgColor)
    }

    override fun onPause() {
        exoPlayer?.pause()
        checkAndCancelTimerJob()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onManualPaymentStepsShownEvent(manualPaymentStepsShownEvent: com.jar.app.feature_homepage.shared.domain.event.detected_spends.ManualPaymentStepsShownEvent) {
        EventBus.getDefault().removeStickyEvent(manualPaymentStepsShownEvent)
        EventBus.getDefault()
            .postSticky(
                com.jar.app.feature_homepage.shared.domain.event.detected_spends.InitiateDetectedRoundOffsPaymentEvent(
                    manualPaymentStepsShownEvent.initiateDetectedRoundOffsPaymentRequest
                )
            )
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onManualPaymentCompletedEvent(manualPaymentCompletedEvent: ManualPaymentCompletedEvent) {
        EventBus.getDefault().removeStickyEvent(manualPaymentCompletedEvent)
        viewModel.fetchDetectedSpendsPaymentInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDailySavingEvent(refreshDailySavingEvent: RefreshDailySavingEvent) {
        if (view != null && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
            binding.recyclerView.scrollToPosition(0)
        viewModel.fetchHomeFeedActions()
        viewModel.fetchDailyInvestUserSettings()
        viewModel.fetchDailySavingUpdateJourney()
        viewModel.fetchDailySavingStatus()
        viewModel.fetchDetectedSpendsPaymentInfo()
        viewModel.fetchUpdateDailySavingAmountInfo()
        viewModel.fetchUserGoldBalance()
        viewModel.fetchFirstCoinGamificationData()
        viewModel.fetchDailySavingGoalHomeCard()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNavItemReselectedEvent(navItemReselectedEvent: NavItemReselectedEvent) {
        if (navItemReselectedEvent.itemId == R.id.newHomeFragment)
            try {
                binding.recyclerView.smoothScrollToPosition(0)
            } catch (ex: Exception) {
                //TODO: Need To check why this event is getting invoked unnecessarily
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onRefreshPromoCodeEvent(refreshPromoCodeEvent: RefreshPromoCodeEvent) {
        EventBus.getDefault().removeStickyEvent(refreshPromoCodeEvent)
        viewModel.fetchPromoCodeUserSettings()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshSpinMetaDataEvent(refreshSpinMetaDataEvent: RefreshSpinMetaDataEvent) {
        viewModel.fetchSpinsMetaData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenHomeScreenWeeklyMagicNotchFlow(openHomeScreenWeeklyMagicNotchFlow: OpenHomeScreenWeeklyMagicNotchFlow) {
        startWeeklyHomeFlow(
            openHomeScreenWeeklyMagicNotchFlow.fromScreen,
            openHomeScreenWeeklyMagicNotchFlow.checkMysteryCardOrChallengeWin
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onRefreshUserGoldBalanceEvent(refreshUserGoldBalanceEvent: RefreshUserGoldBalanceEvent) {
        EventBus.getDefault().removeStickyEvent(refreshUserGoldBalanceEvent)
        if (view != null && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
            binding.recyclerView.scrollToPosition(0)
        viewModel.fetchUserGoldBalance()
        viewModel.fetchFirstCoinGamificationData()
        viewModel.fetchHomeFeedActions()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onRefreshWeeklyChallengeMetaEvent(refreshWeeklyChallengeMetaEvent: RefreshWeeklyChallengeMetaEvent) {
        EventBus.getDefault().removeStickyEvent(refreshWeeklyChallengeMetaEvent)
        viewModel.fetchWeeklyChallengeMetaData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshPartnerBonusEvent(refreshPartnerBonusEvent: RefreshPartnerBonusEvent) {
        viewModel.fetchPartnerBanners()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshRoundOffState(refreshRoundOffStateEvent: RefreshRoundOffStateEvent) {
        viewModel.fetchRoundOffUserSettings()
        viewModel.fetchRoundOffCard()
        viewModel.fetchHomeFeedActions()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUserMetaEvent(refreshUserMetaEvent: RefreshUserMetaEvent) {
        viewModel.fetchUserMeta()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshHomeFeedEvent(refreshHomeFeedEvent: RefreshHomeFeedEvent) {
        viewModel.fetchHomeFeedActions()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLendingKycCompletedEvent(lendingKycCompletedEvent: LendingKycCompletedEvent) {
        viewModel.fetchUserLendingKycProgress()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubmitExitSurvey(submittedExitSurvey: SubmittedExitSurveyEvent) {
        findNavController().popBackStack()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshCouponDiscoverEvent(refreshCouponDiscoverEvent: RefreshCouponDiscoverEvent) {
        viewModel.fetchCouponCodes()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshVibaHorizontalCardEvent(refreshVibaHorizontalCardEvent: RefreshVibaHorizontalCardEvent) {
        viewModel.fetchVibaHorizontalCards()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGoldSipEvent(refreshGoldSipEvent: RefreshGoldSipEvent) {
        viewModel.fetchUserGoldSipDetails()
        viewModel.fetchFirstCoinGamificationData()
        viewModel.fetchHomeFeedActions()
//        viewModel.fetchUserGoldBalance()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshLendingEvent(refreshLendingEvent: RefreshLendingEvent) {
        viewModel.fetchLendingHomeCard()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGoldLeaseHomeCardEvent(refreshGoldLeaseHomeCardEvent: RefreshGoldLeaseHomeCardEvent) {
        viewModel.fetchGoldLeaseHomeCard()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGoldLeaseHomeCardEvent(refreshFirstCoinEvent: RefreshFirstCoinEvent) {
        viewModel.fetchFirstCoinGamificationData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDuoHomeCardEvent(refreshDuoHomeCardEvent: RefreshDuoHomeCardEvent) {
        viewModel.fetchDuoData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateGoalBasedSavingCard(updateGoalBasedSavingCard: UpdateGoalBasedSavingCard) {
        viewModel.fetchDailySavingsCardData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun drawBottomNavForAppWalkthrough(drawBottomNavForAppWalkthrough: DrawBottomNavForAppWalkthrough) {
        EventBus.getDefault().removeStickyEvent(drawBottomNavForAppWalkthrough)
        showWalkthroughOverlay(
            header = null,
            title = drawBottomNavForAppWalkthrough.title,
            targetViewList = listOf(drawBottomNavForAppWalkthrough.view),
            shouldHighlighBottomTab = true,
            sectionType = viewModel.appWalkthroughSectionList[walkthroughIndex].getSectionType()
        )
    }
}
