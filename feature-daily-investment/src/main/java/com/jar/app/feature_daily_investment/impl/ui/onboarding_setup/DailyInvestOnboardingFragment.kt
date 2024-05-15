package com.jar.app.feature_daily_investment.impl.ui.onboarding_setup

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.base.util.setOnImeActionDoneListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.*
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.NavigationDailyInvestmentDirections
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundUp
import com.jar.app.feature_daily_investment.api.util.EventKey
import com.jar.app.feature_daily_investment.api.util.EventKey.amount
import com.jar.app.feature_daily_investment.databinding.FragmentDailyInvestOnboardingBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.DailyInvestmentOnboarding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.Onboarding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.SetupScreenV2
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.skip
import com.jar.app.feature_daily_investment.impl.ui.SuggestedAmountAdapter
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.OnboardingDailySaving
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
internal class DailyInvestOnboardingFragment :
    BaseFragment<FragmentDailyInvestOnboardingBinding>() {

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
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var appScope: CoroutineScope

    private var mandatePaymentJob: Job? = null

    private var currentAmount: Float? = null
    private var futureAmount: Float? = null
    private var isRoundOffsEnabled = false
    private var maxDSAmount = 0
    private var minDSAmount = 0
    private var bestAmount = 0.0f
    private var variantAmount = ArrayList<String>()

    private var adapter: SuggestedAmountAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    private val viewModel by viewModels<DailyInvestOnboardingFragmentViewModel> { defaultViewModelProviderFactory }


    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPress()
        }
    }

    private val animationUpdateListener = ValueAnimator.AnimatorUpdateListener {
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            when (it.animatedFraction) {
                in 0.0..0.1 -> {
                    viewModel.fetchValueInYears(currentAmount.orZero(), months = 3)
                }

                in 0.3..0.4 -> {
                    viewModel.fetchValueInYears(currentAmount.orZero(), months = 6)
                }

                in 0.6..0.7 -> {
                    viewModel.fetchValueInYears(currentAmount.orZero(), months = 12)
                }
            }
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDailyInvestOnboardingBinding
        get() = FragmentDailyInvestOnboardingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    private fun getData() {
        viewModel.fetchCurrentGoldPrice()
        viewModel.fetchSeekBarData()
        viewModel.fetchUserRoundOffDetails()
    }

    override fun setup(savedInstanceState: Bundle?) {
        prefs.setOnboardingComplete()
        setupUI()
        setupListeners()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {

        binding.lottieAnimation.playLottieWithUrlAndExceptionHandling(
            requireContext(), BaseConstants.LottieUrls.DAILY_3_6_12_COIN_STACK
        )

        binding.rvSuggestedAmounts.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        adapter = SuggestedAmountAdapter {
            binding.etBuyAmount.setText("${it.amount.toInt()}")
            binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
            analyticsHandler.postEvent(
                DailySavingsEventKey.ClickAutoAmount_DailySetupScreen,
                mapOf(amount to it.amount.toString())
            )
        }

        binding.rvSuggestedAmounts.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmounts.adapter = adapter
    }

    private fun setupListeners() {
        uiScope.launch {
            binding.root.keyboardVisibilityChanges().collectLatest { isKeyboardShowing ->
                binding.upiView.isVisible = !isKeyboardShowing
            }
        }

        binding.tvSkip.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_Button_Skip, mapOf(
                    BaseConstants.FromScreen to DailySavingsEventKey.Onboarding,
                    BaseConstants.Screen to DailyInvestmentOnboarding,
                    BaseConstants.ButtonType to skip,
                    DailySavingsEventKey.Variant_amounts to variantAmount.joinToString(",")
                        .orEmpty(),
                    DailySavingsEventKey.Best_amount to bestAmount
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
                    viewModel.fetchValueInYears(
                        it.toString().toFloatOrNull().orZero(),
                        months = getMonthsFromAnimationProgress()
                    )
                    val span = buildSpannedString {
                        append(getString(R.string.feature_daily_investment_save))
                        bold {
                            color(
                                ContextCompat.getColor(
                                    requireContext(), com.jar.app.core_ui.R.color.white
                                )
                            ) {
                                append(" \u20B9${it.toString().toInt()} ")
                            }
                        }
                        append(getString(R.string.feature_daily_investment_daily_your_gold_will_be_worth))
                    }.toSpannable()
                    binding.tvDailySavingAmountTitle.text = span
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
                            DailySavingsEventKey.FromScreen to DailySavingsEventKey.Onboarding,
                            DailySavingsEventKey.Variant_amounts to variantAmount.joinToString(",")
                                .orEmpty(),
                            DailySavingsEventKey.Best_amount to bestAmount
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
                getString(R.string.feature_daily_investment_please_enter_the_valid_amount)
                    .snackBar(binding.root)
            }
        }
    }

    private fun getMonthsFromAnimationProgress(): Int {
        return when (binding.lottieAnimation.progress) {
            in 0.0..0.3 -> 6
            in 0.3..0.6 -> 12
            in 0.6..1.0 -> 36
            else -> 12
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewModel.dsAmountInfoLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                analyticsHandler.postEvent(
                    EventKey.ShownDailySetupScreen,
                    mapOf(
                        DailySavingsEventKey.PageName to SetupScreenV2,
                        DailySavingsEventKey.FromScreen to Onboarding,
                        DailySavingsEventKey.Variant_amounts to variantAmount.joinToString(",")
                            .orEmpty(),
                        DailySavingsEventKey.Best_amount to bestAmount,
                    )
                )
                viewModel.createRvListData(it)
            }
        )

        viewModel.rVLiveData.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }


        viewModel.futureLiveData.observe(viewLifecycleOwner) {
            futureAmount = it
            viewModel.getVolumeFromAmount(it.orZero())
        }

        viewModel.volumeLiveData.observe(viewLifecycleOwner) {
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            formatter.maximumFractionDigits = 0
            formatter.minimumFractionDigits = 0
            val amount = futureAmount.orZero().toInt()
            binding.tvFutureValue.text =
                "${formatter.format(amount)} | ${it.orZero().roundUp(2)} gm"
        }

        viewModel.dsAmountInfoLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                val recommendedAmount = it.recommendedSubscriptionAmount
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
                binding.tvMaxLimit.text =
                    getString(
                        R.string.feature_daily_investment_you_can_save_upto_x_per_day,
                        it.sliderMaxValue.toInt()
                    )
            }
        )

        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
            }
        )

        viewModel.updateDailyInvestmentStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                it?.let {
                    viewModel.enableAutomaticDailySavings()
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
                        dismissProgressBar()
                        redirectToHome()
                    }
                }
            },
            onError = { dismissProgressBar() }
        )

        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                if (it.isResetRequired) {
                    if (isRoundOffsEnabled && currentAmount != null)
                        navigateTo(
                            NavigationDailyInvestmentDirections.actionToPreDailyInvestmentAutopaySetupFragment(
                                BaseConstants.DSPreAutoPayFlowType.SETUP_DS, currentAmount!!
                            )
                        )
                    else
                        setupMandate(
                            MandateWorkflowType.valueOf(
                                it.authWorkflowType ?: MandateWorkflowType.TRANSACTION.name
                            )
                        )
                } else {
                    currentAmount?.let { viewModel.enableOrUpdateDailySaving(it) }
                }
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lottieAnimation.addAnimatorUpdateListener(animationUpdateListener)
    }

    private fun setupMandate(authWorkflowType: MandateWorkflowType) {
        mandatePaymentJob?.cancel()
        mandatePaymentJob = appScope.launch(dispatcherProvider.main) {
            mandatePaymentApi.initiateMandatePayment(
                paymentPageHeaderDetails = PaymentPageHeaderDetail(
                    toolbarHeader = getString(R.string.feature_daily_savings),
                    toolbarIcon = R.drawable.feature_daily_investment_ic_daily_saving_tab,
                    title = getString(R.string.feature_daily_investment_auto_save),
                    featureFlow = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                    userLifecycle = com.jar.app.core_analytics.EventKey.UserLifecycles.Onboarding,
                    savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                    mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION,
                    bestAmount = bestAmount.toInt()
                ),
                initiateMandatePaymentRequest = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest(
                    mandateAmount = currentAmount!!,
                    authWorkflowType = authWorkflowType,
                    subscriptionType = SavingsType.DAILY_SAVINGS.name
                )
            ).collectUnwrapped(onError = { errorMessage, _ ->
                if (errorMessage.isNotBlank()) requireContext().showToast(errorMessage)
            }, onSuccess = {
                if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS)
                    viewModel.enableDailySaving(currentAmount!!)
                dailyInvestmentApi.openDailySavingSetupStatusFragment(
                    dailySavingAmount = currentAmount!!,
                    fetchAutoInvestStatusResponse = it.second,
                    mandatePaymentResultFromSDK = it.first,
                    isFromOnboarding = true,
                    flowName = OnboardingDailySaving,
                    popUpToId = R.id.dailyInvestOnboardingFragment,
                    userLifecycle = com.jar.app.core_analytics.EventKey.UserLifecycles.Onboarding
                )
            })
        }
    }

    override fun onDestroyView() {
        binding.lottieAnimation.cancelAnimation()
        binding.lottieAnimation.removeUpdateListener(animationUpdateListener)
        super.onDestroyView()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
        backPressCallback.isEnabled = true
    }


    private fun handleBackPress() {
        redirectToHome()
    }

    private fun redirectToHome() {
        navigateTo(
            BaseConstants.InternalDeepLinks.HOME,
            popUpTo = R.id.dailyInvestOnboardingFragment,
            inclusive = true
        )
    }

    override fun onDestroy() {
        mandatePaymentJob?.cancel()
        super.onDestroy()
    }
}