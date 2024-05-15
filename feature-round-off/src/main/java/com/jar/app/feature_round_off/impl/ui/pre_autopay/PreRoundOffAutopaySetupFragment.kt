package com.jar.app.feature_round_off.impl.ui.pre_autopay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentPreAutopaySetupBinding
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingType
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PreRoundOffAutopaySetupFragment :
    BaseFragment<FeatureRoundOffFragmentPreAutopaySetupBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentPreAutopaySetupBinding
        get() = FeatureRoundOffFragmentPreAutopaySetupBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var appScope: CoroutineScope

    private var mandatePaymentJob: Job? = null

    private var autoPaySetupAmount = 0f
    private var roundOffAmount: Float? = null
    private var isDailySavingsEnabled = false
    private var dailySavingAmount = 0f

    private val viewModel: PreRoundOffAutopayViewModel by viewModels()

    companion object {
        private const val PreRoundOffAutopaySetupFragment = "PreRoundOffAutopaySetupFragment"
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_back_AutopayMandate,
                    mapOf(
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.SourceFlow to "RoundOff",
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ActiveSavingsShown to isDailySavingsEnabled,
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.DailySavingsAmount to dailySavingAmount,
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateAmount to autoPaySetupAmount
                    )
                )
                isEnabled = false
                popBackStack()
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_round_off_label)
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_round_off_ic_round_off)
        binding.toolbar.separator.isVisible = true
        viewModel.fetchInitialRoundOffsData()
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
        binding.btnProceed.setDebounceClickListener {
            viewModel.isAutoPayResetRequired(roundOffAmount.orZero())
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.initialRoundOffLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                roundOffAmount =
                    it?.mandateAmount ?: remoteConfigApi.getRoundOffAmount().toFloat()
                viewModel.fetchSavingDetails()
            },
            onSuccessWithNullData = {
                roundOffAmount = remoteConfigApi.getRoundOffAmount().toFloat()
                //viewModel.fetchDailySavingStatus()
                viewModel.fetchSavingDetails()
            },
            onError = { dismissProgressBar() }
        )
        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                if (it.isResetRequired)
                    setupMandate()
                else
                    viewModel.enableAutomaticRoundOff()
            },
            onSuccessWithNullData = {
                roundOffAmount = remoteConfigApi.getRoundOffAmount().toFloat()
                //viewModel.fetchDailySavingStatus()
                viewModel.fetchSavingDetails()
            },
            onError = { dismissProgressBar() }
        )
        viewModel.managePreferenceLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                EventBus.getDefault()
                    .post(com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent())
                analyticsHandler.postEvent(RoundOffEventKey.Roundoff_AutosaveActivated_Screenshown)
                coreUiApi.openGenericPostActionStatusFragment(
                    GenericPostActionStatusData(
                        postActionStatus = PostActionStatus.ENABLED.name,
                        header = getCustomString(MR.strings.feature_round_off_auto_save_activated),
                        headerColorRes = com.jar.app.core_ui.R.color.color_1EA787,
                        title = getCustomString(MR.strings.feature_round_off_round_offs_will_be_saved_automatically),
                        titleColorRes = com.jar.app.core_ui.R.color.white,
                        imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                        headerTextSize = 18f,
                        titleTextSize = 16f
                    )
                ) {
                    EventBus.getDefault().post(GoToHomeEvent(PreRoundOffAutopaySetupFragment))
                }
            }
        )
        viewModel.savingDetails.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { },
            onSuccess = {
                dismissProgressBar()
                isDailySavingsEnabled = it.enabled!! && it.autoSaveEnabled!!
                dailySavingAmount = it.subscriptionAmount

                if (isDailySavingsEnabled) {
                    autoPaySetupAmount = roundOffAmount.orZero().plus(dailySavingAmount)
                    binding.tvDSRsX.text =
                        getCustomStringFormatted(
                            MR.strings.feature_round_off_currency_float_x,
                            dailySavingAmount
                        )
                    binding.tvUptoRORsX.text =
                        getCustomStringFormatted(
                            MR.strings.feature_round_off_upto_x,
                            roundOffAmount.orZero()
                        )
                    binding.tvActivatedOn.isVisible = it.updateDate != null
                    binding.tvActivatedOn.text =
                        getCustomStringFormatted(
                            MR.strings.feature_round_off_activated_on_s_date,
                            it.updateDate?.getDateMonthNameAndYear().orEmpty()
                        )
                    if (UserSavingType.fromString(it.savingsMetaData?.dailySavingsType) == UserSavingType.SAVINGS_GOAL) {
                        binding.tvDailySaving.text = it.savingsMetaData?.mandateText.orEmpty()
                    }
                } else {
                    autoPaySetupAmount = roundOffAmount.orZero()
                    binding.tvUptoRORsX.text = getCustomStringFormatted(
                        MR.strings.feature_round_off_upto_x,
                        roundOffAmount.orZero()
                    )
                }
                binding.tvXTotalAmount.text =
                    getCustomStringFormatted(
                        MR.strings.feature_round_off_currency_float_x,
                        autoPaySetupAmount
                    )
                binding.tvJarWillNeverDebitX.text =
                    getCustomStringFormatted(
                        MR.strings.feature_round_off_jar_will_never_debit_more_than_xf,
                        autoPaySetupAmount
                    )
                binding.groupDSDisabledROActivating.isVisible = isDailySavingsEnabled
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_AutopayMandate,
                    mapOf(
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.SourceFlow to "RoundOff",
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ActiveSavingsShown to isDailySavingsEnabled,
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.DailySavingsAmount to dailySavingAmount,
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateAmount to autoPaySetupAmount
                    )
                )
            },
            onError = { dismissProgressBar() }
        )
    }

    private fun setupMandate() {
        mandatePaymentJob?.cancel()
        mandatePaymentJob = appScope.launch(dispatcherProvider.main) {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_Proceed_AutopayMandateScreen,
                mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.SourceFlow to "RoundOff",
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ActiveSavingsShown to isDailySavingsEnabled,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.DailySavingsAmount to dailySavingAmount,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateAmount to autoPaySetupAmount
                )
            )
            mandatePaymentApi.initiateMandatePayment(
                paymentPageHeaderDetails = PaymentPageHeaderDetail(
                    toolbarHeader = getCustomString(MR.strings.feature_round_off_label),
                    title = getString(com.jar.app.core_ui.R.string.core_ui_auto_save),
                    toolbarIcon = R.drawable.feature_round_off_ic_round_off,
                    featureFlow = if (findNavController().isPresentInBackStack(R.id.selectRoundOffSaveMethodFragment)) MandatePaymentEventKey.FeatureFlows.SetupRoundoff else MandatePaymentEventKey.FeatureFlows.AutomateRoundoff,
                    userLifecycle = null,
                    savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                    mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.ROUND_OFFS_MANDATE_EDUCATION
                ),
                initiateMandatePaymentRequest = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest(
                    mandateAmount = autoPaySetupAmount,
                    authWorkflowType = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType.PENNY_DROP,
                    subscriptionType = SavingsType.ROUND_OFFS.name
                )
            ).collectUnwrapped(
                onSuccess = {
                    if (it.second.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS) {
                        EventBus.getDefault()
                            .post(com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent())
                        viewModel.enableRoundOff()
                        val encoded = encodeUrl(serializer.encodeToString(it.second))
                        navigateTo(
                            "android-app://com.jar.app/roundOffAutoPaySuccess/$encoded",
                            popUpTo = R.id.preRoundOffAutopaySetupFragment,
                            inclusive = true
                        )
                    } else {
                        EventBus.getDefault()
                            .post(com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent())
                        navigateTo(
                            PreRoundOffAutopaySetupFragmentDirections.actionPreRoundOffAutopaySetupFragmentToRoundOffAutoPayPendingOrFailureFragment(
                                encodeUrl(serializer.encodeToString(it.first)),
                                encodeUrl(serializer.encodeToString(it.second))
                            ),
                            popUpTo = R.id.preRoundOffAutopaySetupFragment,
                            inclusive = true
                        )
                        val mandatePaymentResultFromSDK =
                            encodeUrl(serializer.encodeToString(it.first))
                        val fetchMandatePaymentStatusResponse =
                            encodeUrl(serializer.encodeToString(it.second))
                        navigateTo(
                            "android-app://com.jar.app/roundOffAutoPayPendingOrFailure/$mandatePaymentResultFromSDK/$fetchMandatePaymentStatusResponse",
                            popUpTo = R.id.preRoundOffAutopaySetupFragment,
                            inclusive = true
                        )
                    }
                },
                onError = { message, errorCode ->
                    EventBus.getDefault()
                        .post(com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent())
                }
            )
        }
    }

    override fun onDestroy() {
        backPressCallback.isEnabled = false
        mandatePaymentJob?.cancel()
        super.onDestroy()
    }

}