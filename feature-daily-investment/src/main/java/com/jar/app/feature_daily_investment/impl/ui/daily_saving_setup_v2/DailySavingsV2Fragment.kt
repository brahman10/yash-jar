package com.jar.app.feature_daily_investment.impl.ui.daily_saving_setup_v2

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.FeatureFlowData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.keyboardVisibilityChanges
import com.jar.app.core_ui.extension.scrollToTop
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.NavigationDailyInvestmentDirections
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.EXIT_SURVEY_DEEPLINK
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_daily_investment.shared.domain.model.SavingsBenefits
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.api.util.EventKey
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentAmountSelectionBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
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
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingsV2Fragment :
    BaseFragment<FeatureDailyInvestmentAmountSelectionBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentAmountSelectionBinding
        get() = FeatureDailyInvestmentAmountSelectionBinding::inflate

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var cacheEvictionUtil: CacheEvictionUtil

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    private var initiateMandatePaymentJob: Job? = null

    private val args by navArgs<DailySavingsV2FragmentArgs>()

    private val featureFlowData by lazy {
        val decoded = decodeUrl(args.featureFlowData)
        serializer.decodeFromString<FeatureFlowData>(decoded)
    }

    private val viewModel by viewModels<DailySavingsV2ViewModel> { defaultViewModelProviderFactory }

    private var suggestedAmountAdapter: SuggestedAmountAdapter? = null

    private val suggestedAmountRvSpaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    private var recommendedAmount = 0f
    private var isRoundOffsEnabled = false

    private var dSAmount: Float = 0f
    private var dsTime: Int = 0
    private var jarNumber = 3
    private var goldInGrams = 0f
    private var goldValue = 0f
    private var mandateUpiApp: UpiApp? = null
    private var openMandateBottomSheet: Boolean? = null

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(AppBarData())
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupListeners()
        observeData()
        checkForKeyboardState()
        registerBackPressListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (args.showIntroBottomSheet) {
            openDailySavingsIntroductionBottomSheet()
        }
        super.onCreate(savedInstanceState)
    }

    private fun setRecommendedAmount(data: SavingSetupInfo) {

        // Shown Event for Daily Savings Setup
        val options = ""
        data.options.forEach {
            options.plus(it.amount.toString())
            options.plus(",")
        }
        analyticsHandler.postEvent(
            DailySavingsEventKey.Shown_DailySavings_Card,
            mapOf(
                DailySavingsEventKey.PageName to DailySavingsEventKey.Daily_Savings_V2_1,
                DailySavingsEventKey.Best_amount to data.recommendedSubscriptionAmount,
                DailySavingsEventKey.Variant_amounts to options,
                DailySavingsEventKey.FromScreen to if (args.fromAbandonFlow) DailySavingsEventKey.DS_AbandonState else featureFlowData.fromScreen,
                DailySavingsEventKey.FromSection to featureFlowData.fromSection.toString(),
                DailySavingsEventKey.FromCard to featureFlowData.fromCard.toString(),
                DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString(),
            )
        )
        analyticsHandler.postEvent(
            EventKey.ShownDailySetupScreen,
            mapOf(
                DailySavingsEventKey.PageName to DailySavingsEventKey.Daily_Savings_V2_1,
                DailySavingsEventKey.FromSection to featureFlowData.fromSection.toString(),
                DailySavingsEventKey.FromScreen to if (args.fromAbandonFlow) DailySavingsEventKey.DS_AbandonState else featureFlowData.fromScreen,
                DailySavingsEventKey.Variant_amounts to options,
                DailySavingsEventKey.Best_amount to data.recommendedSubscriptionAmount,
                DailySavingsEventKey.DailySavingAmountSource to viewModel.dSAmountLiveData,
                DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
            )
        )

        // Recommended Amount
        if (dSAmount == 0f) {
            setDsAmountLiveData(data.recommendedSubscriptionAmount)
            recommendedAmount = data.recommendedSubscriptionAmount
        }

        // Displaying Amount Selected in Progress Bar on the Screen
        binding.tvSeekbarAmount.text = getString(
            R.string.feature_daily_investment_add_rupee_symbol,
            dSAmount.getFormattedAmount()
        )

        if (dsTime == 0) {
            binding.jarGroup.visibility = View.VISIBLE
            setDsTimeLiveData(6)
        }

    }

    private fun setRecyclerView(suggestedRecurringAmounts: List<SuggestedRecurringAmount>) {
        binding.rvSuggestedAmount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        suggestedAmountAdapter = SuggestedAmountAdapter {
            setDsAmountLiveData(it.amount)
        }
        binding.rvSuggestedAmount.addItemDecorationIfNoneAdded(suggestedAmountRvSpaceItemDecoration)
        binding.rvSuggestedAmount.adapter = suggestedAmountAdapter
        suggestedAmountAdapter!!.submitList(suggestedRecurringAmounts)
    }

    fun setupListeners() {
        binding.btnBack.setDebounceClickListener {
            backPressCallback.handleOnBackPressed()
        }

        binding.cvSeekbarAmount.setDebounceClickListener {
            fireClickEvent("Edit Amount", recommendedAmount)
            animateToTop()
            val direction =
                DailySavingsV2FragmentDirections.actionDailySavingsV2FragmentToEditDailySavingValueBottomSheet(
                    dSAmount,
                    dsTime
                )
            navigateTo(direction)
        }

        binding.jar1.cvJarLabel.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar1_label),
                recommendedAmount,
                true
            )
            navigateToDSBreakdownScreen()
        }

        binding.jar1.tvJarLabel.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar1_label),
                recommendedAmount,
                true
            )
            setDsTimeLiveData(3)
        }

        binding.jar2.cvJarLabel.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar2_label),
                recommendedAmount,
                true
            )
            navigateToDSBreakdownScreen()
        }

        binding.jar2.tvJarLabel.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar1_label),
                recommendedAmount,
                true
            )
            setDsTimeLiveData(6)
        }

        binding.jar3.cvJarLabel.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar3_label),
                recommendedAmount,
                true
            )
            navigateToDSBreakdownScreen()
        }

        binding.jar3.tvJarLabel.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar3_label),
                recommendedAmount,
                true
            )
            setDsTimeLiveData(9)
        }

        binding.jar1.ivJar.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar1_label),
                recommendedAmount
            )
            jarNumber = 1
            setDsTimeLiveData(3)
        }

        binding.jar2.ivJar.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar2_label),
                recommendedAmount
            )
            jarNumber = 2
            setDsTimeLiveData(6)
        }

        binding.jar3.ivJar.setDebounceClickListener {
            fireClickEvent(
                getString(R.string.daily_investment_amount_selection_jar3_label),
                recommendedAmount
            )
            jarNumber = 3
            setDsTimeLiveData(9)
        }

        binding.btnProceed.setDebounceClickListener {
            viewModel.fetchUserRoundOffDetails()
            analyticsHandler.postEvent(
                DailySavingsEventKey.Clicked_DailySavings_Card,
                mapOf(
                    DailySavingsEventKey.PageName to DailySavingsEventKey.Daily_Savings_V2_1,
                    DailySavingsEventKey.ButtonType to binding.btnProceed.getText(),
                    DailySavingsEventKey.Best_amount to recommendedAmount.toString(),
                    DailySavingsEventKey.AmountSelected to dSAmount.toString(),
                    DailySavingsEventKey.FromScreen to if (args.fromAbandonFlow) DailySavingsEventKey.DS_AbandonState else featureFlowData.fromScreen,
                    DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
                )
            )
        }
    }

    private fun navigateToDSBreakdownScreen() {
        val direction =
            DailySavingsV2FragmentDirections.actionDailySavingsV2FragmentToDailySavingsBreakdown(
                dSAmount,
                dsTime
            )
        navigateTo(direction)
    }

    private fun calculateAmountOfGoldAccumulated(months: Int) {
        viewModel.calculateGoldAmount(
            dailyInvestment = viewModel.dSAmountLiveData.value?.toFloat() ?: DailySavingConstants.DEFAULT_DS_AMOUNT,
            months
        )
    }

    private fun setHeading() {

        var dsAmountFormatted = getString(
            R.string.feature_daily_investment_add_rupee_symbol,
            dSAmount.getFormattedAmount()
        )
        val spannable =
            SpannableString("If you save ₹${dSAmount.getFormattedAmount()} daily,\n your gold savings will be…")
        spannable.setSpan(
            RelativeSizeSpan(1.25f),
            12, // start
            12 + dsAmountFormatted.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(com.jar.app.core_ui.R.color.white)),
            12, // start
            12 + dsAmountFormatted.length,  // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            12, // start
            12 + dsAmountFormatted.length,  // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding.tvHeading.setText(spannable)
    }

    private fun animateToTop() {
        binding.scrollView.scrollToTop()
    }

    private fun setData(data: SavingsBenefits) {
        binding.jar1.tvJarLabel.text =
            getString(R.string.daily_investment_amount_selection_jar1_label)
        binding.jar2.tvJarLabel.text =
            getString(R.string.daily_investment_amount_selection_jar2_label)
        binding.jar3.tvJarLabel.text =
            getString(R.string.daily_investment_amount_selection_jar3_label)

        binding.jar1.tvJar3ViewLabel.text = goldInGrams.toString()
        binding.jar2.tvJar3ViewLabel.text = goldInGrams.toString()
        binding.jar3.tvJar3ViewLabel.text = goldInGrams.toString()

        binding.tv24KGold.text = data.goldHeader
        if(shouldShowOneStepDs()){
            binding.oneStepPayment.isVisible = true
            binding.btnProceed.isVisible = false
            binding.clFooter.isVisible = false
            binding.oneStepPayment.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    Column() {
                        PayNowSection(
                            mandateUpiApp = mandateUpiApp,
                            payNowCtaText = "Proceed",
                            appChooserText = "PAY USING",
                            isMandate = true,
                            onAppChooserClicked ={
                                openMandateBottomSheet = true
                                viewModel.isAutoPayResetRequired(dSAmount)
                            },
                            onPayNowClicked = {
                                openMandateBottomSheet = false
                                viewModel.isAutoPayResetRequired(dSAmount)
                            },
                            showPaymentSecureFooter = false,
                            bgColor = com.jar.app.core_ui.R.color.color_3C3357
                        )
                    }
                }
            }
        }else{
            binding.oneStepPayment.isVisible = false
            binding.btnProceed.isVisible = true
            binding.clFooter.isVisible = true
        }

    }

    // Setting DS Amount in the seekbar TV
    private fun setDSAmountVar(data: Float) {
        binding.tvSeekbarAmount.text = getString(
            R.string.feature_daily_investment_add_rupee_symbol,
            data.getFormattedAmount(0)
        )

        dSAmount = data
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.getExitSurveyData()
            }
        }

    private fun registerBackPressListener() {
        activity?.onBackPressedDispatcher?.addCallback(this, backPressCallback)
    }

    private fun checkForKeyboardState() {
        uiScope.launch {
            binding.root.keyboardVisibilityChanges().collectLatest { isKeyboardShowing ->
                if (isKeyboardShowing) binding.root.hideKeyboard()
            }
        }
    }

    // Setting DS Time Live Data
    private fun setDsTimeLiveData(months: Int) {
        viewModel.setDailySavingTime(months)
    }

    // Setting DS Amount Live Data
    private fun setDsAmountLiveData(data: Float) {
        viewModel.setDailySavingAmount(data)
    }

    private fun refreshPopupValues() {

        binding.jar1.tvJarValue.text = getString(
            R.string.feature_daily_investment_add_rupee_symbol,
            formatValue(goldValue.toInt())
        )
        binding.jar2.tvJarValue.text = getString(
            R.string.feature_daily_investment_add_rupee_symbol,
            formatValue(goldValue.toInt())
        )
        binding.jar3.tvJarValue.text = getString(
            R.string.feature_daily_investment_add_rupee_symbol,
            formatValue(goldValue.toInt())
        )

        binding.jar1.tvJar3ViewLabel.text = formatVolume(goldInGrams)
        binding.jar2.tvJar3ViewLabel.text = formatVolume(goldInGrams)
        binding.jar3.tvJar3ViewLabel.text = formatVolume(goldInGrams)
    }

    // Setting Visibility of different views based on Jar selected
    private fun setViewVisibility(jar: Int) {
        when (jar) {
            1 -> {
                Glide.with(binding.root)
                    .load(BaseConstants.ImageUrlConstants.FEATURE_DAILY_INVESTMENT_SELECTED_JAR1)
                    .into(binding.jar1.ivJar)
                binding.jar2.ivJar.setImageResource(R.drawable.feature_daily_investment_unselected_jar2)
                binding.jar3.ivJar.setImageResource(R.drawable.feature_daily_investment_unselected_jar3)

                binding.jar1.cvJarLabel.visibility = View.VISIBLE
                binding.jar2.cvJarLabel.visibility = View.INVISIBLE
                binding.jar3.cvJarLabel.visibility = View.INVISIBLE

                binding.jar1.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_dark_bg_8dp)
                binding.jar1.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.white
                    )
                )
                binding.jar2.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_light_bg_8dp)
                binding.jar2.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_776e94
                    )
                )
                binding.jar3.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_light_bg_8dp)
                binding.jar3.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_776e94
                    )
                )

                jarNumber = 1
            }

            2 -> {
                binding.jar1.ivJar.setImageResource(R.drawable.feature_daily_investment_unselected_jar1)
                binding.jar2.ivJar.setImageResource(R.drawable.feature_daily_investment_selected_jar2)
                binding.jar3.ivJar.setImageResource(R.drawable.feature_daily_investment_unselected_jar3)

                binding.jar1.cvJarLabel.visibility = View.INVISIBLE
                binding.jar2.cvJarLabel.visibility = View.VISIBLE
                binding.jar3.cvJarLabel.visibility = View.INVISIBLE

                binding.jar1.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_light_bg_8dp)
                binding.jar1.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_776e94
                    )
                )
                binding.jar2.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_dark_bg_8dp)
                binding.jar2.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.white
                    )
                )
                binding.jar3.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_light_bg_8dp)
                binding.jar3.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_776e94
                    )
                )

                jarNumber = 2

            }

            3 -> {
                binding.jar1.ivJar.setImageResource(R.drawable.feature_daily_investment_unselected_jar1)
                binding.jar2.ivJar.setImageResource(R.drawable.feature_daily_investment_unselected_jar2)
                binding.jar3.ivJar.setImageResource(R.drawable.feature_daily_investment_selected_jar3)

                binding.jar1.cvJarLabel.visibility = View.INVISIBLE
                binding.jar2.cvJarLabel.visibility = View.INVISIBLE
                binding.jar3.cvJarLabel.visibility = View.VISIBLE

                binding.jar1.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_light_bg_8dp)
                binding.jar1.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_776e94
                    )
                )
                binding.jar2.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_light_bg_8dp)
                binding.jar2.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_776e94
                    )
                )
                binding.jar3.tvJarLabel.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_dark_bg_8dp)
                binding.jar3.tvJarLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.white
                    )
                )

                jarNumber = 3
            }
        }
    }

    private fun observeData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mandatePaymentApi.fetchLastUsedUpiApp(SavingsType.DAILY_SAVINGS.name)
                    .collectUnwrapped(
                        onSuccess = { upiApp ->
                            mandateUpiApp = upiApp
                        }
                    )
            }
        }

        viewModel.dsTotalGoldLiveData.observe(viewLifecycleOwner) {
            goldInGrams = it
            refreshPopupValues()
        }

        viewModel.dsGoldValueLiveData.observe(viewLifecycleOwner) {
            goldValue = it
            refreshPopupValues()
        }

        viewModel.dSAmountLiveData.observe(viewLifecycleOwner) {
            setDSAmountVar(it.toFloat())
            setHeading()
            calculateAmountOfGoldAccumulated(dsTime / DailySavingConstants.DAYS_IN_MONTH)
        }

        viewModel.dSTimeLiveData.observe(viewLifecycleOwner) {
            dsTime = it
            when (it) {
                (DailySavingConstants.JAR_1_MONTHS * DailySavingConstants.DAYS_IN_MONTH) -> {
                    setViewVisibility(1)
                }

                (DailySavingConstants.JAR_2_MONTHS * DailySavingConstants.DAYS_IN_MONTH) -> {
                    setViewVisibility(2)
                }

                (DailySavingConstants.JAR_3_MONTHS * DailySavingConstants.DAYS_IN_MONTH) -> {
                    setViewVisibility(3)
                }
            }
            calculateAmountOfGoldAccumulated(it / DailySavingConstants.DAYS_IN_MONTH)
        }

        viewModel.amountSelectionScreenData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
            },
            onSuccess = {
                it?.savingsBenefits?.let { it1 -> setData(it1) }
            },
            onError = {
            }
        )

        viewModel.dsSeekBarLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                binding.shimmerPlaceholder.isVisible = true
                binding.shimmerPlaceholder.startShimmer()
                binding.clJarContainer.isVisible = false
                binding.cvSelectAmount.isVisible = false
            },
            onSuccess = {
                setRecommendedAmount(it)
                viewModel.createRvListData(it)
                binding.jarGroup.visibility = View.VISIBLE
                binding.shimmerPlaceholder.isVisible = false
                binding.shimmerPlaceholder.stopShimmer()
                binding.clJarContainer.isVisible = true
                binding.cvSelectAmount.isVisible = true
                if (args.featureFlowData == com.jar.app.core_analytics.EventKey.HOME_SCREEN || args.featureFlowData == BaseConstants.BuyGoldFlowContext.HAMBURGER_MENU) {
                    val currentTime = System.currentTimeMillis()
                    analyticsHandler.postEvent(
                        EventKey.ShownDailySetupScreenTs,
                        mapOf(
                            com.jar.app.core_analytics.EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(
                                endTimeTime = currentTime,
                                startTime = args.clickTime.toLong()
                            ),
                            DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString(),
                            DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
                        )
                    )
                }
            },
            onError = {
                binding.shimmerPlaceholder.isVisible = true
                binding.shimmerPlaceholder.startShimmer()
                binding.clJarContainer.isVisible = false
                binding.cvSelectAmount.isVisible = false
            }
        )

        viewModel.rVLiveData.observe(viewLifecycleOwner) {
            setRecyclerView(it)
            setupListeners()
        }


        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                viewModel.isAutoPayResetRequired(dSAmount)
                isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
            }
        )

        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                val mandateWorkflowType =
                    it.authWorkflowType?.let { MandateWorkflowType.valueOf(it) }
                        ?: run { MandateWorkflowType.PENNY_DROP }
                if (it.isResetRequired) {
                    if (isRoundOffsEnabled)
                        navigateTo(
                            NavigationDailyInvestmentDirections.actionToPreDailyInvestmentAutopaySetupFragment(
                                BaseConstants.DSPreAutoPayFlowType.SETUP_DS, dSAmount
                            )
                        )
                    else if (shouldShowOneStepDs())
                        if(openMandateBottomSheet == true) {
                            initiateMandateFlowForCustomUI(mandateWorkflowType)
                        }else{
                            initiateOneStepDsPayment(it,mandateWorkflowType)
                        }
                    else
                        initiateMandateFlow(it.getFinalMandateAmount(), mandateWorkflowType)
                } else {
                        if(shouldShowOneStepDs() && openMandateBottomSheet == true)
                            initiateMandateFlowForCustomUI(mandateWorkflowType)
                        else{
                            viewModel.enableOrUpdateDailySaving(dSAmount)
                            viewModel.enableAutomaticDailySavings()
                        }
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.updateDailyInvestmentStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                it?.let {
                    EventBus.getDefault().post(RefreshDailySavingEvent(isSetupFlow = true))
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
                            titleTextSize = 16f
                        )
                    ) {
                        EventBus.getDefault()
                            .post(
                                GoToHomeEvent(DailySavingsV2Fragment::javaClass.name)
                            )
                    }
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(
            DailySavingConstants.BREAK_DOWN_TIME_SELECTION
        )?.observe(viewLifecycleOwner) {
            val months = it / DailySavingConstants.DAYS_IN_MONTH
            setDsTimeLiveData(months)
            calculateAmountOfGoldAccumulated(months)
        }
        //Edit Amount
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Float>(
            DailySavingConstants.DAILY_SAVING_AMOUNT_EDIT
        )?.observe(viewLifecycleOwner) {
            setDsAmountLiveData(it)
            calculateAmountOfGoldAccumulated(dsTime / DailySavingConstants.DAYS_IN_MONTH)
            binding.root.hideKeyboard()
        }
        //Exit Flow
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            DailySavingConstants.EXIT_DAILY_SAVING_AMOUNT_SELECTION_FLOW
        )?.observe(viewLifecycleOwner) {
            popBackStack(
                id = R.id.dailySavingsV2Fragment,
                inclusive = true
            )
        }

        binding.root.keyboardVisibilityChanges()
            .onEach {
                if (it.not())
                    binding.scrollView.scrollToTop()
            }
            .launchIn(uiScope)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.exitSurveyResponse.collectLatest {
                    it?.let {
                        if (it) {
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent("${EXIT_SURVEY_DEEPLINK}/${ExitSurveyRequestEnum.DAILY_SAVINGS.name}")
                            )
                        } else {
                            if (args.fromAbandonFlow) {
                                EventBus.getDefault().post(
                                    GoToHomeEvent(
                                        DailySavingsV2Fragment::class.java.name,
                                        BaseConstants.HomeBottomNavigationScreen.HOME
                                    )
                                )
                            } else {
                                openAbandonConfirmationFragment()
                            }
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.loading.collect {
                        if (it) {
                            showProgressBar()
                        } else {
                            dismissProgressBar()
                        }
                    }
                }
            }
        }
    }
        override fun onDestroyView() {
            cacheEvictionUtil.evictHomePageCache()
            super.onDestroyView()
        }

    private fun fireClickEvent(
        eventName: String,
        bestAmount: Float?,
        isViewDetailsClicked: Boolean = false
    ) {
        analyticsHandler.postEvent(
            DailySavingsEventKey.Clicked_DailySavings_Card,
            mapOf(
                DailySavingsEventKey.PageName to DailySavingsEventKey.Daily_Savings_V2_1,
                DailySavingsEventKey.ButtonType to eventName,
                DailySavingsEventKey.Best_amount to bestAmount.toString(),
                DailySavingsEventKey.AmountSelected to dSAmount.toString(),
                DailySavingsEventKey.Action to if (isViewDetailsClicked) DailySavingsEventKey.ViewDetails else DailySavingsEventKey.Jar,
                DailySavingsEventKey.FromScreen to if (args.fromAbandonFlow) DailySavingsEventKey.DS_AbandonState else featureFlowData.fromScreen,
                DailySavingsEventKey.isOneStepSetup to shouldShowOneStepDs().toString()
            )
        )
    }

        private fun openAbandonConfirmationFragment(isOnboardingFlow: Boolean = false) {
            fireClickEvent("Back", recommendedAmount)
            navigateTo("android-app://com.jar.app/dailySavingsAbandonScreenBottomSheet/${R.id.dailySavingsV2Fragment}/${isOnboardingFlow}")
        }

        private fun openDailySavingsIntroductionBottomSheet() {
            val direction =
                DailySavingsV2FragmentDirections.actionDailySavingsV2FragmentToDailySavingsV2IntroductionBottomSheet()
            navigateTo(direction)
        }

        private fun navigateToHomeScreen() {
            navigateTo(
                uri = BaseConstants.InternalDeepLinks.HOME,
                popUpTo = R.id.dailySavingsV2Fragment
            )
        }

        private fun initiateMandateFlowForCustomUI(
            mandateWorkflowType: MandateWorkflowType
        ) {
            dailyInvestmentApi.initiateDailySavingCustomUIMandateBottomSheet(
                customMandateUiFragmentId = R.id.dailySavingsV2Fragment,
                newDailySavingAmount = dSAmount,
                mandateWorkflowType = mandateWorkflowType,
                flowSource = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                customBottomSheetDeeplink = "android-app://com.jar.app/dailyInvestMandateBottomSheet/$dSAmount/${R.id.dailySavingsV2Fragment}",
                popUpToId = R.id.dailySavingsV2Fragment,
                userLifecycle = featureFlowData.fromScreen
            )
        }

        private fun initiateMandateFlow(
            mandateAmount: Float,
            mandateWorkflowType: MandateWorkflowType
        ) {
            dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                mandateAmount = mandateAmount.orZero(),
                source = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                authWorkflowType = mandateWorkflowType,
                newDailySavingAmount = dSAmount,
                popUpToId = R.id.dailySavingsV2Fragment,
                userLifecycle = featureFlowData.fromScreen
            )
        }

        private fun initiateOneStepDsPayment(
            autopayResetRequiredResponse: AutopayResetRequiredResponse,
            mandateWorkflowType: MandateWorkflowType
        ) {
            initiateMandatePaymentJob?.cancel()
            initiateMandatePaymentJob = appScope.launch(dispatcherProvider.main) {
                if (mandateUpiApp == null) {
                    initiateMandateFlow(
                        autopayResetRequiredResponse.getFinalMandateAmount(),
                        mandateWorkflowType
                    )
                } else {
                    mandatePaymentApi.initiateMandatePaymentWithUpiApp(
                        paymentPageHeaderDetails = PaymentPageHeaderDetail(
                            title = getString(
                                R.string.daily_investment_lets_automate_your_rs_d,
                                dSAmount
                            ),
                            toolbarIcon = 0,
                            featureFlow = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                            savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                            userLifecycle = featureFlowData.fromScreen,
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
                        initiateMandateFragmentId = R.id.dailySavingsV2Fragment
                    ).collectUnwrapped(onSuccess = {
                        if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS)
                            viewModel.enableOrUpdateDailySaving(dSAmount)
                        dailyInvestmentApi.openDailySavingSetupStatusFragment(
                            dailySavingAmount = dSAmount,
                            fetchAutoInvestStatusResponse = it.second,
                            mandatePaymentResultFromSDK = it.first,
                            isFromOnboarding = false,
                            flowName = "",
                            popUpToId = R.id.dailySavingsV2Fragment,
                            userLifecycle = prefsApi.getUserLifeCycleForMandate()
                        )
                    }, onError = { errorMessage, errorCode ->
                        if (errorCode == com.jar.app.feature_mandate_payment.impl.util.MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                        } else if (errorMessage.isNotEmpty())
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show()
                    })
                }
            }
        }

        private fun shouldShowOneStepDs() = remoteConfigApi.isOneStepDSExperimentRunning()

}