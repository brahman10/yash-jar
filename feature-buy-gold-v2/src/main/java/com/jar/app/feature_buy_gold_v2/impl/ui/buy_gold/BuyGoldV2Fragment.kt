package com.jar.app.feature_buy_gold_v2.impl.ui.buy_gold

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshCouponDiscoverEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.getAppNameFromPkgName
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.getFormattedTextForOneIntegerValue
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.base.util.secondsToMillis
import com.jar.app.base.util.setHtmlText
import com.jar.app.base.util.volumeToStringWithoutTrailingZeros
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.BuyGoldFlowContext.FLOATING_BUTTON
import com.jar.app.core_base.util.BaseConstants.BuyGoldFlowContext.HAMBURGER_MENU
import com.jar.app.core_base.util.BaseConstants.EXIT_SURVEY_DEEPLINK
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundUp
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.getHtmlTextValue
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.extension.toast
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.widget.PriceValidityTimer
import com.jar.app.core_utils.data.DecimalDigitsInputFilter
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.core_utils.data.RoundAmountToIntInputFilter
import com.jar.app.feature_buy_gold_v2.BuildConfig
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2Api
import com.jar.app.feature_buy_gold_v2.databinding.FragmentNewBuyGoldV2Binding
import com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code.CouponCodeV2Adapter
import com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code.CouponCodeVariantTwoBinder
import com.jar.app.feature_buy_gold_v2.impl.ui.suggested_amount.SuggestedGoldAmountAdapter
import com.jar.app.feature_buy_gold_v2.impl.util.isCouponExpired
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BannerTimerDetails
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByVolumeRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldInputData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldV2BreakdownData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.ContextBannerResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.InitiateBuyGoldData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.NewPaymentStripForBreakdown
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentOptionsData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentSectionHeaderType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldUpiApp
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldRequestType
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey.Shown_BuyGoldScreen_Ts
import com.jar.app.feature_buy_gold_v2.shared.util.ScreenName
import com.jar.app.feature_coupon_api.domain.event.CouponCodeEnteredEvent
import com.jar.app.feature_coupon_api.domain.model.ApplyCouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.CouponApplied
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.CouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.CouponCodeVariant
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState
import com.jar.app.feature_coupon_api.util.CouponOrderUtil
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_payment.impl.ui.payment_option.PayNowSection
import com.jar.app.feature_user_api.domain.model.SuggestedAmountOptions
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
internal class BuyGoldV2Fragment : BaseFragment<FragmentNewBuyGoldV2Binding>(), BaseResources {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var couponOrderUtil: CouponOrderUtil

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var buyGoldApi: BuyGoldV2Api

    @Inject
    lateinit var prefsApi: PrefsApi

    private var isShownEventSynced = false

    private var glide: RequestManager? = null
    private var target: CustomTarget<Drawable> = object : CustomTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            uiScope.launch {
                if (isNewPaymentStripVisible.not()) {
                    binding.layoutOldPaymentStrip.ivBuyNow.setImageDrawable(resource)
                }
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }

    private val interBoldFont by lazy {
        try {
            ResourcesCompat.getFont(requireContext(), com.jar.app.core_ui.R.font.inter_bold)
        } catch (exception: Resources.NotFoundException) {
            null
        }
    }

    private var socialProofIconTarget: CustomTarget<Drawable> = object : CustomTarget<Drawable>() {
        override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?
        ) {
            binding.tvSocialText.setCompoundDrawablesWithIntrinsicBounds(
                resource,
                null,
                null,
                null
            )
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }
    private val viewModelProvider by hiltNavGraphViewModels<BuyGoldV2FragmentViewModelAndroid>(R.id.buy_gold_v2_navigation)

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val isFromOnboarding by lazy {
        args.buyGoldFlowContext == BaseConstants.BuyGoldFlowContext.ONBOARDING
    }

    private val args by navArgs<BuyGoldV2FragmentArgs>()

    private var topStripTimerJob: Job? = null

    private var isTimerFinished = false
    private var isFirstTimeGoldFetched = true

    private var textSizeIncreaseAnimation: ObjectAnimator? = null
    private var textSizeDecreaseAnimation: ObjectAnimator? = null

    private var isCouponApplied = false
    private var hasAppliedCouponCodePassedFromArgsOnce = false
    private var isManualInputFlow = false
    private var hasPreAppliedCouponCode = false
    private var currentGoldPrice: Float? = null

    private var suggestedAmountOptions: SuggestedAmountOptions? = null

    private var couponListJob: Job? = null
    private var bestCouponJob: Job? = null
    private var bestTagJob: Job? = null

    private var isNewPaymentStripVisible = false
    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    companion object {
        const val TEXT_SIZE_ANIMATION_MILLIS = 300L
        const val MIN_TEXT_SIZE_FOR_LABEL = 14F
        const val MAX_TEXT_SIZE_FOR_LABEL = 28F

        const val TIMER_UNIT_SECONDS = "TIMER_UNIT_SECONDS"
        const val TIMER_UNIT_MINUTES = "TIMER_UNIT_MINUTES"
        const val TIMER_UNIT_HOURS = "TIMER_UNIT_HOURS"

        const val DEFAULT_CTA_TEXT = "Buy Now"
        const val DEFAULT_APP_CHOOSER = "Pay Using"
    }

    private val buyGoldInputData by lazy {
        try {
            args.data.takeIf { it.isNotBlank() }?.let {
                return@lazy serializer.decodeFromString<BuyGoldInputData>(decodeUrl(it))
            } ?: kotlin.run {
                return@lazy BuyGoldInputData()
            }
        } catch (e: Exception) {
            return@lazy BuyGoldInputData()
        }
    }

    private val isFromJackpotScreen by lazy {
        buyGoldInputData.isFromJackpotScreen
    }

    private val prefilledAmountForFailedState by lazy {
        buyGoldInputData.prefilledAmountForFailedState.takeIf { it != null } ?: 0f
    }

    private val weeklyAmount by lazy {
        buyGoldInputData.challengeAmount.takeIf { it != null } ?: 0f
    }

    private val showWeeklyChallengeAnimation by lazy {
        buyGoldInputData.showWeeklyChallengeAnimation
    }

    private val couponCodeName by lazy {
        buyGoldInputData.couponCode.takeIf { !it.isNullOrBlank() } ?: BuyGoldV2Constants.NO_CODE
    }

    private val couponTypePassedFromArgs by lazy {
        buyGoldInputData.couponType.takeIf { !it.isNullOrBlank() } ?: BuyGoldV2Constants.NO_CODE
    }

    private val prefillAmountPassedFromArgs by lazy {
        buyGoldInputData.prefillAmount
    }

    private val couponCodePassedFromArgs by lazy {
        decodeUrl(couponCodeName)
    }

    private var suggestedGoldAmountAdapter: SuggestedGoldAmountAdapter? = null
    private var couponCodeV2Adapter: CouponCodeV2Adapter? = null

    private var adapterDataObserver: RecyclerView.AdapterDataObserver? = null

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)
    private val couponSpaceItemDecoration = SpaceItemDecoration(6.dp, 0.dp)

    private var paymentJob: Job? = null

    private val weakReference: WeakReference<View> by lazy {
        WeakReference(binding.root)
    }
    private val confettiAnimationListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {}
        override fun onAnimationCancel(p0: Animator) {}
        override fun onAnimationRepeat(p0: Animator) {}
        override fun onAnimationEnd(p0: Animator) {
            binding.animSuccessConfetti.isVisible = false
        }
    }

    private val inputTextWatcher: TextWatcher by lazy {
        binding.etBuyGoldInput.doAfterTextChanged {
            if (isCouponApplied)
                removeAllCouponCode()
            if (viewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) {
                val inputString = it?.toString().orEmpty().replace(",", "").ifEmpty { null }
                inputString?.toIntOrNull()?.getFormattedAmount()?.let { formattedString ->
                    setTextInInputEditText(formattedString, shouldReattachTextWatcher = true)
                }
                viewModel.buyAmount = getRawAmount()?.toFloatOrNull().orZero()
            } else {
                viewModel.buyVolume = it?.toString()?.toFloatOrNull().orZero()
            }
            checkForMinimumAmountAndVolumeValue()
            suggestedGoldAmountAdapter?.setHighlightNumber(it.toString().toFloatOrNull())

        }
    }

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //In onboarding buy gold back CTA is removed and changed to gold brick icon which shouldn't be clickable
            if (isFromOnboarding.not())
                viewModel.getExitSurveyData()
            else
                openBuyGoldAbandonSheet()
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewBuyGoldV2Binding
        get() = FragmentNewBuyGoldV2Binding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        /**
         * Buy Gold Options API should be called first (Since it contains certain data to be set in UI
         * or required at later stage stage to set UI or check some conditions)
         * Before calling the rest of the APIs (Coupon Codes, Auspicious Time)
         * **/
        viewModel.fetchSuggestedAmount(
            selectedCouponCode = couponCodeName,
            flowContext = getContextForAmounts()
        )
        setupUI()
        setupListeners()
        observeLiveData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Added this event to debug buyFlowContext
        //Can be removed once we fix the drop offs
        analyticsHandler.postEvent(
            BuyGoldV2EventKey.BuyGoldScreen_FlowContext,
            mapOf(
                BuyGoldV2EventKey.Flow to args.buyGoldFlowContext
            )
        )
    }

    private fun getData() {
        viewModel.fetchData(getContextForAmounts())
        viewModel.fetchCurrentGoldBuyPrice()
    }

    private fun setupListeners() {
        binding.etBuyGoldInput.addTextChangedListener(inputTextWatcher)

        binding.tvTabInRupees.setDebounceClickListener {
            if (viewModel.buyGoldRequestType != BuyGoldRequestType.AMOUNT) {
                changeInputType(BuyGoldRequestType.AMOUNT)
            }
            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BuyInRs_BuyGoldScreen)
        }

        binding.tvTabInGrams.setDebounceClickListener {
            if (viewModel.buyGoldRequestType != BuyGoldRequestType.VOLUME) {
                changeInputType(BuyGoldRequestType.VOLUME)
            }
            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BuyInGms_BuyGoldScreen)
        }

        binding.etBuyGoldInput.setOnFocusChangeListener { view, focus ->
            if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                if (focus) {
                    binding.clBuyGoldInput.setBackgroundResource(R.drawable.feature_buy_gold_v2_bg_input)
                } else {
                    binding.clBuyGoldInput.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_round_black_bg_16dp)
                }
            }
        }

        binding.tvManualCoupon.setDebounceClickListener {
            clearFocus()
            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_DifferentCoupon_OrderPreviewScreen)
            navigateTo(
                BuyGoldV2FragmentDirections.actionBuyGoldV2FragmentToEnterCouponCodeDialog()
            )
        }

        binding.layoutOldPaymentStrip.tvViewBreakdown.setDebounceClickListener {
            navigateToBreakdown()
        }

        binding.layoutNewPaymentStrip.tvViewBreakdown.setDebounceClickListener {
            navigateToBreakdown()
        }

        binding.layoutOldPaymentStrip.llBuyNow.setDebounceClickListener {
            uiScope.launch {
                clearFocus()
                if (paymentManager.fetchInstalledUpiApps()
                        .isEmpty() && isFromOnboarding && lifecycle.currentState.isAtLeast(
                        Lifecycle.State.RESUMED
                    )
                ) {
                    getString(com.jar.app.core_ui.R.string.feature_payment_no_upi_apps_found).snackBar(
                        binding.root
                    )
                } else {
                    initiateBuyGoldRequest(BuyGoldPaymentType.PAYMENT_MANGER, null)
                }
            }
        }

        registerAdapterDataObserver()

        binding.couponVariant2View.tvCouponTitle.setOnClickListener {
            navigateTo(
                BuyGoldV2FragmentDirections.actionBuyGoldV2FragmentToCouponListPageFragment()
            )
        }
        binding.llViewDetailsContainer.setOnClickListener {
            navigateTo(
                BuyGoldV2FragmentDirections.actionBuyGoldV2FragmentToCouponListPageFragment()
            )
        }
    }

    private fun navigateToBreakdown() {
        clearFocus()
        val selectedCoupon = viewModel.couponCodeResponse?.couponCodes?.find { it.isSelected }
        val amountWithoutTax =
            (viewModel.buyAmount * 100f) / (100f + viewModel.fetchCurrentBuyPriceResponse?.applicableTax.orZero()).roundUp(
                2
            )
        val newPaymentStripForBreakdown = if (isNewPaymentStripVisible) {
            viewModel.lastUsedUpiApp?.let { upiApp ->
                NewPaymentStripForBreakdown(
                    ctaText = viewModel.buyGoldPaymentMethodsInfo?.ctaText ?: DEFAULT_CTA_TEXT,
                    paymentAppChooserText = viewModel.buyGoldPaymentMethodsInfo?.primaryHeaderText
                        ?: DEFAULT_APP_CHOOSER,
                    lastUsedUpiApp = upiApp,
                    maxPaymentMethodsCount = viewModel.buyGoldPaymentMethodsInfo?.paymentMethodsMaxCount
                        ?: BuyGoldV2Constants.DEFAULT_MAX_PAYMENT_METHODS_COUNT
                )
            } ?: kotlin.run {
                null
            }
        } else {
            null
        }
        navigateTo(
            BuyGoldV2FragmentDirections.actionBuyGoldV2FragmentToBuyGoldV2BreakdownBottomSheetFragmentV2(
                BuyGoldV2BreakdownData(
                    couponCode = if (selectedCoupon == null) null else serializer.encodeToString(
                        selectedCoupon
                    ),
                    totalPayableAmount = viewModel.buyAmount,
                    goldValue = amountWithoutTax,
                    goldVolume = viewModel.buyVolume,
                    applicableTax = viewModel.fetchCurrentBuyPriceResponse?.applicableTax.orZero(),
                    goldPurchasePrice = currentGoldPrice,
                    newPaymentStripForBreakdown = newPaymentStripForBreakdown
                )
            )
        )
        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Clicked_ShowBreakdown_BuyGoldScreen,
            mapOf(
                BuyGoldV2EventKey.CouponApplied to viewModel.couponCodeResponse?.couponCodes?.first()?.isSelected.orFalse(),
                BuyGoldV2EventKey.Experiment to getExperimentName()
            )
        )
    }

    private fun getRawAmount() = binding.etBuyGoldInput.text?.toString()?.replace(",", "")

    private fun observeLiveData() {
        observeInitiateBuyGoldSavedState()
        observeAuspiciousTimeLiveData()
        observeCurrentBuyPriceLiveData()
        observeAmountFromVolumeLiveData()
        observeVolumeFromAmountLiveData()
        observeSuggestedGoldAmountLiveData()
        observeCouponCodesLiveData()
        observeApplyCouponLiveData()
        observeCouponAppliedLiveData()
        observePaymentStripLiveData()
        observeBuyGoldLiveData()
        observeCurrentNetworkStateLiveData()
        observeContextBannerFlow()
        observeExitSurveyFlow()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.appliedCoupon.collectLatest {
                    it?.let { it1 -> onCouponCodeClicked(it1) }
                }
            }
        }

        //Exit Flow
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            BuyGoldV2Constants.EXIT_BUY_GOLD_FLOW
        )?.observe(viewLifecycleOwner) {
            if (it)
                navigateTo(
                    uri = BaseConstants.InternalDeepLinks.HOME,
                    popUpTo = R.id.buyGoldV2Fragment,
                    inclusive = true
                )
        }
    }

    private fun observeExitSurveyFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exitSurveyResponse.collectLatest {
                it?.let {
                    if (it)
                        EventBus.getDefault().post(
                            HandleDeepLinkEvent("$EXIT_SURVEY_DEEPLINK/${ExitSurveyRequestEnum.MANUAL_BUY.name}")
                        )
                    else
                        popBackStack()
                }
            }
        }
    }

    private fun observeInitiateBuyGoldSavedState() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<InitiateBuyGoldData>(
            BuyGoldV2Constants.INITIATE_BUY_GOLD_DATA
        )?.observe(viewLifecycleOwner) {
            initiateBuyGoldRequest(it.buyGoldPaymentType, it.selectedUpiApp)
        }
    }

    private fun getLastUsedUpiApp() {
        viewLifecycleOwner.lifecycleScope.launch {
            paymentManager.fetchLastUsedUpiApp(BaseConstants.BuyGoldFlowContext.BUY_GOLD)
                .collectLatest {
                    viewModel.lastUsedUpiApp = it.data
                    setPaymentStripUI()
                }
        }
    }

    private fun setPaymentStripUI() {
        viewModel.buyGoldPaymentMethodsInfo?.let { paymentData ->
            if (paymentData.shouldShowPaymentMethods.orFalse()) {
                viewModel.lastUsedUpiApp?.let { upiApp ->
                    renderNewPaymentStrip(false)
                    setPaymentStripVisibilityAndFetchData(true)
                } ?: kotlin.run {
                    setPaymentStripVisibilityAndFetchData(false)
                }
            } else {
                setPaymentStripVisibilityAndFetchData(false)
            }
        } ?: kotlin.run {
            setPaymentStripVisibilityAndFetchData(false)
        }
    }

    private fun renderNewPaymentStrip(isEnabled: Boolean) {
        viewModel.lastUsedUpiApp?.let { upiApp ->
            binding.layoutNewPaymentStrip.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    Column {
                        val buyGoldPaymentMethodsInfo = viewModel.buyGoldPaymentMethodsInfo
                        PayNowSection(
                            oneTimeUpiApp = upiApp,
                            payNowCtaText = buyGoldPaymentMethodsInfo?.ctaText ?: DEFAULT_CTA_TEXT,
                            appChooserText = buyGoldPaymentMethodsInfo?.primaryHeaderText
                                ?: DEFAULT_APP_CHOOSER,
                            onAppChooserClicked = {
                                analyticsHandler.postEvent(
                                    BuyGoldV2EventKey.BuyGold_AutoPayMethod_Click,
                                    mapOf(
                                        BuyGoldV2EventKey.recommended_upi to viewModel.lastUsedUpiApp?.packageName.orEmpty()
                                            .getAppNameFromPkgName(requireContext().applicationContext.packageManager)
                                            .orEmpty(),
                                        BuyGoldV2EventKey.Experiment to getExperimentName()
                                    )
                                )
                                val buyGoldPaymentOptionsData = BuyGoldPaymentOptionsData(
                                    context = BaseConstants.BuyGoldFlowContext.BUY_GOLD,
                                    maxPaymentMethodsCount = buyGoldPaymentMethodsInfo?.paymentMethodsMaxCount
                                        ?: BuyGoldV2Constants.DEFAULT_MAX_PAYMENT_METHODS_COUNT,
                                    ctaText = if (isNewPaymentStripVisible) buyGoldPaymentMethodsInfo?.ctaText else binding.layoutOldPaymentStrip.tvBuyNow.text?.toString()
                                        .orEmpty()
                                )
                                val encoded = serializer.encodeToString(buyGoldPaymentOptionsData)
                                navigateTo("android-app://com.jar.app/buyGoldPaymentOption/$encoded")
                            },
                            onPayNowClicked = {
                                uiScope.launch {
                                    if (paymentManager.fetchInstalledUpiApps()
                                            .isEmpty() && isFromOnboarding && lifecycle.currentState.isAtLeast(
                                            Lifecycle.State.RESUMED
                                        )
                                    ) {
                                        getString(com.jar.app.core_ui.R.string.feature_payment_no_upi_apps_found).snackBar(
                                            binding.root
                                        )
                                    } else {
                                        val buyGoldUpiApp = BuyGoldUpiApp(
                                            payerApp = upiApp.packageName,
                                            headerType = BuyGoldPaymentSectionHeaderType.RECOMMENDED
                                        )
                                        initiateBuyGoldRequest(
                                            BuyGoldPaymentType.JUSPAY_UPI_INTENT,
                                            buyGoldUpiApp
                                        )
                                    }
                                }
                            },
                            showPaymentSecureFooter = true,
                            isCtaEnabled = isEnabled,
                            isAppChooserCtaEnabled = isEnabled
                        )
                    }
                }
            }
        }
    }

    private fun setPaymentStripVisibilityAndFetchData(shouldShowNew: Boolean) {
        isNewPaymentStripVisible = shouldShowNew
        binding.layoutNewPaymentStrip.root.isVisible = shouldShowNew
        binding.layoutOldPaymentStrip.root.isVisible = shouldShowNew.not()
        val viewBreakDownText =
            getString(com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_view_breakdown)
        if (isNewPaymentStripVisible) {
            binding.layoutNewPaymentStrip.tvViewBreakdown.setHtmlText(viewBreakDownText)
        } else {
            binding.layoutOldPaymentStrip.tvViewBreakdown.setHtmlText(viewBreakDownText)
        }

        //Get Data and Set the rest of the UI
        getData()
        setWeeklyChallengeComponent()
        setCtaData(
            suggestedAmountOptions?.ctaText
                ?: getString(com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_buy_now)
        )
        prefillAmountPassedFromArgs?.let {
            prefillRecommendedAmount(it.toFloat())
        } ?: kotlin.run {
            if (prefilledAmountForFailedState == 0f) {
                suggestedAmountOptions?.prefillAmount?.let {
                    prefillRecommendedAmount(it)
                } ?: run {
                    isManualInputFlow = true
                    toggleInputState(
                        shouldSetDisableState = true,
                        shouldIgnoreErrorUI = true
                    )
                    binding.etBuyGoldInput.showKeyboard()
                }
            } else {
                prefillRecommendedAmount(prefilledAmountForFailedState)
            }
        }
        suggestedGoldAmountAdapter?.submitList(suggestedAmountOptions?.options)

        suggestedAmountOptions?.socialProofText?.let { socialText ->
            binding.tvSocialText.setHtmlText(socialText)
            suggestedAmountOptions?.socialProofIcon?.let { icon ->
                glide?.asDrawable()
                    ?.load(icon)
                    ?.override(16.dp)
                    ?.into(socialProofIconTarget)
            }

        }
    }

    private fun observeContextBannerFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchContextBanner(getContextForAmounts()).collect(
                    onSuccess = {
                        analyticsHandler.postEvent(
                            BuyGoldV2EventKey.Shown_BuyGoldScreenBanner,
                            mapOf(
                                BuyGoldV2EventKey.Banner_Title to it?.header.orEmpty(),
                                BuyGoldV2EventKey.Flow to it?.bannerType.orEmpty(),
                                BuyGoldV2EventKey.Experiment to getExperimentName()
                            )
                        )
                        setupContextBanner(it)
                    },
                    onError = { errorMessage, errorCode ->
                        postApiFailureEvent(
                            apiType = BuyGoldV2Constants.ApiTypeForAnalytics.FETCH_BANNER_CONTEXT,
                            errorCode = errorCode.orEmpty(),
                            errorMessage = errorMessage
                        )
                    }
                )
            }
        }
    }

    private fun setupContextBanner(bannerData: ContextBannerResponse?) {
        bannerData?.let {
            with(binding.layoutPriceDropOrAuspiciousDate) {
                root.isVisible = true
                val startColor = Color.parseColor(it.bgStartColor)
                val endColor = Color.parseColor(it.bgEndColor)
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(startColor, endColor)
                )
                root.background = gradientDrawable
                Glide.with(requireContext()).load(it.iconDetails.icon).into(ivStripIcon)
                tvStripTitle.text = it.header
                tvStripTitle.setTextColor(Color.parseColor(it.headerColor))
                tvStripDescription.setTextColor(Color.parseColor(it.descColor))
                tvStripDescription.text = it.description

                it.timerDetails?.let { it1 -> setRightImage(llRightView, it1) } ?: run {
                    llRightView.isVisible = false
                }
            }
        } ?: run {
            binding.layoutPriceDropOrAuspiciousDate.root.isVisible = false
        }

    }

    private fun setRightImage(llRightView: LinearLayout, timerDetails: BannerTimerDetails) {
        timerDetails.desc.let {
            binding.layoutPriceDropOrAuspiciousDate.tvTimerDesc.text = it
            binding.layoutPriceDropOrAuspiciousDate.tvTimerDesc.setTextColor(
                Color.parseColor(
                    timerDetails.descColor
                )
            )
        }
        timerDetails.time.let {
            val numbers = it.map { it.toString() }
            llRightView.removeAllViews()
            for (number in numbers) {
                llRightView.addView(createTextView(number, timerDetails.bgColor))
            }
        }

    }

    private fun createTextView(text: String, bgTint: String = "#66000000"): AppCompatTextView {
        return AppCompatTextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.marginEnd = 4.dp
            }

            background = ContextCompat.getDrawable(
                context,
                R.drawable.feature_buy_gold_v2_bg_rounded_8dp_black_40opacity
            )?.apply {
                setTint(Color.parseColor(bgTint))
            }
            setPadding(8.dp, 6.dp, 8.dp, 6.dp)
            textSize = 14f

            interBoldFont?.let {
                typeface = it
            }


            gravity = Gravity.CENTER
            setText(text)
            setTextColor(ContextCompat.getColor(context, com.jar.app.core_ui.R.color.white))
        }

    }

    private fun observeBuyGoldLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.buyGoldFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        it?.let {
                            dismissProgressBar()
                            EventBus.getDefault().post(RefreshCouponDiscoverEvent())
                            when (viewModel.buyGoldPaymentType) {
                                BuyGoldPaymentType.PAYMENT_MANGER -> {
                                    paymentJob?.cancel()
                                    paymentJob = appScope.launch(dispatcherProvider.main) {
                                        paymentManager.initiateOneTimePayment(it)
                                            .collectUnwrapped(
                                                onLoading = {
                                                    showProgressBar()
                                                },
                                                onSuccess = { fetchManualPaymentStatusResponse ->
                                                    dismissProgressBar()
                                                    openOrderStatus(
                                                        fetchManualPaymentStatusResponse
                                                    )
                                                },
                                                onError = { message, errorCode ->
                                                    view?.let {
                                                        uiScope.launch {
                                                            message.toast(it)
                                                        }
                                                    }
                                                }
                                            )
                                    }
                                }

                                BuyGoldPaymentType.JUSPAY_UPI_INTENT -> {
                                    dismissProgressBar()
                                    paymentJob?.cancel()
                                    paymentJob = appScope.launch(dispatcherProvider.main) {
                                        paymentManager.initiatePaymentWithUpiApp(
                                            initiatePaymentResponse = it,
                                            upiApp = UpiApp(
                                                packageName = viewModel.selectedUpiApp?.payerApp!!,
                                                appName = viewModel.selectedUpiApp?.payerApp!!.getAppNameFromPkgName(
                                                    requireContext().applicationContext.packageManager
                                                ).orEmpty()
                                            ),
                                            initiatePageFragmentId = R.id.buyGoldV2Fragment
                                        ).collectUnwrapped(
                                            onLoading = {
                                                showProgressBar()
                                            },
                                            onSuccess = { fetchManualPaymentStatusResponse ->
                                                dismissProgressBar()
                                                openOrderStatus(
                                                    fetchManualPaymentStatusResponse
                                                )
                                            },
                                            onError = { errorMessage, _ ->
                                                dismissProgressBar()
                                                if (errorMessage.isNotEmpty()) {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        errorMessage,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    onError = { errorMessage, errorCode ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                        if (errorCode == BaseConstants.ErrorCode.INVALID_BUY_PRICE_EXCEPTION) {
                            viewModel.fetchCurrentGoldBuyPrice()
                        }
                    }
                )
            }
        }
    }

    private fun openOrderStatus(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        fetchManualPaymentStatusResponse.transactionId?.let { transactionId ->
            fetchManualPaymentStatusResponse.paymentProvider?.let { paymentProvider ->
                popBackStack(R.id.buyGoldV2Fragment, true)
                buyGoldApi.openOrderStatusFlow(
                    transactionId = transactionId,
                    paymentProvider = paymentProvider,
                    paymentFlowSource = args.buyGoldFlowContext,
                    isOneTimeInvestment = fetchManualPaymentStatusResponse.oneTimeInvestment.orFalse(),
                    buyGoldFlowContext = args.buyGoldFlowContext
                )
            }
        }
    }

    private fun observePaymentStripLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.payableAmountFlow.collectLatest {
                    it?.let {
                        val amountText = getCustomStringFormatted(
                            MR.strings.feature_buy_gold_v2_rupees_x_string,
                            it.roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true)
                        )
                        binding.layoutNewPaymentStrip.tvBuyAmount.text = amountText
                        binding.layoutOldPaymentStrip.tvBuyAmount.text = amountText

                        viewModel.buyAmount = it.roundUp(2).orZero()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.rewardAmountFlow.collectLatest {
                    it?.let {
                        if (it == 0.0f) {
                            if (isNewPaymentStripVisible) {
                                binding.layoutNewPaymentStrip.shimmerExtraAmount.stopShimmer()
                                binding.layoutNewPaymentStrip.groupExtraAmount.isVisible = false
                            } else {
                                binding.layoutOldPaymentStrip.shimmerExtraAmount.stopShimmer()
                                binding.layoutOldPaymentStrip.groupExtraAmount.isVisible = false
                            }
                        } else {
                            if (isNewPaymentStripVisible) {
                                binding.layoutNewPaymentStrip.groupExtraAmount.isVisible = true
                            } else {
                                binding.layoutOldPaymentStrip.groupExtraAmount.isVisible = true
                            }
                            if (viewModel.couponCodeResponse?.couponCodes?.first()?.couponCodeVariant == CouponCodeVariant.COUPON_VARIANT_ONE.name) {
                                val extraAmountText = getCustomStringFormatted(
                                    MR.strings.feature_buy_gold_v2_extra_x_rupees,
                                    it.roundUp(2).getFormattedAmount()
                                )
                                if (isNewPaymentStripVisible) {
                                    binding.layoutNewPaymentStrip.shimmerExtraAmount.startShimmer()
                                    binding.layoutNewPaymentStrip.tvExtraAmount.text =
                                        extraAmountText
                                } else {
                                    binding.layoutOldPaymentStrip.shimmerExtraAmount.startShimmer()
                                    binding.layoutOldPaymentStrip.tvExtraAmount.text =
                                        extraAmountText
                                }
                            } else {
                                val extraAmountText = getCustomStringFormatted(
                                    MR.strings.feature_buy_gold_v2_extra_x_rupees,
                                    it.roundUp(2).getFormattedAmount()
                                )
                                val extraAmountDrawable = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.feature_buy_gold_v2_bg_rounded_58ddc8_8dp
                                )
                                val extraAmountTextColor = ContextCompat.getColor(
                                    requireContext(),
                                    com.jar.app.core_ui.R.color.color_58DDC8
                                )

                                if (isNewPaymentStripVisible) {
                                    binding.layoutNewPaymentStrip.shimmerExtraAmount.startShimmer()
                                    binding.layoutNewPaymentStrip.tvExtraAmount.text =
                                        extraAmountText
                                    binding.layoutNewPaymentStrip.tvExtraAmount.background =
                                        extraAmountDrawable
                                    binding.layoutNewPaymentStrip.tvExtraAmount.setTextColor(
                                        extraAmountTextColor
                                    )
                                } else {
                                    binding.layoutOldPaymentStrip.shimmerExtraAmount.startShimmer()
                                    binding.layoutOldPaymentStrip.tvExtraAmount.text =
                                        extraAmountText
                                    binding.layoutOldPaymentStrip.tvExtraAmount.background =
                                        extraAmountDrawable
                                    binding.layoutOldPaymentStrip.tvExtraAmount.setTextColor(
                                        extraAmountTextColor
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private fun observeCouponAppliedLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isCouponAppliedFlow.collectLatest {
                    it?.let {
                        isCouponApplied = it
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.preApplyCouponFlow.collectLatest {
                    if (isCouponCodePassedFromArgs().not()
                        && prefilledAmountForFailedState == 0f
                        && viewModel.buyAmount != 0.0f
                    ) {
                        it?.let {
                            val coupon = it.first
                            val amount = it.second
                            if (amount != 0f && hasPreAppliedCouponCode.not()) {
                                hasPreAppliedCouponCode = true
                                uiScope.launch {
                                    delay(500)
                                    viewModel.applyCouponCode(
                                        coupon,
                                        ScreenName.Buy_Gold_Home_Screen.name
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeApplyCouponLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.applyCouponCodeFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            var position = 0
                            var ctr = 0
                            viewModel.couponCodeResponse?.couponCodes?.forEach { coupon ->
                                ctr++
                                if (coupon.couponCode == it.couponCode) {
                                    position = ctr
                                    return@forEach
                                }
                            }
                            analyticsHandler.postEvent(
                                BuyGoldV2EventKey.CouponCodeApplied_BuyGoldScreen,
                                mapOf(
                                    BuyGoldV2EventKey.couponTitle to it.title,
                                    BuyGoldV2EventKey.couponCode to it.couponCode,
                                    BuyGoldV2EventKey.isUserWinningsApplied to if (it.couponType == CouponType.WINNINGS.name) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                                    BuyGoldV2EventKey.POSITION to position,
                                    BuyGoldV2EventKey.Screen to it.screenName.orEmpty(),
                                    BuyGoldV2EventKey.Coupon_Title_Typed to it.isManuallyEntered,
                                    BuyGoldV2EventKey.Winnings_Status to it.couponCode.orEmpty()
                                        .getHtmlTextValue().toString(),
                                    BuyGoldV2EventKey.Experiment to getExperimentName()
                                )
                            )
                            setOnAppliedCouponUI(it, position)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(
                            binding.root,
                            com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                            progressColor = com.jar.app.core_ui.R.color.color_016AE1,
                            duration = 2000,
                            translationY = 0f
                        )
                        analyticsHandler.postEvent(
                            BuyGoldV2EventKey.Shown_ErrorMessage_OrderPreviewScreen,
                            mapOf(
                                BuyGoldV2EventKey.ErrorMessage to errorMessage,
                                BuyGoldV2EventKey.Experiment to getExperimentName()
                            )
                        )
                        removeAllCouponCode()
                    },
                )
            }
        }
    }

    private fun observeCouponCodesLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.couponCodesFlow.collectUnwrapped(
                    onLoading = {
                        binding.groupFetchingCoupons.isVisible = true
                    },
                    onSuccess = {
                        binding.groupFetchingCoupons.isVisible = false
                        viewModel.apiResponseCount += 1
                        if (viewModel.apiResponseCount == 1 && (args.buyGoldFlowContext == FLOATING_BUTTON || args.buyGoldFlowContext == HAMBURGER_MENU || args.buyGoldFlowContext == BaseConstants.BuyGoldFlowContext.HOME_SCREEN)) {
                            val currentTime = System.currentTimeMillis()
                            analyticsHandler.postEvent(
                                Shown_BuyGoldScreen_Ts,
                                mapOf(
                                    EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(
                                        endTimeTime = currentTime,
                                        startTime = args.clickTime.toLong()
                                    ),
                                    BuyGoldV2EventKey.Experiment to getExperimentName()
                                )
                            )
                        }
                        binding.groupFetchingCoupons.isVisible = false
                        it?.let { it1 -> sortCouponListAndSetAdapter(it1) }
                        checkForMinimumAmountAndVolumeValue()
                    },
                    onSuccessWithNullData = {
                        binding.groupFetchingCoupons.isVisible = false
                    },
                    onError = { errorMessage, errorCode ->
                        postApiFailureEvent(
                            apiType = BuyGoldV2Constants.ApiTypeForAnalytics.FETCH_COUPON_CODES,
                            errorCode = errorCode.orEmpty(),
                            errorMessage = errorMessage
                        )
                        errorMessage.snackBar(weakReference.get()!!)
                        binding.groupFetchingCoupons.isVisible = false
                    }
                )
            }
        }
    }

    private fun postApiFailureEvent(apiType: String, errorMessage: String, errorCode: String) {
        analyticsHandler.postEvent(
            BuyGoldV2EventKey.BuyGoldScreen_ApiFailed,
            mapOf(
                BuyGoldV2EventKey.ApiType to apiType,
                BuyGoldV2EventKey.ErrorMessage to errorMessage,
                BuyGoldV2EventKey.ErrorCode to errorCode,
                BuyGoldV2EventKey.Flow to args.buyGoldFlowContext
            )
        )
    }

    private fun observeSuggestedGoldAmountLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.suggestedAmountFlow.collect(
                    onSuccess = {
                        suggestedAmountOptions = it?.suggestedAmount
                        if (BuildConfig.FLAVOR.contains("staging")) {
                            setPaymentStripVisibilityAndFetchData(shouldShowNew = false)
                        } else {
                            getLastUsedUpiApp()
                        }
                    },
                    onError = { errorMessage, errorCode ->
                        postApiFailureEvent(
                            apiType = BuyGoldV2Constants.ApiTypeForAnalytics.FETCH_SUGGESTED_AMOUNT,
                            errorCode = errorCode.orEmpty(),
                            errorMessage = errorMessage
                        )
                        setCtaData(getString(com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_buy_now))
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun setWeeklyChallengeComponent() {
        binding.llWeeklyChallengeComponent.isVisible =
            suggestedAmountOptions?.weeklyChallengeEligibleText != null
        Glide.with(requireContext()).load(suggestedAmountOptions?.weeklyChallengeIcon.orEmpty())
            .into(binding.ivWeeklyChallangeLogo)
        toggleWeeklyMagicComponentText()
    }

    private fun toggleWeeklyMagicComponentText() {
        binding.llWeeklyChallengeComponent.isVisible =
            viewModel.buyAmount >= getMinimumBuyGoldAmount()
                    && viewModel.buyAmount <= remoteConfigManager.getMaximumGoldBuyAmount()
                    && args.buyGoldFlowContext == BaseConstants.BuyGoldFlowContext.WEEKLY_CHALLENGE
        if (binding.llWeeklyChallengeComponent.isVisible) {
            binding.tvWeeklyChallengeMessage.setHtmlText(
                if (viewModel.buyAmount >= suggestedAmountOptions?.weeklyChallengeMinimumAmount.orZero())
                    suggestedAmountOptions?.weeklyChallengeEligibleText.orEmpty()
                else
                    suggestedAmountOptions?.weeklyChallengeNotEligibleText.orEmpty()
            )
        }
    }

    private fun setCtaData(ctaText: String) {
        if (isNewPaymentStripVisible.not()) {
            binding.layoutOldPaymentStrip.tvBuyNow.setHtmlText(ctaText)
            glide = Glide.with(requireContext())
            glide?.load(remoteConfigManager.getBuyGoldCtaDrawableLink())
                ?.override(40.dp)
                ?.into(target)
        }
    }

    private fun observeAmountFromVolumeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.amountFromVolumeFlow.collectUnwrapped(
                    onSuccess = {
                        val buyAmount = if (viewModel.buyVolume == 0.0f) 0.0f else it
                        if (viewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) {
                            setTextInInputEditText(buyAmount.getFormattedAmount())
                        } else {
                            binding.tvRupeeSymbol.text = getCustomStringFormatted(
                                MR.strings.feature_buy_gold_v2_rupees_x_string,
                                it.roundUp(2)
                                    .getFormattedAmount(shouldRemoveTrailingZeros = true)
                            )
                        }
                        viewModel.buyAmount = buyAmount
                        checkForNewBestCoupon()
                        shouldShowBestTag()
                        toggleWeeklyMagicComponentText()
                    }
                )
            }
        }
    }

    private fun observeVolumeFromAmountLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.volumeFromAmountFlow.collectUnwrapped(
                    onSuccess = {
                        if (viewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) {
                            setTextInInputEditText(it.volumeToStringWithoutTrailingZeros())
                        } else {
                            binding.tvGramSymbol.text = getCustomStringFormatted(
                                MR.strings.feature_buy_gold_v2_x_gm_string,
                                it.volumeToStringWithoutTrailingZeros()
                            )
                        }
                        viewModel.buyVolume = it
                        checkForNewBestCoupon()
                        shouldShowBestTag()
                        toggleWeeklyMagicComponentText()
                    }
                )
            }
        }
    }

    private fun observeCurrentBuyPriceLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.currentGoldBuyPriceFlow.collect(
                    onLoading = {
                        if (!isFirstTimeGoldFetched) {
                            binding.goldPriceProgressLayout.setTimerState(PriceValidityTimer.TimerState.FETCHING_PRICE)
                        }
                        binding.svContent.isClickable = false
                        if (isNewPaymentStripVisible) {
                            binding.layoutNewPaymentStrip.tvViewBreakdown.isClickable = false
                            renderNewPaymentStrip(false)
                        } else {
                            binding.layoutOldPaymentStrip.llBuyNow.isClickable = false
                            binding.layoutOldPaymentStrip.tvViewBreakdown.isClickable = false
                        }
                    },
                    onSuccess = {
                        currentGoldPrice = it.price
                        if (!isFirstTimeGoldFetched) {
                            if (viewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) {
                                viewModel.calculateAmountFromVolume(viewModel.buyVolume)
                            } else {
                                viewModel.calculateVolumeFromAmount(viewModel.buyAmount)
                            }
                        }
                        if (isCouponApplied) {
                            removeAllCouponCode()
                        }
                        isFirstTimeGoldFetched = false
                        binding.goldPriceProgressLayout.setTimerState(PriceValidityTimer.TimerState.NEW_PRICE_FETCHED)
                        uiScope.launch {
                            //Intentional delay to show price fetched message
                            delay(1000)
                            binding.svContent.isClickable = true
                            if (isNewPaymentStripVisible) {
                                binding.layoutNewPaymentStrip.tvViewBreakdown.isClickable = true
                                renderNewPaymentStrip(true)
                            } else {
                                binding.layoutOldPaymentStrip.llBuyNow.isClickable = true
                                binding.layoutOldPaymentStrip.tvViewBreakdown.isClickable = true
                            }
                            binding.goldPriceProgressLayout.start(
                                livePriceMessage = getCustomStringFormatted(
                                    MR.strings.feature_buy_gold_v2_buy_price,
                                    it.price
                                ),
                                validityInMillis = it.getValidityInMillis(),
                                uiScope = uiScope,
                                onInterval = {
                                    val remainingTimeInMinutes = getMinutesFromMillis(it)
                                    //If greater tha equal to 3 minutes change progress color to green else red
                                    if (remainingTimeInMinutes >= 3) {
                                        binding.goldPriceProgressLayout.setProgressBarTintColor(
                                            com.jar.app.core_ui.R.color.color_335665
                                        )
                                    } else {
                                        binding.goldPriceProgressLayout.setProgressBarTintColor(
                                            com.jar.app.core_ui.R.color.color_EF8A8A_opacity_30
                                        )
                                    }
                                },
                                onFinish = {
                                    isTimerFinished = true
                                    viewModel.fetchCurrentGoldBuyPrice()
                                },
                            )
                            if (isTimerFinished) {
                                isTimerFinished = false
                                updatedPriceEventFire(it.price, it.isPriceDrop.orFalse())
                            }
                            if (it.isPriceDrop.orFalse()) {
                                viewModel.auspiciousTimeFlow.value.data?.data?.isLive?.let { isLive ->
                                    if (isLive.not()) {
                                        startTopStripTimer(
                                            it.getValidityInMillis(),
                                            isAuspiciousTime = false
                                        )
                                    }
                                }
                            }
                            viewModel.currentGoldPrice = it.price
                        }
                    },
                    onError = { errorMessage, _ ->
                        isFirstTimeGoldFetched = false
                        analyticsHandler.postEvent(
                            BuyGoldV2EventKey.Shown_ErrorMessage_BuyGoldScreen,
                            mapOf(
                                BuyGoldV2EventKey.ErrorMessage to errorMessage,
                                BuyGoldV2EventKey.Experiment to getExperimentName()
                            )
                        )
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun sendShownEvent(suggestedAmountOptions: SuggestedAmountOptions?) {
        var allSuggestedAmounts = ""
        suggestedAmountOptions?.options?.forEach {
            allSuggestedAmounts = allSuggestedAmounts + "," + it.amount
        }
        val popularAmount =
            suggestedAmountOptions?.options?.find { suggestedOption -> suggestedOption.prefill.orFalse() }?.amount
                ?: kotlin.run {
                    suggestedAmountOptions?.options?.find { suggestedOption -> suggestedOption.recommended.orFalse() }?.amount
                }
        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Shown_BuyGoldScreen,
            mapOf(
                BuyGoldV2EventKey.isNewBuyGoldFlow to BuyGoldV2EventKey.Buy_Gold_YES,
                BuyGoldV2EventKey.entryType to args.buyGoldFlowContext,
                BuyGoldV2EventKey.popularAmount to popularAmount.orZero(),
                BuyGoldV2EventKey.allSuggestedValues to allSuggestedAmounts,
                BuyGoldV2EventKey.preSelectedAmount to binding.etBuyGoldInput.text?.toString()
                    .orEmpty(),
                BuyGoldV2EventKey.weeklymagicmessageshown to suggestedAmountOptions?.weeklyChallengeEligibleText.orEmpty()
                    .getHtmlTextValue().toString(),
                BuyGoldV2EventKey.Best_Offer_Coupon_Title to viewModel.couponCodeResponse?.couponCodes?.first()?.title.orEmpty(),
                BuyGoldV2EventKey.Best_Offer_Coupon_Status to viewModel.couponCodeResponse?.couponCodes?.first()?.isCouponAmountEligible.orFalse(),
                BuyGoldV2EventKey.auto_select_payment to if (viewModel.lastUsedUpiApp != null) BuyGoldV2EventKey.True else BuyGoldV2EventKey.False,
                BuyGoldV2EventKey.recommended_upi to viewModel.lastUsedUpiApp?.packageName.orEmpty()
                    .getAppNameFromPkgName(requireContext().applicationContext.packageManager)
                    .orEmpty(),
                BuyGoldV2EventKey.Experiment to getExperimentName()
            )
        )
    }

    private fun updatedPriceEventFire(newPrice: Float, isPriceDrop: Boolean) {
        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Shown_PriceChangeToast_BuyGoldScreen,
            mapOf(
                BuyGoldV2EventKey.PreviousPrice to viewModel.currentGoldPrice,
                BuyGoldV2EventKey.isGoldPriceDropped to if (isPriceDrop) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                BuyGoldV2EventKey.NewPrice to newPrice,
                BuyGoldV2EventKey.Experiment to getExperimentName()
            )
        )
    }

    private fun sortCouponListAndSetAdapter(couponCodeResponse: CouponCodeResponse) {
        couponListJob?.cancel()
        couponListJob = uiScope.launch {
            viewModel.couponCodeResponse = viewModel.couponCodeResponse?.copy(
                couponCodes = couponOrderUtil.sortCouponListOrder(
                    couponCodeResponse.couponCodes.orEmpty().toMutableList(),
                    viewModel.buyAmount,
                    shouldInactiveAllCoupons = binding.tvErrorMessage.isVisible
                )

            )
            viewModel.couponCodeResponse?.let {
                setCouponsListInAdapter(it)
            }
        }
    }

    private fun shouldShowBestTag() {
        bestTagJob?.cancel()
        bestTagJob = uiScope.launch {
            val suggestedValue =
                if (viewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) {
                    suggestedAmountOptions?.volumeOptions?.find { it.isBestTag.orFalse() }?.amount
                } else {
                    suggestedAmountOptions?.options?.find { it.isBestTag.orFalse() }?.amount
                }
            val inputValue =
                if (viewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) viewModel.buyVolume else viewModel.buyAmount
            if (inputValue > suggestedValue.orZero()) {
                if (viewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) {
                    val newList = suggestedAmountOptions?.volumeOptions?.map {
                        it.copy(
                            isBestTag = false
                        )
                    }
                    suggestedGoldAmountAdapter?.submitList(newList)
                } else {
                    val newList = suggestedAmountOptions?.options?.map {
                        it.copy(
                            isBestTag = false
                        )
                    }
                    suggestedGoldAmountAdapter?.submitList(newList)
                }
            } else {
                suggestedAmountOptions?.volumeOptions?.find { suggestedOption -> suggestedOption.prefill.orFalse() }
                    ?.let { bestOption ->
                        bestOption.isBestTag = true
                    } ?: kotlin.run {
                    suggestedAmountOptions?.volumeOptions?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                        ?.let { bestOption ->
                            bestOption.isBestTag = true
                        }
                }
                suggestedAmountOptions?.options?.find { suggestedOption -> suggestedOption.prefill.orFalse() }
                    ?.let { bestOption ->
                        bestOption.isBestTag = true
                    } ?: kotlin.run {
                    suggestedAmountOptions?.options?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                        ?.let { bestOption ->
                            bestOption.isBestTag = true
                        }
                }
                suggestedAmountOptions?.let {
                    if (viewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) suggestedGoldAmountAdapter?.submitList(
                        it.options
                    ) else suggestedGoldAmountAdapter?.submitList(it.volumeOptions)
                }
            }
        }
    }

    private fun checkForNewBestCoupon() {
        if (isCouponCodePassedFromArgs().not()
            || (isCouponCodePassedFromArgs() && hasAppliedCouponCodePassedFromArgsOnce)
            || isManualInputFlow
        ) {
            bestCouponJob?.cancel()
            bestCouponJob = uiScope.launch {
                //Intentional delay
                delay(500)
                viewModel.couponCodeResponse?.let {
                    var couponOrder = ""
                    var winningsCoupon: CouponCode? = null
                    it.couponCodes.orEmpty().forEach {
                        if (it.getCouponType() == CouponType.WINNINGS) {
                            winningsCoupon = it
                        }
                        couponOrder = couponOrder + "," + it.couponCode
                    }
                    val winningsIndex = winningsCoupon?.let { winnings ->
                        it.couponCodes.orEmpty().indexOf(winnings)
                    } ?: 0
                    var isSuggestionClicked = BuyGoldV2EventKey.Buy_Gold_NO
                    if (viewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) {
                        suggestedAmountOptions?.options?.forEach {
                            if (it.amount == viewModel.buyAmount) {
                                isSuggestionClicked = BuyGoldV2EventKey.Buy_Gold_YES
                                return@forEach
                            }
                        }
                    } else {
                        suggestedAmountOptions?.volumeOptions?.forEach {
                            if (it.amount == viewModel.buyVolume) {
                                isSuggestionClicked = BuyGoldV2EventKey.Buy_Gold_YES
                                return@forEach
                            }
                        }
                    }
                    analyticsHandler.postEvent(
                        if (viewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) BuyGoldV2EventKey.BuyNow_GoldMoneyEntered else BuyGoldV2EventKey.BuyNow_GoldWeightEntered,
                        mapOf(
                            BuyGoldV2EventKey.Amount to viewModel.buyAmount,
                            BuyGoldV2EventKey.goldWeight to viewModel.buyVolume,
                            BuyGoldV2EventKey.couponOrder to couponOrder,
                            BuyGoldV2EventKey.isSuggestionClicked to isSuggestionClicked.orEmpty(),
                            BuyGoldV2EventKey.jarWinningsCouponPosition to winningsIndex + 1,
                            BuyGoldV2EventKey.Experiment to getExperimentName(),
                            BuyGoldV2EventKey.weeklymagicmessageshown to
                                    if (binding.tvWeeklyChallengeMessage.isVisible)
                                        if (viewModel.buyAmount >= suggestedAmountOptions?.weeklyChallengeMinimumAmount.orZero())
                                            suggestedAmountOptions?.weeklyChallengeEligibleText.orEmpty()
                                                .getHtmlTextValue().toString()
                                        else
                                            suggestedAmountOptions?.weeklyChallengeNotEligibleText.orEmpty()
                                                .getHtmlTextValue().toString()
                                    else ""
                        )
                    )
                    sortCouponListAndSetAdapter(it)
                }
            }
        }
    }

    private fun setCouponsListInAdapter(couponCodeResponse: CouponCodeResponse) {
        //Intentionally making a copy of list and then setting to adapter
        //At some instances the adapter was not updating the items even if the values were changed
        //DIFF Util somehow was considering the new item as old item
        val updatedList = couponCodeResponse.couponCodes?.map { it.copy() }
        checkCouponVariantAndSetup(couponCodeResponse, updatedList)
        binding.rvCoupon.isVisible = true

        applyCouponPassedFromArgs()
    }

    private fun checkCouponVariantAndSetup(
        couponCodeResponse: CouponCodeResponse,
        updatedList: List<CouponCode>?
    ) {
        if (isShownEventSynced.not()) {
            isShownEventSynced = true
            sendShownEvent(suggestedAmountOptions)
        }
        if (couponCodeResponse.couponCodes.orEmpty()
                .first().couponCodeVariant == CouponCodeVariant.COUPON_VARIANT_ONE.name
        ) {
            showCouponCodeVariant1()
            hideCouponCodeVariant2()
            couponCodeV2Adapter?.submitList(updatedList)
            binding.rvCoupon.isVisible = true
            binding.couponVariant2View.root.isVisible = false

        } else {
            hideCouponCodeVariant1()
            showCouponCodeVariant2()
            binding.couponVariant2View.root.isVisible = true
            viewModel.couponCodeResponse =
                viewModel.couponCodeResponse?.copy(
                    couponCodes = couponCodeResponse.couponCodes.orEmpty()
                        .sortedByDescending { it.isSelected })
            viewModel.couponCodeResponse?.couponCodes?.find { it.isSelected }?.let {
                setupCouponVariantTwo(it)
            } ?: run {
                viewModel.couponCodeResponse?.couponCodes?.first()
                    ?.let { setupCouponVariantTwo(it) }
            }
        }
    }

    private fun setupCouponVariantTwo(couponCode: CouponCode) {
        CouponCodeVariantTwoBinder(binding.couponVariant2View, requireContext(), uiScope,
            onApplyClick = { couponCode, _, screenName ->
                analyticsHandler.postEvent(
                    BuyGoldV2EventKey.ClickedApply_CouponTextbox_OrderPreviewScreen,
                    mapOf(
                        BuyGoldV2EventKey.couponTitle to couponCode.title.orEmpty(),
                        BuyGoldV2EventKey.moneySavedByCoupon to couponCode.getMaxRewardThatCanBeAvailed(
                            viewModel.buyAmount
                        ).orZero(),
                        BuyGoldV2EventKey.couponDiscountPercentage to couponCode.rewardPercentage.orZero(),
                        BuyGoldV2EventKey.isWinningsCoupon to if (couponCode.getCouponType() == CouponType.WINNINGS) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                        BuyGoldV2EventKey.Amount to viewModel.buyAmount,
                        BuyGoldV2EventKey.CouponCode to couponCode.couponCode.orEmpty(),
                        BuyGoldV2EventKey.Screen to screenName,
                        BuyGoldV2EventKey.Experiment to getExperimentName()
                    )
                )
                onCouponCodeClicked(couponCode)
            },
            onCouponExpired = { expiredCoupon ->
                viewModel.couponCodeResponse?.let {
                    it.couponCodes.orEmpty().find { it.couponCode == expiredCoupon.couponCode }
                        ?.let {
                            it.couponState =
                                CouponState.INACTIVE.name
                            it.isCouponAmountEligible = false
                            it.validityInMillis = -1L
                        }
                    if (expiredCoupon.isSelected) {
                        removeAllCouponCode()
                    } else {
                        sortCouponListAndSetAdapter(it)
                    }
                }

            }, getCurrentAmount = {
                viewModel.buyAmount
            },
            screenName = ScreenName.Buy_Gold_Home_Screen.name
        ).bind(couponCode)

        val textToUnderline = "View all offers"
        val spannable = SpannableString("View all offers")
        spannable.setSpan(
            UnderlineSpan(),
            0,
            textToUnderline.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvBgViewDetails.text = spannable
    }

    private fun observeAuspiciousTimeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.auspiciousTimeFlow.collect(
                    onSuccess = {
                        viewModel.isAuspiciousTime = it.isLive.orFalse()
                        if (it.isLive.orFalse()) {
                            val validity =
                                it.validityInSeconds.orZero().toLong().secondsToMillis()
                            startTopStripTimer(validity, true)
                            binding.bgAuspiciousLottie.isVisible = true
                            binding.bgAuspiciousLottie.playAnimation()
                            binding.separatorNormal.isVisible = false
                            binding.separatorShubhMuhrat.isVisible = true
                            binding.layoutPriceDropOrAuspiciousDate.root.isVisible = true
                        } else {
                            topStripTimerJob?.cancel()
                            binding.bgAuspiciousLottie.isVisible = false
                            binding.separatorNormal.isVisible = true
                            binding.separatorShubhMuhrat.isVisible = false
                            binding.bgAuspiciousLottie.cancelAnimation()
                            binding.layoutPriceDropOrAuspiciousDate.root.isVisible = false
                        }
                        analyticsHandler.postEvent(
                            BuyGoldV2EventKey.BuyGold_IsAuspiciousFetched,
                            mapOf(
                                BuyGoldV2EventKey.IsAuspicious to if (it.isLive.orFalse()) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                                BuyGoldV2EventKey.Experiment to getExperimentName()
                            )
                        )
                    }
                )
            }
        }
    }

    private fun startTopStripTimer(validityInMillis: Long, isAuspiciousTime: Boolean) {
        setupUIForTopStrip(isAuspiciousTime = isAuspiciousTime)
        topStripTimerJob?.cancel()
        topStripTimerJob = uiScope.countDownTimer(
            totalMillis = validityInMillis,
            onInterval = {
                val remainingSeconds = getSecondsFromMillis(it)
                val remainingMinutes = getMinutesFromMillis(it)
                val remainingHours = getHoursFromMillis(it)
                if (remainingSeconds <= 60L) {
                    setTimerText(remainingSeconds, null, TIMER_UNIT_SECONDS)
                } else if (remainingMinutes <= 60L) {
                    setTimerText(remainingMinutes, null, TIMER_UNIT_MINUTES)
                } else {
                    val minutes = remainingMinutes - remainingHours * 60L
                    val finalRemainingMinutes = if (minutes < 10) "0$minutes" else minutes
                    val finalRemainingHours =
                        if (remainingHours < 10) "0$remainingHours" else remainingHours
                    val remainingTimeHours =
                        "${finalRemainingHours}h\n${finalRemainingMinutes}m"

                    setTimerText(remainingHours + 1, remainingTimeHours, TIMER_UNIT_HOURS)
                }
            },
            onFinished = {
                binding.layoutPriceDropOrAuspiciousDate.root.isVisible = false
                viewModel.isAuspiciousTime = false
                binding.bgAuspiciousLottie.isVisible = false
                binding.separatorNormal.isVisible = true
                binding.separatorShubhMuhrat.isVisible = false
                binding.bgAuspiciousLottie.cancelAnimation()
            }
        )
    }

    private fun getSecondsFromMillis(millis: Long) = TimeUnit.MILLISECONDS.toSeconds(millis)

    private fun getMinutesFromMillis(millis: Long) = TimeUnit.MILLISECONDS.toMinutes(millis)

    private fun getHoursFromMillis(millis: Long) = TimeUnit.MILLISECONDS.toHours(millis)

    private fun setTimerText(
        remainingTime: Long,
        remainingTimeHours: String?,
        timerUnit: String
    ) {

        val timerText = if (timerUnit == TIMER_UNIT_HOURS) remainingTimeHours.orEmpty()
        else if (timerUnit == TIMER_UNIT_MINUTES) getCustomString(MR.strings.feature_buy_gold_v2_x_mins).getFormattedTextForOneIntegerValue(
            remainingTime.toInt()
        ) else if (remainingTime > 1) getCustomString(MR.strings.feature_buy_gold_v2_x_secs).getFormattedTextForOneIntegerValue(
            remainingTime.toInt()
        ) else getCustomString(MR.strings.feature_buy_gold_v2_x_sec).getFormattedTextForOneIntegerValue(
            remainingTime.toInt()
        )
        //  binding.layoutPriceDropOrAuspiciousDate.llRightView.addView(createTextView(timerText.toString()))
    }

    private fun setupUIForTopStrip(isAuspiciousTime: Boolean) {
        analyticsHandler.postEvent(
            if (isAuspiciousTime) BuyGoldV2EventKey.Shown_ShubhMuhurat_banner_BuyGoldScreen else BuyGoldV2EventKey.Shown_price_drop_banner_BuyGoldScreen
        )
        binding.layoutPriceDropOrAuspiciousDate.clRoot.background = ContextCompat.getDrawable(
            requireContext(),
            if (isAuspiciousTime) R.drawable.feature_buy_gold_v2_bg_shubh_muhurat else R.drawable.feature_buy_gold_v2_bg_price_drop
        )
        binding.layoutPriceDropOrAuspiciousDate.ivStripIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (isAuspiciousTime) R.drawable.feature_buy_gold_v2_ic_auspicious_date else R.drawable.feature_buy_gold_v2_ic_price_drop
            )
        )
        binding.layoutPriceDropOrAuspiciousDate.tvStripTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isAuspiciousTime) com.jar.app.core_ui.R.color.bgColor else com.jar.app.core_ui.R.color.white
            )
        )
        binding.layoutPriceDropOrAuspiciousDate.tvStripDescription.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isAuspiciousTime) com.jar.app.core_ui.R.color.bgColor else com.jar.app.core_ui.R.color.white
            )
        )
        binding.layoutPriceDropOrAuspiciousDate.tvStripTitle.text =
            if (isAuspiciousTime) getCustomString(MR.strings.feature_buy_gold_v2_lucky_you_it_s_shubh_muhurat) else getCustomString(
                MR.strings.feature_buy_gold_v2_gold_s_price_just_dropped
            )
    }

    private fun setupUI() {
        if (isFromOnboarding)
            prefsApi.setOnboardingComplete()
        setupFestivalCampaignUI()
        setupBuyPriceTimerUI()
        setupToolbar()
        setupToolbarListener()
        setupSuggestedGoldAmountAdapter()
        setupCouponCodeAdapter()
        binding.animSuccessConfetti.addAnimatorListener(confettiAnimationListener)
        suggestedAmountOptions?.let {
            setCtaData(
                it.ctaText
                    ?: DEFAULT_CTA_TEXT
            )
            setWeeklyChallengeComponent()
            changeInputType(viewModel.buyGoldRequestType)
        } ?: kotlin.run {
            binding.etBuyGoldInput.filters = arrayOf(
                InputFilter.LengthFilter(9),
                RoundAmountToIntInputFilter(
                    shouldRoundToInt = true,
                    isInputSeparatedByComma = true
                )
            )
        }

        binding.tvManualCoupon.setHtmlText(
            getString(com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_click_here_to_apply_different_code)
        )
    }

    private fun setupFestivalCampaignUI() {
        if (remoteConfigManager.isFestivalCampaignEnabled()) {
            val campaignImageUrl = remoteConfigManager.getFestivalBuyGoldScreenAsset()
            if (campaignImageUrl.isNotBlank()) {
                binding.apply {
                    ivFestivalCampaignBuyGold.isVisible = true
                    Glide.with(requireContext())
                        .load(campaignImageUrl)
                        .into(ivFestivalCampaignBuyGold)
                }
            } else {
                binding.ivFestivalCampaignBuyGold.isVisible = false
            }
        }
    }

    private fun setupBuyPriceTimerUI() {
        binding.goldPriceProgressLayout.shouldShowLiveTag(true)
        binding.goldPriceProgressLayout.setRootBackground(com.jar.app.core_ui.R.color.color_3c3357)
    }

    private fun setupCouponCodeAdapter() {
        binding.rvCoupon.setHasFixedSize(true)
        binding.rvCoupon.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvCoupon.addItemDecorationIfNoneAdded(couponSpaceItemDecoration)
        couponCodeV2Adapter = CouponCodeV2Adapter(
            uiScope = uiScope,
            onApplyClick = { couponCode, pos ->
                clearFocus()
                analyticsHandler.postEvent(
                    BuyGoldV2EventKey.ClickedApply_CouponTextbox_OrderPreviewScreen,
                    mapOf(
                        BuyGoldV2EventKey.POSITION to pos + 1,
                        BuyGoldV2EventKey.couponTitle to couponCode.title.orEmpty(),
                        BuyGoldV2EventKey.moneySavedByCoupon to couponCode.getMaxRewardThatCanBeAvailed(
                            viewModel.buyAmount
                        ).orZero(),
                        BuyGoldV2EventKey.couponDiscountPercentage to couponCode.rewardPercentage.orZero(),
                        BuyGoldV2EventKey.isWinningsCoupon to if (couponCode.getCouponType() == CouponType.WINNINGS) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                        BuyGoldV2EventKey.Amount to viewModel.buyAmount,
                        BuyGoldV2EventKey.CouponCode to couponCode.couponCode.orEmpty(),
                        BuyGoldV2EventKey.Experiment to getExperimentName()
                    )
                )
                onCouponCodeClicked(couponCode)
            },
            onRemoveCouponClick = { couponCode, pos ->
                clearFocus()
                onRemoveCouponClicked()
                showCouponRemovedSnackbar(couponCode)

            },
            onCouponExpired = { expiredCoupon ->
                viewModel.couponCodeResponse?.let {
                    it.couponCodes?.find { it.couponCode == expiredCoupon.couponCode }?.let {
                        it.couponState =
                            CouponState.INACTIVE.name
                        it.isCouponAmountEligible = false
                        it.validityInMillis = -1L
                    }
                    if (expiredCoupon.isSelected) {
                        removeAllCouponCode()
                    } else {
                        sortCouponListAndSetAdapter(it)
                    }
                }
            }
        )
        binding.rvCoupon.adapter = couponCodeV2Adapter
    }

    private fun showCouponRemovedSnackbar(couponCode: CouponCode) {
        val removeMessage =
            if (couponCode.getCouponType() == CouponType.WINNINGS) getCustomString(MR.strings.feature_buy_gold_v2_applied_winnings_was_removed) else getCustomString(
                MR.strings.feature_buy_gold_v2_applied_coupon_was_removed
            )
        removeMessage.snackBar(
            binding.root,
            com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
            progressColor = com.jar.app.core_ui.R.color.color_016AE1,
            duration = 2000,
            translationY = 0f
        )
    }

    private fun onCouponCodeClicked(couponCode: CouponCode) {
        if (couponCode.isSelected) {
            removeAllCouponCode()
            if (couponCode.couponCodeVariant != CouponCodeVariant.COUPON_VARIANT_TWO.name) {
                showCouponRemovedSnackbar(couponCode)
            }
        } else {
            if (viewModel.canApplyCoupon(couponCode)) {
                viewModel.applyCouponCode(
                    couponCode,
                    ScreenName.Buy_Gold_Home_Screen.name
                )
            } else {
                viewModel.getApplyCouponErrorMessage(
                    couponCode
                )?.let {
                    getCustomStringFormatted(
                        it,
                        couponCode.minimumAmount.toInt()
                    ).snackBar(
                        binding.root,
                        com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                        progressColor = com.jar.app.core_ui.R.color.color_016AE1,
                        duration = 2000,
                        translationY = 0f
                    )
                }
            }
        }
    }

    private fun onRemoveCouponClicked() {
        removeAllCouponCode()
    }

    private fun removeAllCouponCode() {
        binding.animSuccessConfetti.isVisible = false
        viewModel.deselectAllCoupons()
    }

    private fun setOnAppliedCouponUI(
        applyCouponCodeResponse: ApplyCouponCodeResponse,
        position: Int
    ) {
        if (findNavController().currentBackStackEntry?.destination?.id != R.id.couponCodeAppliedDialogFragment) {
            binding.animSuccessConfetti.isVisible = true
            binding.animSuccessConfetti.playAnimation()
            navigateTo(
                BuyGoldV2FragmentDirections.actionBuyGoldV2FragmentToCouponCodeAppliedDialogFragment(
                    CouponApplied(
                        couponCode = applyCouponCodeResponse.couponCode,
                        couponDescription = encodeUrl(
                            applyCouponCodeResponse.couponCodeDesc ?: ""
                        ),
                        couponType = applyCouponCodeResponse.couponType,
                        couponTile = applyCouponCodeResponse.title,
                        couponPosition = position
                    )
                )
            )
        }
    }

    private fun applyCouponPassedFromArgs() {
        if (isCouponCodePassedFromArgs()
            && hasAppliedCouponCodePassedFromArgsOnce.not()
            && couponTypePassedFromArgs.equals(BuyGoldV2Constants.NO_CODE, ignoreCase = true)
                .not()
            && prefilledAmountForFailedState == 0f
            && viewModel.buyAmount != 0.0f
        ) {
            uiScope.launch {
                delay(500)
                if (couponTypePassedFromArgs == CouponType.WINNINGS.name) {
                    viewModel.applyCouponCode(
                        couponCode = couponCodePassedFromArgs,
                        couponType = couponTypePassedFromArgs
                    )
                } else {
                    viewModel.couponCodeResponse?.couponCodes?.find { it.couponCode == couponCodePassedFromArgs }
                        ?.let {
                            val validityInMillis = it.validityInMillis.orZero()
                            if (validityInMillis > 0) {
                                if (it.isCouponExpired()
                                        .not() && it.getCouponState() == CouponState.ACTIVE
                                ) {
                                    viewModel.applyCouponCode(
                                        couponCode = couponCodePassedFromArgs,
                                        couponType = couponTypePassedFromArgs
                                    )
                                }
                            } else {
                                if (it.getCouponState() == CouponState.ACTIVE) {
                                    viewModel.applyCouponCode(
                                        couponCode = couponCodePassedFromArgs,
                                        couponType = couponTypePassedFromArgs
                                    )
                                }
                            }
                        }
                }
                hasAppliedCouponCodePassedFromArgsOnce = true
            }
        }
    }

    private fun setupSuggestedGoldAmountAdapter() {
        suggestedGoldAmountAdapter = SuggestedGoldAmountAdapter {
            if (it.unit != null && it.unit!!.contains(BuyGoldV2Constants.UNIT_GM)) {
                setTextInInputEditText(it.amount.volumeToStringWithoutTrailingZeros())
            } else {
                setTextInInputEditText("${it.amount.toInt()}")
            }
        }

        binding.rvSuggestedGoldAmount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvSuggestedGoldAmount.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedGoldAmount.adapter = suggestedGoldAmountAdapter
    }

    private fun setupToolbar() {
        binding.toolbar.btnBack.setImageResource(if (isFromOnboarding) com.jar.app.core_ui.R.drawable.ic_buy_gold_bricks else com.jar.app.core_ui.R.drawable.ic_arrow_back_small)
        binding.toolbar.tvTitle.isVisible = true
        binding.toolbar.lottieView.isVisible = isFromOnboarding.not()

        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.separator.isVisible = true
        binding.toolbar.tvEnd.isVisible = isFromOnboarding
        binding.toolbar.tvEnd.text = getString(com.jar.app.core_ui.R.string.core_ui_skip)

        binding.toolbar.tvTitle.text =
            getCustomString(MR.strings.feature_buy_gold_v2_save_manually)
        if (isFromOnboarding.not())
            binding.toolbar.lottieView.setAnimation(com.jar.app.core_ui.R.raw.auspicious)
    }

    private fun setupToolbarListener() {
        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BackButton_BuyGoldScreen)
            if (isFromOnboarding.not()) {
                popBackStack()
            } else {
                backPressCallback.handleOnBackPressed()
            }
        }

        binding.toolbar.lottieView.setDebounceClickListener {
            navigateTo(
                BuyGoldV2FragmentDirections.actionBuyGoldV2FragmentToAuspiciousDatesBottomSheet()
            )
            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ShubhMuhurat_BuyGoldScreen)
        }

        binding.toolbar.tvEnd.setDebounceClickListener {
            analyticsHandler.postEvent(BuyGoldV2EventKey.BuyGoldScreen_SkipClicked)
            openBuyGoldAbandonSheet()
        }
    }

    private fun openBuyGoldAbandonSheet() {
        navigateTo("android-app://com.jar.app/buyGoldAbandonScreenBottomSheet/${args.buyGoldFlowContext}")
    }

    private fun initiateBuyGoldRequest(
        buyGoldPaymentType: BuyGoldPaymentType,
        selectedUpiApp: BuyGoldUpiApp?
    ) {
        val currentGoldPrice =
            viewModel.fetchCurrentBuyPriceResponse
        var jarWinningsUsed = 0.0f
        var couponCodeId: String? = null
        var codeCoupon: String? = null
        var selectedCoupon: CouponCode? = null
        viewModel.couponCodeResponse?.couponCodes?.find { it.isSelected }?.let {
            if (it.getCouponType() == CouponType.WINNINGS) {
                jarWinningsUsed = it.getMaxRewardThatCanBeAvailed(viewModel.buyAmount)
            } else {
                couponCodeId = it.couponCodeId
                codeCoupon = it.couponCode
            }
            selectedCoupon = it
        }

        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Clicked_PayNow_OrderPreviewScreen,
            mapOf(
                BuyGoldV2EventKey.POSITION to viewModel.couponCodeResponse?.couponCodes?.indexOf(
                    selectedCoupon
                )
                    .orZero() + 1,
                BuyGoldV2EventKey.couponTitle to selectedCoupon?.title.orEmpty(),
                BuyGoldV2EventKey.couponCode to selectedCoupon?.couponCode.orEmpty(),
                BuyGoldV2EventKey.moneySavedByCoupon to selectedCoupon?.getMaxRewardThatCanBeAvailed(
                    viewModel.buyAmount
                ).orZero(),
                BuyGoldV2EventKey.couponDiscountPercentage to selectedCoupon?.rewardPercentage.orZero(),
                BuyGoldV2EventKey.isWinningsCoupon to if (selectedCoupon?.getCouponType() == CouponType.WINNINGS) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                BuyGoldV2EventKey.CouponCode to selectedCoupon?.couponCode.orEmpty(),
                BuyGoldV2EventKey.entryType to args.buyGoldFlowContext,
                BuyGoldV2EventKey.auto_select_payment to if (viewModel.lastUsedUpiApp != null) BuyGoldV2EventKey.True else BuyGoldV2EventKey.False,
                BuyGoldV2EventKey.recommended_upi to viewModel.lastUsedUpiApp?.packageName.orEmpty()
                    .getAppNameFromPkgName(requireContext().applicationContext.packageManager)
                    .orEmpty(),
                BuyGoldV2EventKey.Experiment to getExperimentName()
            )
        )

        if (viewModel.buyAmount != 0.0f && currentGoldPrice != null) {
            viewModel.buyGoldPaymentType = buyGoldPaymentType
            viewModel.selectedUpiApp = selectedUpiApp
            when (viewModel.buyGoldRequestType) {
                BuyGoldRequestType.AMOUNT -> {
                    viewModel.buyGoldByAmount(
                        BuyGoldByAmountRequest(
                            amount = viewModel.buyAmount,
                            fetchCurrentGoldPriceResponse = currentGoldPrice,
                            auspiciousTimeId = viewModel.auspiciousTimeFlow.value.data?.data?.auspiciousTimeId,
                            couponCodeId = couponCodeId,
                            couponCode = codeCoupon,
                            paymentGateway = paymentManager.getCurrentPaymentGateway(),
                            jarWinningsUsedAmount = jarWinningsUsed,
                            weeklyChallengeFlow = showWeeklyChallengeAnimation
                        )
                    )
                }

                BuyGoldRequestType.VOLUME -> {
                    viewModel.buyGoldByVolume(
                        BuyGoldByVolumeRequest(
                            volume = viewModel.buyVolume,
                            fetchCurrentGoldPriceResponse = currentGoldPrice,
                            auspiciousTimeId = viewModel.auspiciousTimeFlow.value.data?.data?.auspiciousTimeId,
                            couponCodeId = couponCodeId,
                            couponCode = codeCoupon,
                            paymentGateway = paymentManager.getCurrentPaymentGateway(),
                            jarWinningsUsedAmount = jarWinningsUsed,
                            weeklyChallengeFlow = showWeeklyChallengeAnimation
                        )
                    )
                }
            }
        }
    }

    private fun observeCurrentNetworkStateLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkFlow.networkStatus.collectLatest {
                    binding.toolbar.clNetworkContainer.isSelected = it
                    binding.toolbar.tvInternetConnectionText.text =
                        if (it) getCustomString(MR.strings.core_ui_we_are_back_online) else getCustomString(
                            MR.strings.core_ui_no_internet_available_please_try_again
                        )
                    binding.toolbar.tvInternetConnectionText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        if (it) com.jar.app.core_ui.R.drawable.ic_wifi_on else com.jar.app.core_ui.R.drawable.ic_wifi_off,
                        0,
                        0,
                        0
                    )
                    if (it) {
                        if (binding.toolbar.networkExpandableLayout.isExpanded) {
                            uiScope.launch {
                                delay(500)
                                binding.toolbar.networkExpandableLayout.collapse(true)
                            }
                        }
                    } else {
                        binding.toolbar.networkExpandableLayout.expand(true)
                    }
                }
            }
        }
    }

    private fun prefillRecommendedAmount(amount: Float) {
        viewModel.buyAmount = amount
        setTextInInputEditText("${amount.toInt()}")
        viewModel.calculateVolumeFromAmount(amount)
    }

    private fun changeInputType(inputType: BuyGoldRequestType) {
        viewModel.buyGoldRequestType = inputType
        binding.tvTabInRupees.setBackgroundResource(
            if (inputType == BuyGoldRequestType.AMOUNT) com.jar.app.core_ui.R.drawable.rounded_bg_3c3357_12dp else 0
        )
        binding.tvTabInGrams.setBackgroundResource(
            if (inputType == BuyGoldRequestType.VOLUME) com.jar.app.core_ui.R.drawable.rounded_bg_3c3357_12dp else 0
        )
        binding.tvTabInRupees.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (inputType == BuyGoldRequestType.AMOUNT) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )
        binding.tvTabInGrams.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (inputType == BuyGoldRequestType.VOLUME) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )

        binding.tvRupeeSymbol.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (inputType == BuyGoldRequestType.AMOUNT) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )
        binding.tvGramSymbol.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (inputType == BuyGoldRequestType.VOLUME) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )

        binding.tvGramSymbol.setTypeface(
            binding.tvGramSymbol.typeface,
            if (inputType == BuyGoldRequestType.VOLUME) Typeface.BOLD else Typeface.NORMAL
        )
        binding.tvRupeeSymbol.setTypeface(
            binding.tvRupeeSymbol.typeface,
            if (inputType == BuyGoldRequestType.AMOUNT) Typeface.BOLD else Typeface.NORMAL
        )

        if (inputType == BuyGoldRequestType.AMOUNT) {
            binding.etBuyGoldInput.filters = arrayOf(
                InputFilter.LengthFilter(9),
                RoundAmountToIntInputFilter(
                    shouldRoundToInt = true,
                    isInputSeparatedByComma = true
                )
            )
        } else {
            binding.etBuyGoldInput.filters =
                arrayOf(InputFilter.LengthFilter(7), DecimalDigitsInputFilter(4))
        }

        val goldInput =
            if (inputType == BuyGoldRequestType.VOLUME) viewModel.buyAmount else viewModel.buyVolume
        if (inputType == BuyGoldRequestType.AMOUNT) {
            binding.tvRupeeSymbol.text =
                getCustomString(MR.strings.feature_buy_gold_v2_rupees_symbol)
            animateDecreaseTextSize(WeakReference(binding.tvGramSymbol))
            animateIncreaseTextSize(WeakReference(binding.tvRupeeSymbol))
            viewModel.calculateAmountFromVolume(goldInput)
        } else {
            binding.tvGramSymbol.text = getCustomString(MR.strings.feature_buy_gold_v2_gm_label)
            animateIncreaseTextSize(WeakReference(binding.tvGramSymbol))
            animateDecreaseTextSize(WeakReference(binding.tvRupeeSymbol))
            viewModel.calculateVolumeFromAmount(goldInput)
        }
        suggestedAmountOptions?.let {
            if (inputType == BuyGoldRequestType.AMOUNT) suggestedGoldAmountAdapter?.submitList(
                it.options
            ) else suggestedGoldAmountAdapter?.submitList(it.volumeOptions)
        }
    }

    private fun animateDecreaseTextSize(textViewWR: WeakReference<TextView>) {
        textSizeDecreaseAnimation?.end()
        textSizeDecreaseAnimation =
            ObjectAnimator.ofFloat(
                textViewWR.get(),
                "textSize",
                MAX_TEXT_SIZE_FOR_LABEL,
                MIN_TEXT_SIZE_FOR_LABEL
            )
        textSizeDecreaseAnimation?.duration = TEXT_SIZE_ANIMATION_MILLIS
        textSizeDecreaseAnimation?.interpolator = LinearInterpolator()
        textSizeDecreaseAnimation?.start()
    }

    private fun animateIncreaseTextSize(textViewWR: WeakReference<TextView>) {
        textSizeIncreaseAnimation?.end()
        textSizeIncreaseAnimation =
            ObjectAnimator.ofFloat(
                textViewWR.get(),
                "textSize",
                MIN_TEXT_SIZE_FOR_LABEL,
                MAX_TEXT_SIZE_FOR_LABEL
            )
        textSizeIncreaseAnimation?.duration = TEXT_SIZE_ANIMATION_MILLIS
        textSizeIncreaseAnimation?.interpolator = LinearInterpolator()
        textSizeIncreaseAnimation?.start()
    }

    private fun setTextInInputEditText(
        text: String,
        shouldReattachTextWatcher: Boolean = false
    ) {
        if (binding.etBuyGoldInput.text.toString() != text) {
            if (shouldReattachTextWatcher) {
                binding.etBuyGoldInput.removeTextChangedListener(inputTextWatcher)
            }
            binding.etBuyGoldInput.setText(text)
            binding.etBuyGoldInput.setSelection(
                binding.etBuyGoldInput.text.toString().trim().length
            )
            if (shouldReattachTextWatcher) {
                binding.etBuyGoldInput.addTextChangedListener(inputTextWatcher)
            }
        }
        binding.etBuyGoldInput.gravity =
            if (viewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) Gravity.LEFT else Gravity.RIGHT
    }

    private fun checkForMinimumAmountAndVolumeValue() {
        when (viewModel.buyGoldRequestType) {
            BuyGoldRequestType.VOLUME -> {
                when {
                    viewModel.buyVolume < viewModel.getVolumeForXAmount(getMinimumBuyGoldAmount()) -> {
                        /**Subtracting 0.0001 from the minimum buy volume for error message of below limit case,
                         * For Eg : If Minimum Volume is 0.0015, then the error message should be
                         * "Please enter a value greater than 0.0014"
                         * **/
                        binding.tvErrorMessage.text =
                            getCustomStringFormatted(
                                MR.strings.feature_buy_gold_v2_please_enter_a_volume_thats_more_than_x_gm,
                                viewModel.getVolumeForXAmount(getMinimumBuyGoldAmount()) - 0.0001f
                            )
                        toggleInputState(shouldSetDisableState = true)
                    }

                    viewModel.buyVolume > viewModel.getVolumeForXAmount(remoteConfigManager.getMaximumGoldBuyAmount()) -> {
                        /**Adding 0.0001 to the maximum buy volume for error message of beyond limit case,
                         * For Eg : If Maximum Volume is 31.8282, then the error message should be
                         * "Please enter a value less than 31.8283"
                         * **/
                        binding.tvErrorMessage.text =
                            getCustomStringFormatted(
                                MR.strings.feature_buy_gold_v2_please_enter_a_volume_thats_less_than_x_gm,
                                viewModel.getVolumeForXAmount(remoteConfigManager.getMaximumGoldBuyAmount()) + 0.0001f
                            )
                        toggleInputState(shouldSetDisableState = true)
                    }

                    else -> {
                        toggleInputState(shouldSetDisableState = false)
                    }
                }
                viewModel.calculateAmountFromVolume(viewModel.buyVolume)
            }

            BuyGoldRequestType.AMOUNT -> {
                when {
                    viewModel.buyAmount < getMinimumBuyGoldAmount() -> {
                        /**Subtracting 1 from the minimum buy amount for error message of below limit case,
                         * For Eg : If Minimum Amount is 10, then the error message should be
                         * "Please enter an amount greater than 9"
                         * **/
                        binding.tvErrorMessage.text =
                            getCustomStringFormatted(
                                MR.strings.feature_buy_gold_v2_please_enter_a_amount_thats_more_than_x_rs,
                                getMinimumBuyGoldAmount().toInt() - 1
                            )
                        toggleInputState(shouldSetDisableState = true)
                    }

                    viewModel.buyAmount > remoteConfigManager.getMaximumGoldBuyAmount() -> {
                        /**Adding 1 to the maximum buy amount for error message of beyond limit case,
                         * For Eg : If Maximum Amount is 200000, then the error message should be
                         * "Please enter an amount less than 200001"
                         * **/
                        binding.tvErrorMessage.text =
                            getCustomStringFormatted(
                                MR.strings.feature_buy_gold_v2_please_enter_a_amount_thats_less_than_x_rs,
                                remoteConfigManager.getMaximumGoldBuyAmount().toInt() + 1
                            )
                        toggleInputState(shouldSetDisableState = true)
                    }

                    else -> {
                        toggleInputState(shouldSetDisableState = false)
                    }
                }
                viewModel.calculateVolumeFromAmount(viewModel.buyAmount)
            }
        }
    }

    private fun toggleInputState(
        shouldSetDisableState: Boolean,
        shouldIgnoreErrorUI: Boolean = false
    ) {
        //shouldSetDisableState -> Whether the state of CTA is to be disabled or enabled
        //shouldIgnoreErrorUI -> Flag to show error UI - can be set to true when the state is meant
        //to be disabled but the error UI is not to be shown. Eg: - In manual input flow

        binding.clBuyGoldInput.setBackgroundResource(
            if (shouldSetDisableState && shouldIgnoreErrorUI.not()) R.drawable.feature_buy_gold_v2_bg_rounded_2e2942_outline_eb6a6e_16dp else if (binding.etBuyGoldInput.hasFocus()) R.drawable.feature_buy_gold_v2_bg_input else com.jar.app.core_ui.R.drawable.core_ui_round_black_bg_16dp
        )
        binding.tvErrorMessage.isVisible = shouldSetDisableState && shouldIgnoreErrorUI.not()

        if (isNewPaymentStripVisible) {
            binding.layoutNewPaymentStrip.tvViewBreakdown.isClickable =
                shouldSetDisableState.not()
            binding.layoutNewPaymentStrip.tvViewBreakdown.isSelected =
                shouldSetDisableState.not()
            renderNewPaymentStrip(shouldSetDisableState.not())
        } else {
            binding.layoutOldPaymentStrip.llBuyNow.alpha =
                if (shouldSetDisableState) 0.5f else 1f
            binding.layoutOldPaymentStrip.llBuyNow.isClickable = shouldSetDisableState.not()
            binding.layoutOldPaymentStrip.tvViewBreakdown.isClickable =
                shouldSetDisableState.not()
            binding.layoutOldPaymentStrip.tvViewBreakdown.isSelected =
                shouldSetDisableState.not()
        }

        binding.tvManualCoupon.isClickable = shouldSetDisableState.not()
        binding.tvManualCoupon.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (shouldSetDisableState) com.jar.app.core_ui.R.color.white_30 else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )
        if (shouldSetDisableState) {
            viewModel.couponCodeResponse?.let {
                sortCouponListAndSetAdapter(it)
            }
        }
    }

    private fun getContextForAmounts(): String =
        when {
            isFromJackpotScreen -> BaseConstants.BuyGoldFlowContext.JACKPOT_SCREEN
            weeklyAmount > 0f -> BaseConstants.BuyGoldFlowContext.WEEKLY_CHALLENGE
            isFromOnboarding -> BaseConstants.BuyGoldFlowContext.BUY_GOLD
            else -> args.buyGoldFlowContext
        }

    private fun registerAdapterDataObserver() {
        adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                scrollToSelectedCouponPosition()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                scrollToSelectedCouponPosition()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                scrollToSelectedCouponPosition()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                scrollToSelectedCouponPosition()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                scrollToSelectedCouponPosition()
            }
        }
        couponCodeV2Adapter?.registerAdapterDataObserver(adapterDataObserver!!)
    }

    private fun getMinimumBuyGoldAmount() =
        suggestedAmountOptions?.firstOtpTxnMinEligibleAmount
            ?: remoteConfigManager.getMinimumGoldBuyAmount()

    private fun isCouponCodePassedFromArgs(): Boolean =
        couponCodePassedFromArgs.equals(BuyGoldV2Constants.NO_CODE, ignoreCase = true).not()

    private fun scrollToSelectedCouponPosition() {
        val selectedPosition =
            viewModel.couponCodeResponse?.couponCodes?.indexOfFirst { it.isSelected }
        if (selectedPosition != null) {
            binding.rvCoupon.post {
                if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    binding.rvCoupon.scrollToPosition(if (selectedPosition == -1) 0 else selectedPosition)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCouponCodeEnterEvent(couponCodeEnteredEvent: CouponCodeEnteredEvent) {
        EventBus.getDefault().removeStickyEvent(couponCodeEnteredEvent)
        viewModel.applyManuallyEnteredCouponCode(
            couponCodeEnteredEvent.couponCode,
            ScreenName.Buy_Gold_Home_Screen.name
        )
    }

    private fun clearFocus() {
        binding.etBuyGoldInput.clearFocus()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        glide?.clear(target)
        glide?.clear(socialProofIconTarget)
        isFirstTimeGoldFetched = true
        isCouponApplied = false
        isShownEventSynced = false
        textSizeIncreaseAnimation?.cancel()
        textSizeDecreaseAnimation?.cancel()
        suggestedGoldAmountAdapter = null
        binding.animSuccessConfetti.removeAnimatorListener(confettiAnimationListener)
        adapterDataObserver?.let {
            couponCodeV2Adapter?.unregisterAdapterDataObserver(it)
        }
        binding.etBuyGoldInput.removeTextChangedListener(inputTextWatcher)
        viewModel.resetAppliedCoupon()
        super.onDestroyView()
    }

    private fun hideCouponCodeVariant1() {
        with(binding) {
            tvExtraGoldLabel.isVisible = false
            //   shimmerCoupon.isVisible = false
            rvCoupon.isVisible = false
            tvManualCoupon.isVisible = false
        }
    }

    private fun showCouponCodeVariant1() {
        with(binding) {
            tvExtraGoldLabel.isVisible = true
            // shimmerCoupon.isVisible = true
            rvCoupon.isVisible = true
            tvManualCoupon.isVisible = true
        }
    }

    private fun getExperimentName() =
        if (viewModel.lastUsedUpiApp != null) BuyGoldV2EventKey.Zomato else ""

    private fun hideCouponCodeVariant2() {
        with(binding) {
            couponVariant2View.root.isVisible = false
            llViewDetailsContainer.isVisible = false
        }
    }

    private fun showCouponCodeVariant2() {
        with(binding) {
            couponVariant2View.root.isVisible = true
            llViewDetailsContainer.isVisible = true
        }
    }
}