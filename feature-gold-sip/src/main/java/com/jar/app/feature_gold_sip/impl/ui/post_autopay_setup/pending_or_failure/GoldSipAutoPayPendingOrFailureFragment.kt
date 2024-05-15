package com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.pending_or_failure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.RefreshGoldSipEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_gold_sip.NavigationGoldSipDirections
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipFragmentAutopayPendingOrFailureBinding
import com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.success.PostSetupSipData
import com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldSipAutoPayPendingOrFailureFragment :
    BaseFragment<FeatureGoldSipFragmentAutopayPendingOrFailureBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipFragmentAutopayPendingOrFailureBinding
        get() = FeatureGoldSipFragmentAutopayPendingOrFailureBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var appScope: CoroutineScope

    private var mandatePaymentJob: Job? = null

    private val args: GoldSipAutoPayPendingOrFailureFragmentArgs by navArgs()

    private val viewModelProvider by viewModels<GoldSipAutoPayPendingOrFailureViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val mandatePaymentResultFromSDK by lazy {
        val decoded = decodeUrl(args.mandatePaymentResultFromSDK)
        serializer.decodeFromString<com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK>(decoded)
    }

    private var fetchMandatePaymentStatusResponse: com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse? = null

    private val postSetupSipData by lazy {
        val decoded = decodeUrl(args.postSetupSipData)
        serializer.decodeFromString<PostSetupSipData>(decoded)
    }

    private val sipSubscriptionType by lazy {
        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(postSetupSipData.sipSubscriptionType)
    }

    companion object {
        const val GoldSipAutoPayPendingOrFailureFragment =
            "GoldSipAutoPayPendingOrFailureFragment"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        val decoded = decodeUrl(args.fetchMandatePaymentStatusResponse)
        fetchMandatePaymentStatusResponse = serializer.decodeFromString<com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse>(decoded)
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        dismissProgressBar()
        binding.tvTitle.text = fetchMandatePaymentStatusResponse?.title
        binding.tvDescription.text = fetchMandatePaymentStatusResponse?.description
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.PROCESSING_RUPEE
        )

        binding.btnGoBackToSettings.setText(
            if (postSetupSipData.isSetupFlow) getString(com.jar.app.core_ui.R.string.core_ui_go_to_home)
            else getString(com.jar.app.core_ui.R.string.core_ui_go_back_to_settings)
        )
        setViewsAccordingToStatus()

        viewModel.fireSipAutoPayPendingOrFailureEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PostSetupScreen,
            mapOf(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupStatus to fetchMandatePaymentStatusResponse?.getAutoInvestStatus()?.name.orEmpty(),
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                    sipSubscriptionType.textRes
                ),
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to fetchMandatePaymentStatusResponse?.recurringAmount.orZero(),
            )
        )
    }

    private fun setViewsAccordingToStatus() {
        if (fetchMandatePaymentStatusResponse?.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.FAILURE) {
            binding.btnRetryOrRefresh.setText(getString(com.jar.app.core_ui.R.string.retry))
            if (postSetupSipData.isSetupFlow) {
                binding.tvTitle.text =
                    getCustomStringFormatted(
                        GoldSipMR.strings.feature_gold_sip_uh_ho_s_gold_sip_setup_failed,
                        getCustomString(sipSubscriptionType.textRes)
                    )
                binding.tvDescription.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_if_your_money_was_debited)
            } else {
                binding.tvTitle.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_uh_oh_gold_sip_not_updated)
                binding.tvDescription.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_as_soon_as_we_have_update)
            }
        } else {
            binding.tvTitle.text =
                getCustomString(GoldSipMR.strings.feature_gold_sip_hmm_this_is_taking_a_bit_longer)
            binding.tvDescription.text =
                getCustomString(GoldSipMR.strings.feature_gold_sip_as_soon_we_have_an_update_we_will_let_you_know)
            binding.btnRetryOrRefresh.setText(getString(com.jar.app.core_ui.R.string.feature_buy_gold_refresh))
        }
    }

    private fun setupListener() {
        binding.btnRetryOrRefresh.setDebounceClickListener {
            if (fetchMandatePaymentStatusResponse?.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.FAILURE) {
                viewModel.fireSipAutoPayPendingOrFailureEvent(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PostSetupScreen,
                    mapOf(
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Retry,
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupStatus to fetchMandatePaymentStatusResponse?.getAutoInvestStatus()?.name.orEmpty(),
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                            sipSubscriptionType.textRes
                        ),
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to fetchMandatePaymentStatusResponse?.recurringAmount.orZero(),
                    )
                )
                reInitiateMandatePayment()
            } else {
                viewModel.fireSipAutoPayPendingOrFailureEvent(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PostSetupScreen,
                    mapOf(
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Refresh,
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupStatus to fetchMandatePaymentStatusResponse?.getAutoInvestStatus()?.name.orEmpty(),
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                            sipSubscriptionType.textRes
                        ),
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to fetchMandatePaymentStatusResponse?.recurringAmount.orZero(),
                    )
                )
                viewModel.fetchAutoInvestStatus(mandatePaymentResultFromSDK)
            }
        }

        binding.btnGoBackToSettings.setDebounceClickListener {
            viewModel.fireSipAutoPayPendingOrFailureEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PostSetupScreen,
                mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to if (postSetupSipData.isSetupFlow) com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Homepage else com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.GoBackToSettings,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupStatus to fetchMandatePaymentStatusResponse?.getAutoInvestStatus()?.name.orEmpty(),
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                        sipSubscriptionType.textRes
                    ),
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to fetchMandatePaymentStatusResponse?.recurringAmount.orZero(),
                )
            )
            EventBus.getDefault().post(
                GoToHomeEvent(
                    GoldSipAutoPayPendingOrFailureFragment,
                    if (postSetupSipData.isSetupFlow) BaseConstants.HomeBottomNavigationScreen.HOME else BaseConstants.HomeBottomNavigationScreen.PROFILE
                )
            )
        }

        binding.tvContactSupport.setDebounceClickListener {
            viewModel.fireSipAutoPayPendingOrFailureEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PostSetupScreen,
                mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Contact_Support,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupStatus to fetchMandatePaymentStatusResponse?.getAutoInvestStatus()?.name.orEmpty(),
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                        sipSubscriptionType.textRes
                    ),
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to fetchMandatePaymentStatusResponse?.recurringAmount.orZero(),
                )
            )
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomString(if (postSetupSipData.isSetupFlow) GoldSipMR.strings.feature_gold_sip_hi_i_am_facing_some_issue_in_setting_up_my_gold_sip else GoldSipMR.strings.feature_gold_sip_hi_i_am_facing_some_issue_in_updating_my_gold_sip_details)
            )
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.mandateStatusFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        if (it?.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS)
                            navigateTo(
                                GoldSipAutoPayPendingOrFailureFragmentDirections.actionGoldSipAutoPayPendingOrFailureFragmentToGoldSipAutoPaySuccessFragment(
                                    encodeUrl(
                                        serializer.encodeToString(
                                            fetchMandatePaymentStatusResponse
                                        )
                                    ),
                                    encodeUrl(
                                        serializer.encodeToString(
                                            PostSetupSipData(
                                                sipSubscriptionType = sipSubscriptionType.name,
                                                isSetupFlow = postSetupSipData.isSetupFlow,
                                                subscriptionDay = postSetupSipData?.subscriptionDay.orEmpty(),
                                                sipDayValue = postSetupSipData?.sipDayValue.orZero(),
                                                null,
                                                postSetupSipData?.sipAmount.orZero()
                                            )
                                        )
                                    )
                                )
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
                viewModel.updateGoldSipDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = { updatedSipDetails ->
                        EventBus.getDefault().post(RefreshGoldSipEvent())
                        fetchMandatePaymentStatusResponse?.let {
                            navigateTo(
                                NavigationGoldSipDirections.actionToGoldSipAutoPaySuccessFragment(
                                    encodeUrl(serializer.encodeToString(it)),
                                    encodeUrl(
                                        serializer.encodeToString(
                                            PostSetupSipData(
                                                sipSubscriptionType = sipSubscriptionType.name,
                                                isSetupFlow = postSetupSipData.isSetupFlow,
                                                subscriptionDay = postSetupSipData.subscriptionDay,
                                                sipDayValue = postSetupSipData.sipDayValue,
                                                nextDeductionDate = updatedSipDetails.nextDeductionDate?.epochToDate()
                                                    ?.getFormattedDate("d MMM''yy").orEmpty(),
                                                sipAmount = postSetupSipData?.sipAmount.orZero()
                                            )
                                        )
                                    )
                                ),
                                popUpTo = R.id.goldSipTypeSelectionFragment,
                                inclusive = true
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun reInitiateMandatePayment() {
        mandatePaymentJob?.cancel()
        mandatePaymentJob = appScope.launch(dispatcherProvider.main) {
            mandatePaymentApi.initiateMandatePayment(
                paymentPageHeaderDetails = PaymentPageHeaderDetail(
                    toolbarHeader = getCustomString(GoldSipMR.strings.feature_gold_sip_label),
                    title = getString(com.jar.app.core_ui.R.string.core_ui_auto_save),
                    toolbarIcon = com.jar.app.core_ui.R.drawable.core_ui_ic_gold_sip,
                    featureFlow = MandatePaymentEventKey.FeatureFlows.RetrySetupSavingsPlan,
                    savingFrequency = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> MandatePaymentEventKey.SavingFrequencies.Weekly
                        SipSubscriptionType.MONTHLY_SIP -> MandatePaymentEventKey.SavingFrequencies.Monthly
                    },
                    userLifecycle = null,
                    mandateSavingsType = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> MandatePaymentCommonConstants.MandateStaticContentType.MONTHLY_SAVINGS_MANDATE_EDUCATION
                        SipSubscriptionType.MONTHLY_SIP -> MandatePaymentCommonConstants.MandateStaticContentType.WEEKLY_SAVINGS_MANDATE_EDUCATION
                    }
                ),
                initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                    mandateAmount = postSetupSipData?.sipAmount.orZero(),
                    authWorkflowType = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType.PENNY_DROP,
                    subscriptionType = sipSubscriptionType.name
                )
            ).collectUnwrapped(
                onSuccess = {
                    if (it.second.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS) {
                        EventBus.getDefault().post(RefreshGoldSipEvent())
                        fetchMandatePaymentStatusResponse = it.second
                        viewModel.updateGoldSip(
                            com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails(
                                postSetupSipData?.sipAmount.orZero(),
                                postSetupSipData?.sipDayValue.orZero(),
                                sipSubscriptionType.name
                            )
                        )
                    } else {
                        val mandatePaymentResultFromSDK =
                            encodeUrl(serializer.encodeToString(it.first))
                        val fetchMandatePaymentStatusResponse =
                            encodeUrl(serializer.encodeToString(it.second))
                        val postSetupSipData = encodeUrl(
                            serializer.encodeToString(
                                PostSetupSipData(
                                    sipSubscriptionType = sipSubscriptionType.name,
                                    isSetupFlow = postSetupSipData.isSetupFlow,
                                    subscriptionDay = postSetupSipData.subscriptionDay,
                                    sipDayValue = postSetupSipData.sipDayValue,
                                    nextDeductionDate = postSetupSipData?.nextDeductionDate,
                                    sipAmount = postSetupSipData?.sipAmount.orZero()
                                )
                            )
                        )

                        navigateTo(
                            "android-app://com.jar.app/goldSipAutoPayPendingOrFailure/$mandatePaymentResultFromSDK/$fetchMandatePaymentStatusResponse/$postSetupSipData",
                            popUpTo = R.id.sipMandateRedirectionFragment,
                            inclusive = true
                        )
                    }
                },
                onError = { message, errorCode -> }
            )
        }
    }

    override fun onDestroy() {
        mandatePaymentJob?.cancel()
        super.onDestroy()
    }
}