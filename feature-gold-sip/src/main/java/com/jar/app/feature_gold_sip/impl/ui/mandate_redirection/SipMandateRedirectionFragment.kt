package com.jar.app.feature_gold_sip.impl.ui.mandate_redirection

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.RefreshGoldSipEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_gold_sip.NavigationGoldSipDirections
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipFragmentMandateRedirectionBinding
import com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.success.PostSetupSipData
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_mandate_payment.impl.util.MandateErrorCodes
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import javax.inject.Inject

@AndroidEntryPoint
internal class SipMandateRedirectionFragment :
    BaseFragment<FeatureGoldSipFragmentMandateRedirectionBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipFragmentMandateRedirectionBinding
        get() = FeatureGoldSipFragmentMandateRedirectionBinding::inflate

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var appScope: CoroutineScope

    private var animation: ObjectAnimator? = null

    private val viewModelProvider by viewModels<SipMandateRedirectionViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args: SipMandateRedirectionFragmentArgs by navArgs()
    private var repeatingTaskJob: Job? = null
    private var mandateJob: Job? = null
    private var year = 1

    companion object {
        const val SCREEN_TIMER = 5000L
    }

    private val sipSubscriptionType by lazy {
        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(args.goldSipUpdateEvent.subscriptionType)
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchBuyPrice()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
    }

    private fun setupUI() {
        binding.tvBySavingX.text = getCustomStringFormatted(
            GoldSipMR.strings.feature_gold_sip_by_saving_x_s_you_will_have,
            args.goldSipUpdateEvent.sipAmount.toInt(),
            getCustomString(sipSubscriptionType.textRes)
        )
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.buyPriceFlow.collect(
                    onSuccess = {
                        viewModel.fetchCurrentGoldPriceResponse = it
                        getUpdatedSipData()
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateGoldSipDetailsFlow.collect(
                    onSuccess = {
                        EventBus.getDefault().post(RefreshGoldSipEvent())
                        navigateTo(
                            NavigationGoldSipDirections.actionToGoldSipSetupAutopaySuccessFragment(
                                args.goldSipUpdateEvent
                            ),
                            popUpTo = R.id.sipMandateRedirectionFragment,
                            inclusive = true
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.goldYearPriceFlow.collectUnwrapped(
                    onSuccess = {
                        binding.clParentContainer.isVisible = true
                        binding.progressBar.isVisible = false
                        if (year == 2)
                            animateProgress()
                        val text = getCustomStringFormatted(
                            GoldSipMR.strings.feature_gold_sip_xf_gm_gold_in_x_year,
                            it.volume,
                            it.years
                        )
                        val spannable = SpannableString(text)
                        spannable.setSpan(
                            ForegroundColorSpan(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.jar.app.core_ui.R.color.color_EBB46A
                                )
                            ),
                            0,
                            text.length - 10,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                        binding.tvXGmGoldInXYears.text = spannable
                        binding.tvWorthX.text =
                            getCustomStringFormatted(
                                GoldSipMR.strings.feature_gold_sip_worth_x,
                                it.sipAmount.getFormattedAmount()
                            )
                    }
                )
            }
        }


    }

    private fun getUpdatedSipData() {
        repeatingTaskJob?.cancel()
        repeatingTaskJob = uiScope.launch {
            doRepeatingTask {
                if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED && animation?.isPaused != true)
                    viewModel.getGoldYearAndPrice(
                        sipSubscriptionType,
                        year++,
                        args.goldSipUpdateEvent.sipAmount
                    )
            }
        }
    }

    private fun animateProgress() {
        binding.progressHorizontal.isVisible = true
        val durationInMillis = Duration.ofMillis(SCREEN_TIMER).toMillis()

        animation = ObjectAnimator.ofInt(binding.progressHorizontal, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.start()
        animation?.doOnEnd {
            goToMandateFlow(args.goldSipUpdateEvent)
        }
    }

    private fun goToMandateFlow(goldSipUpdateEvent: com.jar.app.feature_gold_sip.shared.domain.event.GoldSipUpdateEvent) {
        mandateJob?.cancel()
        mandateJob = appScope.launch(dispatcherProvider.main) {
            mandatePaymentApi.initiateMandatePayment(
                paymentPageHeaderDetails = PaymentPageHeaderDetail(
                    toolbarHeader = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> getString(com.jar.app.core_ui.R.string.core_ui_auto_save)
                        SipSubscriptionType.MONTHLY_SIP -> MandatePaymentEventKey.SavingFrequencies.Monthly
                    },
                    title = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> getCustomStringFormatted(
                            requireContext(),
                            GoldSipMR.strings.feature_gold_sip_lets_automate_your_s_savings_of_x,
                            getCustomString(GoldSipMR.strings.feature_gold_sip_weekly),
                            goldSipUpdateEvent.sipAmount.toInt()
                        )

                        SipSubscriptionType.MONTHLY_SIP -> getCustomStringFormatted(
                            requireContext(),
                            GoldSipMR.strings.feature_gold_sip_lets_automate_your_s_savings_of_x,
                            getCustomString(GoldSipMR.strings.feature_gold_sip_monthly),
                            goldSipUpdateEvent.sipAmount.toInt()
                        )
                    },
                    toolbarIcon = com.jar.app.core_ui.R.drawable.core_ui_ic_gold_sip,
                    savingFrequency = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> MandatePaymentEventKey.SavingFrequencies.Weekly
                        SipSubscriptionType.MONTHLY_SIP -> MandatePaymentEventKey.SavingFrequencies.Monthly
                    },
                    featureFlow = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> MandatePaymentEventKey.FeatureFlows.WeeklySavingsPlan
                        SipSubscriptionType.MONTHLY_SIP -> MandatePaymentEventKey.FeatureFlows.MonthlySavingsPlan
                    },
                    userLifecycle = null,
                    mandateSavingsType = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> MandatePaymentCommonConstants.MandateStaticContentType.MONTHLY_SAVINGS_MANDATE_EDUCATION
                        SipSubscriptionType.MONTHLY_SIP -> MandatePaymentCommonConstants.MandateStaticContentType.WEEKLY_SAVINGS_MANDATE_EDUCATION
                    }
                ),
                initiateMandatePaymentRequest = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest(
                    mandateAmount = goldSipUpdateEvent.sipAmount,
                    authWorkflowType = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType.TRANSACTION,
                    subscriptionType = goldSipUpdateEvent.subscriptionType
                )
            ).collectUnwrapped(
                onSuccess = {
                    if (it.second.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS) {
                        viewModel.updateGoldSip(
                            com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails(
                                goldSipUpdateEvent.sipAmount,
                                goldSipUpdateEvent.sipDayValue,
                                goldSipUpdateEvent.subscriptionType
                            )
                        )
                    } else {
                        EventBus.getDefault().post(RefreshGoldSipEvent())
                        val mandatePaymentResultFromSDK =
                            encodeUrl(serializer.encodeToString(it.first))
                        val fetchMandatePaymentStatusResponse =
                            encodeUrl(serializer.encodeToString(it.second))
                        val postSetupSipData = encodeUrl(
                            serializer.encodeToString(
                                PostSetupSipData(
                                    sipSubscriptionType =
                                    com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(
                                        goldSipUpdateEvent.subscriptionType.uppercase()
                                    ).name,
                                    isSetupFlow = true,
                                    subscriptionDay = goldSipUpdateEvent.sipDay,
                                    sipDayValue = goldSipUpdateEvent.sipDayValue,
                                    null,
                                    args.goldSipUpdateEvent.sipAmount
                                )
                            )
                        )
                        navigateTo(
                            "android-app://com.jar.app/goldSipAutoPayPendingOrFailure/$mandatePaymentResultFromSDK/$fetchMandatePaymentStatusResponse/$postSetupSipData",
                            popUpTo = R.id.sipMandateRedirectionFragment,
                            inclusive = true
                        )
                        navigateTo(
                            NavigationGoldSipDirections.actionToGoldSipAutoPayPendingOrFailureFragment(
                                encodeUrl(serializer.encodeToString(it.first)),
                                encodeUrl(serializer.encodeToString(it.second)),
                                encodeUrl(
                                    serializer.encodeToString(
                                        PostSetupSipData(
                                            sipSubscriptionType =
                                            com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(
                                                goldSipUpdateEvent.subscriptionType.uppercase()
                                            ).name,
                                            isSetupFlow = true,
                                            subscriptionDay = goldSipUpdateEvent.sipDay,
                                            sipDayValue = goldSipUpdateEvent.sipDayValue,
                                            null,
                                            args.goldSipUpdateEvent.sipAmount
                                        )
                                    )
                                )
                            ),
                            popUpTo = R.id.sipMandateRedirectionFragment,
                            inclusive = true
                        )
                    }

                },
                onError = { message, errorCode ->
                    appScope.launch(dispatcherProvider.main) {
                        delay(500)
                        if (
                            errorCode == MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN
                            && isBindingInitialized()
                            && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
                        )
                            popBackStack()
                    }
                }
            )
        }
    }

    override fun onPause() {
        super.onPause()
        animation?.pause()
    }

    override fun onResume() {
        super.onResume()
        animation?.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        repeatingTaskJob?.cancel()
        repeatingTaskJob = null
        animation?.cancel()
    }

    override fun onDestroy() {
        mandateJob?.cancel()
        super.onDestroy()
    }
}