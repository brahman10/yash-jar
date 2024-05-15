package com.jar.app.feature_daily_investment.impl.ui.onboarding_setup

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.CacheEvictionUtil
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.setOnImeActionDoneListener
import com.jar.app.base.util.showToast
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.keyboardVisibilityChanges
import com.jar.app.core_ui.extension.scrollToTop
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.NavigationDailyInvestmentDirections
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.api.util.EventKey
import com.jar.app.feature_daily_investment.api.util.EventKey.ShownDailySetupScreenTs
import com.jar.app.feature_daily_investment.api.util.EventKey.amount
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentCustomOnboardingVariantsBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.DailyInvestmentOnboarding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.Onboarding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.skip
import com.jar.app.feature_daily_investment.impl.ui.SuggestedAmountAdapter
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_payment.impl.ui.payment_option.PayNowSection
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class DailyInvestmentCustomOnboardingVariantsFragment :
    BaseFragment<FeatureDailyInvestmentCustomOnboardingVariantsBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var cacheEvictionUtil: CacheEvictionUtil

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefsApi: PrefsApi

    private var initiateMandatePaymentJob: Job? = null

    private var mandatePaymentJob: Job? = null

    private val args by navArgs<DailyInvestmentCustomOnboardingVariantsFragmentArgs>()

    private var currentAmount: Float? = null
    private var isRoundOffsEnabled = false
    private var maxDSAmount = 0
    private var minDSAmount = 0
    private var bestAmount = 0.0f
    private var variantAmount = ArrayList<String>()
    private var isMandateRequired: Boolean = false
    private var glide: RequestManager? = null
    private var mandateUpiApp: UpiApp? = null
    private var openMandateBottomSheet: Boolean? = null

    private var adapter: SuggestedAmountAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    private val viewModelProvider by viewModels<DailyInvestmentOnboardingVariantsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPress()
        }
    }

    private val requestListener by lazy {
        object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (args.fromScreen == com.jar.app.core_analytics.EventKey.HOME_SCREEN || args.fromScreen == BaseConstants.BuyGoldFlowContext.HAMBURGER_MENU) {
                    val isFromCache =
                        dataSource === DataSource.MEMORY_CACHE || dataSource === DataSource.DATA_DISK_CACHE
                    val currentTime = System.currentTimeMillis()
                    analyticsHandler.postEvent(
                        ShownDailySetupScreenTs,
                        mapOf(
                            com.jar.app.core_analytics.EventKey.IS_FROM_CACHE to isFromCache,
                            com.jar.app.core_analytics.EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(
                                endTimeTime = currentTime,
                                startTime = args.clickTime.toLong()
                            ),
                            DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
                        )
                    )
                }
                return false
            }
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentCustomOnboardingVariantsBinding
        get() = FeatureDailyInvestmentCustomOnboardingVariantsBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    private fun getData() {
        viewModel.fetchDailyInvestmentOnboardingFragmentData(args.version)
        viewModel.fetchUserRoundOffDetails()
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.fetchSavingSetupInfo()
        prefs.setOnboardingComplete()
        setupListeners()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.toolbar.btnBack.isVisible = args.fromOnboardingScreen.not()
        binding.toolbar.tvEnd.isVisible = args.fromOnboardingScreen
        binding.toolbar.tvEnd.text = requireContext().getString(R.string.feature_daily_investment_skip)
        binding.toolbar.tvTitle.text = requireContext().getString(R.string.feature_daily_savings)
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_daily_investment_ic_daily_saving_coin_and_calender)
        binding.toolbar.separator.isVisible = true
        glide = Glide.with(requireContext())
        glide?.load(viewModel.staticDataFlow.value.data?.data?.headerImageUrl)
            ?.listener(requestListener)
            ?.into(binding.ivHeader)
        glide?.load(viewModel.staticDataFlow.value.data?.data?.footerImageUrl)
            ?.into(binding.ivFooter)
        binding.tvHeaderTwo.text = viewModel.staticDataFlow.value.data?.data?.ETText
        viewModel.staticDataFlow.value.data?.data?.ctaText?.let {
            binding.btnSetDailyInvestment.setText(it)
        }

        binding.rvSuggestedAmounts.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        adapter = SuggestedAmountAdapter {
            binding.etBuyAmount.setText("${it.amount.toInt()}")
            binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
            analyticsHandler.postEvent(
                DailySavingsEventKey.ClickAutoAmount_DailySetupScreen,
                mapOf(amount to it.amount.toString(),
                    DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString())
            )
        }

        binding.rvSuggestedAmounts.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmounts.adapter = adapter
        if(remoteConfigApi.isFestivalCampaignEnabled()) {
            setupFestivalBanner()
        } else{
            binding.ivFestivalDSCampaign.isVisible = false
        }
    }

    private fun setupFestivalBanner() {
        val bannerUrl = remoteConfigApi.getFestivalDsScreenAsset()
        if (bannerUrl.isNotBlank()) {
            glide?.load(bannerUrl)?.into(binding.ivFestivalDSCampaign)
        } else {
            binding.ivFestivalDSCampaign.isVisible = false
        }
    }

    private fun setupOneStepDS(){
        if(shouldShowOneStepDs()){
            binding.oneStepPayment.isVisible = true
            binding.clDefaultPayment.isVisible = false
            binding.oneStepPayment.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    Column(
                    ) {
                        PayNowSection(
                            mandateUpiApp = mandateUpiApp,
                            payNowCtaText = getString(R.string.proceed),
                            appChooserText = getString(R.string.feature_daily_investment_pay_using),
                            isMandate = true,
                            onAppChooserClicked ={
                                fireClickEvent(DailySavingsEventKey.changeUPI, currentAmount)
                                openMandateBottomSheet = true
                                viewModel.isAutoPayResetRequired(currentAmount.orZero())
                            },
                            onPayNowClicked = {
                                fireClickEvent(getString(R.string.proceed), currentAmount)
                                openMandateBottomSheet = false
                                viewModel.isAutoPayResetRequired(currentAmount.orZero())
                            },
                            showPaymentSecureFooter = false,
                            bgColor = com.jar.app.core_ui.R.color.color_3C3357
                        )
                    }
                }
            }
        }else{
            binding.oneStepPayment.isVisible = false
            binding.clDefaultPayment.isVisible = true
        }
    }
    private fun setupListeners() {
        uiScope.launch {
            binding.root.keyboardVisibilityChanges().collectLatest { isKeyboardShowing ->
                if (isKeyboardShowing)
                    binding.svParent.scrollTo(0, binding.ivFooter.bottom)
                else
                    binding.svParent.scrollToTop()

            }
        }

        binding.toolbar.tvEnd.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_Button_Skip, mapOf(
                    BaseConstants.FromScreen to Onboarding,
                    BaseConstants.Screen to DailyInvestmentOnboarding,
                    BaseConstants.ButtonType to skip,
                    DailySavingsEventKey.Variant_amounts to variantAmount.joinToString(",")
                        .orEmpty(),
                    DailySavingsEventKey.Best_amount to bestAmount,
                    DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
                )
            )
            redirectToHome()
        }

        binding.etBuyAmount.doAfterTextChanged {
            try {
                val isAmountValid: Boolean
                var message = ""
                currentAmount = it?.toString()?.toFloatOrNull().orZero()
                if (it.isNullOrEmpty()) {
                    isAmountValid = false
                    message =
                        getString(R.string.feature_daily_investment_this_field_cannot_be_left_empty)
                } else if (it.toString().toInt() > maxDSAmount) {
                    isAmountValid = false
                    message = getString(
                        R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_x,
                        maxDSAmount
                    )
                } else if (it.toString().toInt() < minDSAmount) {
                    isAmountValid = false
                    message = getString(
                        R.string.feature_daily_investment_min_amount_cannot_be_less_than_rs_x,
                        minDSAmount
                    )
                } else {
                    isAmountValid = true
                }
                binding.btnSetDailyInvestment.setDisabled(isAmountValid.not())
                binding.tvErrorMessage.isVisible = isAmountValid.not()
                binding.tvErrorMessage.text = message
            } catch (ex: Exception) {

            }
        }

        binding.etBuyAmount.setOnImeActionDoneListener {
            binding.etBuyAmount.hideKeyboard()
        }

        binding.btnSetDailyInvestment.setDebounceClickListener {
            val amountString = binding.etBuyAmount.text

            if (!amountString.isNullOrBlank()) {
                val amount = amountString.toString().toFloatOrNull().orZero()
                if (amount <= maxDSAmount) {
                    analyticsHandler.postEvent(
                        EventKey.START_INVESTING_CLICKED, mapOf(
                            DailySavingsEventKey.MandateAmount to currentAmount?.toString()
                                .orEmpty(),
                            DailySavingsEventKey.FromScreen to Onboarding,
                            DailySavingsEventKey.Variant_amounts to variantAmount.joinToString(","),
                            DailySavingsEventKey.Best_amount to bestAmount,
                            DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
                        )
                    )
                    viewModel.isAutoPayResetRequired(amount)
                } else {
                    getString(
                        R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_x,
                        maxDSAmount
                    ).snackBar(binding.root)
                }
            } else {
                getString(R.string.feature_daily_investment_please_enter_the_valid_amount).snackBar(
                    binding.root
                )
            }
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            handleBackPress()
        }
    }


    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mandatePaymentApi.fetchLastUsedUpiApp(SavingsType.DAILY_SAVINGS.name)
                    .collectUnwrapped(
                        onSuccess = { upiApp ->
                            mandateUpiApp = upiApp
                            setupOneStepDS()
                        }
                    )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.amountListFlow.collect {
                    adapter?.submitList(it)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.dsAmountInfoFlow.collect(
                    onSuccess = {
                        val recommendedAmount = it.recommendedSubscriptionAmount
                        currentAmount = it.recommendedSubscriptionAmount
                        maxDSAmount = it.sliderMaxValue.toInt()
                        minDSAmount = it.sliderMinValue.toInt()
                        if (binding.etBuyAmount.text.isNullOrBlank()) {
                            binding.etBuyAmount.setText(recommendedAmount.toInt().toString())
                            binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
                        }
                        it.options.map {
                            variantAmount.add(it.amount.toString())
                        }
                        bestAmount = it.recommendedSubscriptionAmount
                        viewModel.createRvListData(it)
                        analyticsHandler.postEvent(
                            EventKey.ShownDailySetupScreen,
                            mapOf(
                                DailySavingsEventKey.PageName to if (args.version == DailySavingConstants.DailySavingVariants.V3) DailySavingsEventKey.SetupScreenV3 else DailySavingsEventKey.SetupScreenV4,
                                DailySavingsEventKey.FromSection to if (args.fromOnboardingScreen) {
                                    Onboarding
                                } else {
                                    args.fromScreen
                                },
                                DailySavingsEventKey.FromScreen to if (args.fromOnboardingScreen) {
                                    Onboarding
                                } else {
                                    args.fromScreen
                                },
                                DailySavingsEventKey.Variant_amounts to variantAmount.joinToString(","),
                                DailySavingsEventKey.Best_amount to bestAmount,
                                DailySavingsEventKey.DailySavingAmountSource to currentAmount.toString(),
                                DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
                            )
                        )
                        val xIndiansSaveText = buildSpannedString {
                            append(
                                getString(
                                    R.string.feature_daily_investment_one_crore_indians_save
                                )
                            )
                            append(" ")
                            color(
                                ContextCompat.getColor(
                                    requireContext(), com.jar.app.core_ui.R.color.color_FFDA2D
                                )
                            ) {
                                append(
                                    getString(
                                        R.string.feature_daily_investment_rs_value_int,
                                        recommendedAmount.toInt() + 10
                                    )
                                )
                            }
                            append(" ")
                            append(
                                getString(
                                    R.string.feature_daily_investment_daily_on_jar
                                )
                            )

                        }.toSpannable()
                        binding.tvXIndianSaves.text = xIndiansSaveText
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.roundOffDetailsFlow.collect(
                    onSuccess = {
                        isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isAutoPayResetRequiredFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        val mandateWorkflowType =
                            it.authWorkflowType?.let { MandateWorkflowType.valueOf(it) }
                                ?: run { MandateWorkflowType.PENNY_DROP }
                        isMandateRequired = it.isResetRequired
                        if (it.isResetRequired) {
                            if (isRoundOffsEnabled && currentAmount != null) {
                                navigateTo(
                                    NavigationDailyInvestmentDirections.actionToPreDailyInvestmentAutopaySetupFragment(
                                        BaseConstants.DSPreAutoPayFlowType.SETUP_DS, currentAmount!!
                                    )
                                )
                            }else{
                                if(shouldShowOneStepDs()){
                                    if(openMandateBottomSheet == true){
                                        initiateMandateFlowForCustomUI(mandateWorkflowType)
                                    }else{
                                        initiateOneStepDsPayment(it,mandateWorkflowType)
                                    }
                                }else{
                                    setupMandate(
                                        MandateWorkflowType.valueOf(
                                            it.authWorkflowType ?: MandateWorkflowType.TRANSACTION.name
                                        )
                                    )
                                }
                            }


                        } else {
                            if(shouldShowOneStepDs() && openMandateBottomSheet == true)
                                initiateMandateFlowForCustomUI(mandateWorkflowType)
                            else
                                currentAmount?.let { viewModel.enableOrUpdateDailySaving(it) }
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticDataFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        setupUI()
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateDailyInvestmentStatusFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        it?.let {
                            dismissProgressBar()
                            viewModel.enableAutomaticDailySavings()
                            if (isMandateRequired.not()) {
                                coreUiApi.openGenericPostActionStatusFragment(
                                    GenericPostActionStatusData(
                                        postActionStatus = PostActionStatus.ENABLED.name,
                                        header = getString(R.string.feature_daily_investment_daily_investment_setup_successfully),
                                        headerColorRes = com.jar.app.core_ui.R.color.color_1EA787,
                                        title = getString(
                                            R.string.feature_daily_investment_x_will_be_auto_saved_starting_tomorrow,
                                            it.amount.toInt()
                                        ),
                                        titleColorRes = com.jar.app.core_ui.R.color.white,
                                        imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                                        headerTextSize = 18f,
                                        titleTextSize = 16f,
                                    )
                                ) {
                                    EventBus.getDefault().post(RefreshDailySavingEvent())
                                    navigateToHome()
                                }
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }
        //Exit Flow
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            DailySavingConstants.EXIT_DAILY_SAVING_AMOUNT_SELECTION_FLOW
        )?.observe(viewLifecycleOwner) {
            navigateToHome()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.exitSurveyResponse.collect {
                    it?.let {
                        if (it) {
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent("${BaseConstants.EXIT_SURVEY_DEEPLINK}/${ExitSurveyRequestEnum.DAILY_SAVINGS.name}")
                            )
                        } else {
                            redirectToHome()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        if (args.fromOnboardingScreen.not()) {
            cacheEvictionUtil.evictHomePageCache()
        }
        glide?.clear(binding.ivHeader)
        super.onDestroyView()
    }

    private fun navigateToHome() {
        if (args.fromOnboardingScreen && args.fromScreen != BaseConstants.QuestFlowConstants.QUESTS)
            navigateTo(
                uri = BaseConstants.InternalDeepLinks.HOME,
                popUpTo = R.id.dailyInvestmentCustomOnboardingVariantsFragment,
                inclusive = true
            )
        else
            popBackStack(
                id = R.id.dailyInvestmentCustomOnboardingVariantsFragment,
                inclusive = true
            )
    }

    private fun setupMandate(authWorkflowType: MandateWorkflowType) {
        mandatePaymentJob?.cancel()
        mandatePaymentJob = appScope.launch(dispatcherProvider.main) {
            mandatePaymentApi.initiateMandatePayment(
                paymentPageHeaderDetails = PaymentPageHeaderDetail(
                    toolbarHeader = getString(R.string.feature_daily_savings),
                    toolbarIcon = R.drawable.feature_daily_investment_ic_daily_saving_tab,
                    title = getString(R.string.let_s_automate_your_saving, currentAmount?.toInt()),
                    featureFlow = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                    userLifecycle = args.fromScreen,
                    savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                    mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION,
                    bestAmount = bestAmount.toInt()
                ),
                initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                    mandateAmount = currentAmount!!,
                    authWorkflowType = authWorkflowType,
                    subscriptionType = SavingsType.DAILY_SAVINGS.name
                )
            ).collectUnwrapped(
                onLoading = {
                },
                onSuccess = {
                if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS)
                    viewModel.enableDailySaving(currentAmount!!)
                dailyInvestmentApi.openDailySavingSetupStatusFragment(
                    dailySavingAmount = currentAmount!!,
                    fetchAutoInvestStatusResponse = it.second,
                    mandatePaymentResultFromSDK = it.first,
                    isFromOnboarding = args.fromOnboardingScreen,
                    flowName = if (args.fromOnboardingScreen) DailySavingConstants.OnboardingDailySaving else DailySavingConstants.SetupDailySaving,
                    popUpToId = R.id.dailyInvestmentCustomOnboardingVariantsFragment,
                    userLifecycle = args.fromScreen
                )
            }, onError = { errorMessage, _ ->
                if (errorMessage.isNotBlank()) requireContext().showToast(errorMessage)
            })
        }
    }


    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    private fun openAbandonConfirmationFragment(isOnboardingFlow: Boolean = true) {
        fireClickEvent("Back", currentAmount)
        navigateTo("android-app://com.jar.app/dailySavingsAbandonScreenBottomSheet/${R.id.dailyInvestmentCustomOnboardingVariantsFragment}/${isOnboardingFlow}")
    }


    private fun handleBackPress() {
        if (args.fromOnboardingScreen) {
            redirectToHome()
        } else {
            viewModel.getExitSurveyData()
        }
    }

    private fun redirectToHome() {
        openAbandonConfirmationFragment()
    }

    private fun fireClickEvent(
        eventName: String, bestAmount: Float?, isViewDetailsClicked: Boolean = false
    ) {
        analyticsHandler.postEvent(
            DailySavingsEventKey.Clicked_DailySavings_Card, mapOf(
                DailySavingsEventKey.PageName to if (args.version == DailySavingConstants.DailySavingVariants.V3) DailySavingsEventKey.SetupScreenV3 else DailySavingsEventKey.SetupScreenV4,
                DailySavingsEventKey.ButtonType to eventName,
                DailySavingsEventKey.Best_amount to bestAmount.toString(),
                DailySavingsEventKey.AmountSelected to currentAmount.toString(),
                DailySavingsEventKey.Action to if (isViewDetailsClicked) DailySavingsEventKey.ViewDetails else DailySavingsEventKey.Jar,
                DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
            )
        )
    }

    private fun initiateMandateFlowForCustomUI(
        mandateWorkflowType: MandateWorkflowType
    ) {
        dailyInvestmentApi.initiateDailySavingCustomUIMandateBottomSheet(
            customMandateUiFragmentId = R.id.dailyInvestmentCustomOnboardingVariantsFragment,
            newDailySavingAmount = currentAmount.orZero(),
            mandateWorkflowType = mandateWorkflowType,
            flowSource = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
            customBottomSheetDeeplink = "android-app://com.jar.app/dailyInvestMandateBottomSheet/${currentAmount.orZero()}/${R.id.dailyInvestmentCustomOnboardingVariantsFragment}",
            popUpToId = R.id.dailyInvestmentCustomOnboardingVariantsFragment,
            userLifecycle = args.fromScreen
        )
    }

    private fun initiateOneStepDsPayment(
        autopayResetRequiredResponse: AutopayResetRequiredResponse,
        mandateWorkflowType: MandateWorkflowType
    ) {
        initiateMandatePaymentJob?.cancel()
        initiateMandatePaymentJob = appScope.launch(dispatcherProvider.main) {
            if(mandateUpiApp == null){
                initiateMandateFlow(autopayResetRequiredResponse.getFinalMandateAmount(), mandateWorkflowType)
            }else{
                mandatePaymentApi.initiateMandatePaymentWithUpiApp(
                    paymentPageHeaderDetails = PaymentPageHeaderDetail(
                        title = getString(
                            R.string.daily_investment_lets_automate_your_rs_d,
                            currentAmount?.toInt().orZero()
                        ),
                        toolbarIcon = 0,
                        featureFlow = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                        savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                        userLifecycle = args.fromScreen,
                        mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION,
                        toolbarHeader = getString(R.string.feature_daily_savings),
                    ),

                    initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                        mandateAmount = autopayResetRequiredResponse.newMandateAmount.orZero(),
                        authWorkflowType = if (autopayResetRequiredResponse.authWorkflowType != null) MandateWorkflowType.valueOf(
                            autopayResetRequiredResponse.authWorkflowType.orEmpty()
                        ) else MandateWorkflowType.TRANSACTION,
                        subscriptionType = SavingsType.DAILY_SAVINGS.name,
                    ),

                    upiApp = mandateUpiApp!!,
                    initiateMandateFragmentId = R.id.dailyInvestmentCustomOnboardingVariantsFragment
                ).collectUnwrapped(
                    onLoading = {
                    },
                    onSuccess = {
                        if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS)
                            viewModel.enableOrUpdateDailySaving(currentAmount.orZero())
                        dailyInvestmentApi.openDailySavingSetupStatusFragment(
                            dailySavingAmount = currentAmount.orZero(),
                            fetchAutoInvestStatusResponse = it.second,
                            mandatePaymentResultFromSDK = it.first,
                            isFromOnboarding = false,
                            flowName = "",
                            popUpToId = R.id.dailyInvestmentCustomOnboardingVariantsFragment,
                            userLifecycle = prefsApi.getUserLifeCycleForMandate()
                        )
                    },
                    onError = { message, error ->
                    }
                )
            }
        }
    }

    private fun initiateMandateFlow(
        mandateAmount: Float,
        mandateWorkflowType: MandateWorkflowType
    ) {
        dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
            mandateAmount = mandateAmount.orZero(),
            source = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
            authWorkflowType = mandateWorkflowType,
            newDailySavingAmount = currentAmount.orZero(),
            popUpToId = R.id.dailyInvestmentCustomOnboardingVariantsFragment,
            userLifecycle = args.fromScreen
        )
    }

    private fun shouldShowOneStepDs() = remoteConfigApi.isOneStepDSExperimentRunning() && args.fromOnboardingScreen.not()

    override fun onDestroy() {
        mandatePaymentJob?.cancel()
        super.onDestroy()
    }
}