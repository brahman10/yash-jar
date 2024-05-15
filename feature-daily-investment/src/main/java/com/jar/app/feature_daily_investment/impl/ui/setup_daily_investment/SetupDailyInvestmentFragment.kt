package com.jar.app.feature_daily_investment.impl.ui.setup_daily_investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.keyboardVisibilityChanges
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.NavigationDailyInvestmentDirections
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.api.util.EventKey
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.data.model.DailyInvestmentSetupArguments
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentSetupDailyInvestmentBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.DS_AbandonState
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.SetupScreenV1
import com.jar.app.feature_daily_investment.impl.ui.SuggestedAmountAdapter
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SetupDailyInvestmentFragment :
    BaseFragment<FeatureDailyInvestmentFragmentSetupDailyInvestmentBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var cacheEvictionUtil: CacheEvictionUtil

    private var adapter: SuggestedAmountAdapter? = null
    private var amount: Float = 0f

    private var isRoundOffsEnabled = false
    private var maxDSAmount = 0f
    private var minDSAmount = 0f

    private val spaceItemDecoration =
        SpaceItemDecoration(3.dp, 0.dp)

    private val args: SetupDailyInvestmentFragmentArgs by navArgs()

    private val argsData by lazy {
        val decoded = decodeUrl(args.dailyInvestmentArgsData)
        serializer.decodeFromString<DailyInvestmentSetupArguments>(decoded)
    }

    private var variantAmount = ArrayList<String>()
    var bestAmount = 0.0f
    private var shouldShowCoreUiStatusBottomSheet = false

    private val viewModel by viewModels<SetupDailyInvestmentFragmentViewModel> { defaultViewModelProviderFactory }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (argsData.fromAbandonFlow || argsData.flowData.fromScreen == DailySavingConstants.ONBOARDING) {
                    navigateToHome()
                } else {
                    openAbandonConfirmationFragment()
                }
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentSetupDailyInvestmentBinding
        get() = FeatureDailyInvestmentFragmentSetupDailyInvestmentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (argsData.flowData.fromScreen == DailySavingConstants.ONBOARDING) {
            prefs.setOnboardingComplete()
        }
        if (argsData.showIntroBottomSheet) {
            openDailySavingsIntroductionBottomSheet()
        }
        super.onCreate(savedInstanceState)
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        checkForKeyboardState()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        OverScrollDecoratorHelper.setUpOverScroll(binding.root)
        binding.rvSuggestedAmounts.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = SuggestedAmountAdapter {
            val amount = it.amount
            viewModel.setSuggestedAmount(amount.toInt())
            viewModel.setAmountSource(DailySavingsEventKey.Value_From_Suggestion)
            binding.etBuyAmount.setText("${amount.toInt()}")
            binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
            analyticsHandler.postEvent(
                EventKey.ClickAutoAmount_DailySetupScreen,
                mapOf(
                    DailySavingConstants.Amount to amount.toString()
                )
            )
        }
        binding.rvSuggestedAmounts.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmounts.adapter = adapter
        viewModel.fetchBuyPrice()

        binding.btnClose.isVisible = false
        if (argsData.flowData.fromScreen == DailySavingConstants.ONBOARDING) {
            binding.toolbar.tvEnd.isVisible = true
            binding.toolbar.tvEnd.text =
                requireContext().getString(R.string.feature_daily_investment_skip)
        } else {
            binding.toolbar.root.isVisible = true
            binding.toolbar.tvTitle.text = getString(R.string.feature_daily_savings)
            binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_daily_investment_ic_daily_saving_tab)
        }
    }

    private fun setupListeners() {
        binding.etBuyAmount.doAfterTextChanged {
            val isAmountValid: Boolean
            var message = ""
            if (it.isNullOrEmpty()) {
                isAmountValid = false
                message =
                    getString(R.string.feature_daily_investment_this_field_cannot_be_left_empty)
            } else if (it.toString().toFloatOrNull().orZero() > maxDSAmount) {
                isAmountValid = false
                message = getString(
                    R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_x,
                    maxDSAmount.toInt()
                )
            } else if (it.toString().toFloatOrNull().orZero() < minDSAmount) {
                isAmountValid = false
                message = getString(
                    R.string.feature_daily_investment_min_amount_cannot_be_less_than_rs_x,
                    minDSAmount.toInt()
                )
            } else {
                isAmountValid = true
                viewModel.setDailyAmount(it.toString().toFloatOrNull().orZero())
                amount = it.toString().toFloatOrNull().orZero()
                if (amount != (viewModel.recommendedAmountFromApiLiveData.value?.toFloat().orZero())
                    && amount != (viewModel.suggestedAmountFromApiLiveData.value?.toFloat()
                        .orZero())
                ) {
                    viewModel.setAmountSource(DailySavingsEventKey.Value_From_Custom_Input)
                }
            }
            binding.btnSetDailyInvestment.setDisabled(isAmountValid.not())
            binding.tvErrorMessage.isVisible = isAmountValid.not()
            binding.tvErrorMessage.text = message
        }

        binding.etBuyAmount.setOnImeActionDoneListener {
            binding.etBuyAmount.hideKeyboard()
        }

        binding.btnSetDailyInvestment.setDebounceClickListener {
            val amountString = binding.etBuyAmount.text

            analyticsHandler.postEvent(
                EventKey.START_INVESTING_CLICKED,
                mapOf(
                    DailySavingsEventKey.FromScreen to argsData.flowData.fromScreen,
                    DailySavingsEventKey.FromSection to argsData.flowData.fromSection.toString(),
                    DailySavingsEventKey.FromCard to argsData.flowData.fromCard.toString(),
                    DailySavingsEventKey.Variant_amounts to variantAmount.joinToString(","),
                    DailySavingsEventKey.Best_amount to bestAmount,
                    DailySavingsEventKey.DailySavingAmountSource to viewModel.amountSourceLiveData.value.toString()
                )
            )
            if (!amountString.isNullOrBlank()) {
                amount = amountString.toString()?.toFloat().orZero()
                if (amount <= maxDSAmount) {
                    viewModel.isAutoPayResetRequired(amount)
                } else {
                    getString(
                        R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_x,
                        maxDSAmount
                    ).snackBar(binding.root)
                }
            } else {
                getString(R.string.feature_daily_investment_please_enter_the_valid_amount)
                    .snackBar(binding.root)
            }
        }

        binding.toolbar.tvEnd.setDebounceClickListener {
            navigateToHome()
        }

        binding.btnClose.setDebounceClickListener {
            if (argsData.fromAbandonFlow || argsData.flowData.fromScreen == DailySavingConstants.ONBOARDING)
                navigateToHome()
            else
                popBackStack()

            analyticsHandler.postEvent(
                EventKey.ClickCrossButton_DailySetupScreen
            )
        }
        binding.toolbar.btnBack.setDebounceClickListener {
            if (argsData.fromAbandonFlow || argsData.flowData.fromScreen == DailySavingConstants.ONBOARDING) {
                navigateToHome()
            } else {
                openAbandonConfirmationFragment()
            }
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewModel.dsSeekBarLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                viewModel.createRvListData(it)
            }
        )

        viewModel.rVLiveData.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }

        viewModel.dsSeekBarLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                val recommendedAmount = it.recommendedSubscriptionAmount
                maxDSAmount = it.sliderMaxValue
                minDSAmount = it.sliderMinValue
                if (binding.etBuyAmount.text.isNullOrBlank()) {
                    binding.etBuyAmount.setText(recommendedAmount.toInt().toString())
                    binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
                }
                viewModel.setRecommendedAmount(recommendedAmount.toInt())
                if (amount == 0f) {
                    amount = recommendedAmount
                }
                viewModel.setAmountSource(DailySavingsEventKey.Value_From_Recommendation)
                binding.tvMaxInvestLimit.text =
                    getString(
                        R.string.feature_daily_investment_you_can_invest_up_to_n_per_day,
                        it.sliderMaxValue.getFormattedAmount()
                    )
                variantAmount.clear()
                it.options.map {
                    variantAmount.add(it.amount.toString())
                }
                bestAmount = it.recommendedSubscriptionAmount
                analyticsHandler.postEvent(
                    EventKey.ShownDailySetupScreen,
                    mapOf(
                        DailySavingsEventKey.PageName to SetupScreenV1,
                        DailySavingsEventKey.FromScreen to if (argsData.showIntroBottomSheet) DS_AbandonState else argsData.flowData.fromScreen,
                        DailySavingsEventKey.FromSection to argsData.flowData.fromSection.orEmpty(),
                        DailySavingsEventKey.FromCard to argsData.flowData.fromCard.orEmpty(),
                        DailySavingsEventKey.Variant_amounts to variantAmount.joinToString(","),
                        DailySavingsEventKey.Best_amount to bestAmount,
                        DailySavingsEventKey.DailySavingAmountSource to viewModel.amountSourceLiveData.value.toString()
                    )
                )
            }
        )

        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                val mandateWorkflowType =
                    it.authWorkflowType?.let { MandateWorkflowType.valueOf(it) }
                        ?: run { MandateWorkflowType.PENNY_DROP }
                if (it.isResetRequired) {
                    if (isRoundOffsEnabled)
                        navigateTo(
                            NavigationDailyInvestmentDirections.actionToPreDailyInvestmentAutopaySetupFragment(
                                BaseConstants.DSPreAutoPayFlowType.SETUP_DS, amount
                            )
                        )
                    else if (remoteConfigApi.isMandateBottomSheetExperimentRunning())
                        initiateMandateFlowForCustomUI(mandateWorkflowType)
                     else
                        initiateMandateFlow(it.getFinalMandateAmount(), mandateWorkflowType)

                } else {
                    shouldShowCoreUiStatusBottomSheet = true
                    viewModel.enableOrUpdateDailySaving(amount)
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
                    if (shouldShowCoreUiStatusBottomSheet)
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
                            if (argsData.flowData.fromScreen == DailySavingConstants.ONBOARDING) {
                                if (
                                    isBindingInitialized() && lifecycle.currentState.isAtLeast(
                                        Lifecycle.State.RESUMED
                                    )
                                )
                                    navigateToHome()
                            } else {
                                popBackStack(R.id.setupDailyInvestmentFragment, inclusive = true)
                            }
                        }
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
            }
        )

        viewModel.buyPriceLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                viewModel.fetchCurrentGoldPriceResponse = it
            }
        )

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            DailySavingConstants.EXIT_DAILY_SAVING_AMOUNT_SELECTION_FLOW
        )?.observe(viewLifecycleOwner) {
            findNavController().popBackStack(
                R.id.setupDailyInvestmentFragment,
                true
            )
        }
    }

    override fun onDestroyView() {
        cacheEvictionUtil.evictHomePageCache()
        super.onDestroyView()
    }

    private fun navigateToHome() {
        navigateTo(
            uri = BaseConstants.InternalDeepLinks.HOME,
            popUpTo = R.id.setupDailyInvestmentFragment,
            inclusive = true
        )
    }

    private fun checkForKeyboardState() {
        uiScope.launch {
            binding.root.keyboardVisibilityChanges().collectLatest { isKeyboardShowing ->
                binding.tvPoweredByUpi.isVisible = !isKeyboardShowing
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    private fun openDailySavingsIntroductionBottomSheet() {
        val direction =
            SetupDailyInvestmentFragmentDirections.actionSetupDailyInvestmentFragmentToDailySavingsV2IntroductionBottomSheet()
        navigateTo(direction)
    }

    private fun openAbandonConfirmationFragment(isOnboardingFlow: Boolean = false) {
        navigateTo("android-app://com.jar.app/dailySavingsAbandonScreenBottomSheet/${R.id.setupDailyInvestmentFragment}/${isOnboardingFlow}")
    }

    private fun initiateMandateFlowForCustomUI(
        mandateWorkflowType: MandateWorkflowType
    ) {
        dailyInvestmentApi.initiateDailySavingCustomUIMandateBottomSheet(
            customMandateUiFragmentId = R.id.setupDailyInvestmentFragment,
            newDailySavingAmount = amount,
            mandateWorkflowType = mandateWorkflowType,
            flowSource = argsData.flowData.fromScreen,
            customBottomSheetDeeplink = "android-app://com.jar.app/dailyInvestMandateBottomSheet/$amount/${R.id.setupDailyInvestmentFragment}",
            popUpToId = R.id.setupDailyInvestmentFragment,
            userLifecycle = argsData.flowData.fromScreen
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
            newDailySavingAmount = amount,
            popUpToId = R.id.setupDailyInvestmentFragment,
            userLifecycle = argsData.flowData.fromScreen
        )
    }

}