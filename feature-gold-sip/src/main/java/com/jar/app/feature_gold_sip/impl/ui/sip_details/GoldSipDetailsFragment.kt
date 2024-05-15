package com.jar.app.feature_gold_sip.impl.ui.sip_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.base.data.event.RefreshGoldSipEvent
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.feature_gold_sip.NavigationGoldSipDirections
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipFragmentSipDetailsBinding
import com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.success.PostSetupSipData
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.base.data.model.PauseSavingOption
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_gold_sip.impl.ui.disable_sip.DisableSipEvent
import com.jar.app.feature_user_api.domain.model.PauseStatusData
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType
import com.jar.app.feature_gold_sip.shared.util.WeekGenerator
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldSipDetailsFragment : BaseFragment<FeatureGoldSipFragmentSipDetailsBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipFragmentSipDetailsBinding
        get() = FeatureGoldSipFragmentSipDetailsBinding::inflate

    @Inject
    lateinit var weekGenerator: WeekGenerator

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var appScope: CoroutineScope

    private var mandatePaymentJob: Job? = null

    private val viewModelProvider by viewModels<GoldSipDetailsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val sipDetailsAdapter = LabelAndValueAdapter()
    private val autoSaveDetailsAdapter = LabelAndValueAdapter()
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 6.dp)
    private var sipSubscriptionType: SipSubscriptionType? = null
    private var activityRef: WeakReference<FragmentActivity>? = null
    private var updatedSipDay: String? = null
    private var updatedSipDayValue: Int? = null
    private var sipAmount: Float = 0f
    private var sipDate = ""
    private var pauseStatusData: PauseStatusData? = null
    private var fetchMandatePaymentStatusResponse: com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse? =
        null

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        viewModel.fetchGoldSipDetails()
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupToolbar()
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun getData() {
        activityRef = WeakReference(requireActivity())
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(GoldSipMR.strings.feature_gold_sip_label)
        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.separator.isVisible = true
    }

    private fun setupUI() {
        binding.rvSipDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSipDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSipDetails.adapter = sipDetailsAdapter
        binding.rvAutoSaveDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAutoSaveDetails.adapter = autoSaveDetailsAdapter
        binding.rvAutoSaveDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setDebounceClickListener{
            popBackStack()
        }
        binding.btnUpdateGoldSip.setDebounceClickListener {
            sipSubscriptionType?.let {
                if (pauseStatusData != null && pauseStatusData?.savingsPaused.orFalse())
                    viewModel.resumeSip()
                else {
                    viewModel.fireGoldSipDetailEvent(
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPSettingsScreen,
                        mutableMapOf(
                            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Update,
                            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                                it.textRes
                            ),
                            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Date to sipDate,
                            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to sipAmount,
                        )
                    )
                    navigateTo(
                        GoldSipDetailsFragmentDirections.actionGoldSipDetailsFragmentToUpdateSipBottomSheet(
                            it
                        )
                    )
                }
            }
        }

        binding.tvDisableGoldSip.setDebounceClickListener {
            sipSubscriptionType?.let {
                viewModel.fireGoldSipDetailEvent(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPSettingsScreen,
                    mutableMapOf(
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Disable,
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                            it.textRes
                        ),
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Date to sipDate,
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to sipAmount
                    )
                )
                navigateTo(
                    GoldSipDetailsFragmentDirections.actionGoldSipDetailsFragmentToDisableSipBottomSheet(
                        it, pauseStatusData?.savingsPaused.orFalse()
                    )
                )
            }
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.goldSipDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        it?.let {
                            dismissProgressBar()
                            it.subscriptionType?.let {
                                sipSubscriptionType =
                                    SipSubscriptionType.valueOf(
                                        it.uppercase()
                                    )
                                binding.tvWeeklyOrMonthly.text =
                                    getCustomString(sipSubscriptionType!!.textRes)
                            }
                            pauseStatusData = it.pauseStatus
                            sipAmount = it.subscriptionAmount
                            binding.btnUpdateGoldSip.setText(getCustomString(GoldSipMR.strings.feature_gold_sip_update_gold_sip))
                            sipDate = it.updateDate?.epochToDate()
                                ?.getFormattedDate("d MMM''yy").orEmpty()
                            createSipDetailsList(it)
                            createAutoSaveDetailsList(it)
                            checkIsPausedAndSetViews(it)
                            viewModel.fireGoldSipDetailEvent(
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPSettingsScreen,
                                mutableMapOf(
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown,
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                                        sipSubscriptionType?.textRes
                                            ?: SipSubscriptionType.WEEKLY_SIP.textRes
                                    ),
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to it.subscriptionAmount,
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Date to sipDate,
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.AutopayUPI to it.upiId.orEmpty(),
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.BankAccount to it.bankName.orEmpty()
                                )
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sipPausedFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.fireGoldSipDetailEvent(
                            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPSettingsScreen,
                            mutableMapOf(
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Resume,
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                                    sipSubscriptionType!!.textRes
                                ),
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Date to sipDate,
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to sipAmount,
                            )
                        )
                        EventBus.getDefault().post(RefreshGoldSipEvent())
                        coreUiApi.openGenericPostActionStatusFragment(
                            GenericPostActionStatusData(
                                postActionStatus = PostActionStatus.RESUMED.name,
                                header = getCustomString(GoldSipMR.strings.feature_gold_sip_yay_gold_sip_has_been_resumed_successfully),
                                description = getCustomString(GoldSipMR.strings.feature_gold_sip_your_saving_will_continue_from_tomorrow),
                                title = null,
                                imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                                headerTextSize = 20f,
                            )
                        ) {
                            viewModel.fireGoldSipDetailEvent(
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPUpdatedSuccessfully,
                                null
                            )
                            viewModel.fetchGoldSipDetails()
                        }
                    },
                    onError = { _, _ -> dismissProgressBar() },
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateGoldSipDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = { updatedSipDetails ->
                        dismissProgressBar()
                        EventBus.getDefault().post(RefreshGoldSipEvent())
                        fetchMandatePaymentStatusResponse?.let {
                            navigateTo(
                                NavigationGoldSipDirections.actionToGoldSipAutoPaySuccessFragment(
                                    encodeUrl(serializer.encodeToString(it)),
                                    encodeUrl(
                                        serializer.encodeToString(
                                            PostSetupSipData(
                                                sipSubscriptionType = sipSubscriptionType!!.name,
                                                isSetupFlow = false,
                                                subscriptionDay = updatedSipDay.orEmpty(),
                                                sipDayValue = updatedSipDayValue.orZero(),
                                                nextDeductionDate = updatedSipDetails.nextDeductionDate?.epochToDate()
                                                    ?.getFormattedDate("d MMM''yy").orEmpty(),
                                                sipAmount = sipAmount
                                            )
                                        )
                                    )
                                ),
                                popUpTo = R.id.goldSipDetailsFragment,
                                inclusive = true
                            )
                        }
                    },
                    onError = { _, _ -> dismissProgressBar() }
                )
            }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.PAUSE_SIP
        )?.observe(viewLifecycleOwner) {
            EventBus.getDefault().post(RefreshGoldSipEvent())
            viewModel.fetchGoldSipDetails()
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.UPDATE_SIP_BOTTOM_SHEET_CLOSED
        )?.observe(viewLifecycleOwner) {
            viewModel.fetchGoldSipDetails()
        }

    }

    private fun createSipDetailsList(userGoldSipDetails: UserGoldSipDetails) {
        binding.tvGoldSipSetupDate.text =
            getCustomStringFormatted(GoldSipMR.strings.feature_gold_sip_was_setup_on, sipDate)
        val nextDeductionDate =
            userGoldSipDetails.nextDeductionDate?.epochToDate()?.getFormattedDate("d MMM''yy")
                .orEmpty()
        val list = ArrayList<LabelAndValue>()
        if (userGoldSipDetails.provider.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getCustomString(GoldSipMR.strings.feature_gold_sip_amount),
                    getString(
                        com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                        userGoldSipDetails.subscriptionAmount.toInt()
                    ),
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        sipSubscriptionType?.textRes?.let {
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_frequency),
                    getCustomString(it),
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        }
        list.add(
            LabelAndValue(
                getCustomString(GoldSipMR.strings.feature_gold_sip_day),
                when (SipSubscriptionType.valueOf(
                    userGoldSipDetails.subscriptionType?.uppercase()
                        ?: SipSubscriptionType.WEEKLY_SIP.name
                )) {
                    SipSubscriptionType.WEEKLY_SIP -> getCustomString(
                        weekGenerator.getWeekFromDay(userGoldSipDetails.subscriptionDay).stringRes
                    )

                    SipSubscriptionType.MONTHLY_SIP -> userGoldSipDetails.subscriptionDay.toString()
                },
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        list.add(
            LabelAndValue(
                getCustomString(GoldSipMR.strings.feature_gold_sip_next_installment),
                nextDeductionDate,
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        sipDetailsAdapter.submitList(list)
    }

    private fun createAutoSaveDetailsList(userGoldSipDetails: UserGoldSipDetails) {
        val list = ArrayList<LabelAndValue>()
        if (userGoldSipDetails.provider.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_upi_app),
                    userGoldSipDetails.provider.orEmpty(),
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        if (userGoldSipDetails.upiId.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_upi_id),
                    userGoldSipDetails.upiId.orEmpty(),
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        if ((userGoldSipDetails.bankLogo ?: userGoldSipDetails.bankName).isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_bank_account),
                    userGoldSipDetails.bankLogo ?: userGoldSipDetails.bankName.orEmpty(),
                    isTextualValue = userGoldSipDetails.bankLogo.isNullOrEmpty(),
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        autoSaveDetailsAdapter.submitList(list)
    }

    private fun checkIsPausedAndSetViews(userGoldSipDetails: UserGoldSipDetails) {
        binding.clPauseContainer.isVisible =
            userGoldSipDetails.pauseStatus != null && userGoldSipDetails.pauseStatus?.savingsPaused.orFalse()

        if (userGoldSipDetails.pauseStatus?.savingsPaused == true) {
            userGoldSipDetails.pauseStatus?.let {
                it.pausedFor?.let {
                    PauseSavingOption.valueOf(it)
                }?.let {
                    binding.tvGoldSipIsPausedFor.text =
                        getCustomStringFormatted(
                            GoldSipMR.strings.feature_gold_sip_is_paused_for_next_s,
                            (it.timeValue.toString() + " " + getCustomString(StringResource(it.durationType.durationRes)))
                        )
                }
                binding.tvAutoResumingIn.text =
                    getString(
                        com.jar.app.core_ui.R.string.core_ui_auto_reuming_on_s,
                        it.willResumeOn?.getDateShortMonthNameAndYear().orEmpty()
                    )
                binding.btnUpdateGoldSip.setText(getCustomString(GoldSipMR.strings.feature_gold_sip_resume_now))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGoldSipUpdateEvent(goldSipUpdateEvent: com.jar.app.feature_gold_sip.shared.domain.event.GoldSipUpdateEvent) {
        mandatePaymentJob?.cancel()
        mandatePaymentJob = appScope.launch(dispatcherProvider.main) {
            mandatePaymentApi.initiateMandatePayment(
                paymentPageHeaderDetails = PaymentPageHeaderDetail(
                    toolbarHeader = getCustomString(GoldSipMR.strings.feature_gold_sip_label),
                    title = getString(com.jar.app.core_ui.R.string.core_ui_auto_save),
                    toolbarIcon = com.jar.app.core_ui.R.drawable.core_ui_ic_gold_sip,
                    savingFrequency = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> MandatePaymentEventKey.SavingFrequencies.Weekly
                        SipSubscriptionType.MONTHLY_SIP -> MandatePaymentEventKey.SavingFrequencies.Monthly
                        else -> MandatePaymentEventKey.SavingFrequencies.Weekly
                    },
                    featureFlow = MandatePaymentEventKey.FeatureFlows.UpdateSavingsPlan,
                    userLifecycle = null,
                    mandateSavingsType = when (sipSubscriptionType) {
                        SipSubscriptionType.WEEKLY_SIP -> MandatePaymentCommonConstants.MandateStaticContentType.WEEKLY_SAVINGS_MANDATE_EDUCATION
                        SipSubscriptionType.MONTHLY_SIP -> MandatePaymentCommonConstants.MandateStaticContentType.MONTHLY_SAVINGS_MANDATE_EDUCATION
                        else -> MandatePaymentCommonConstants.MandateStaticContentType.WEEKLY_SAVINGS_MANDATE_EDUCATION
                    }
                ),
                initiateMandatePaymentRequest = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest(
                    mandateAmount = goldSipUpdateEvent.sipAmount,
                    authWorkflowType = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType.PENNY_DROP,
                    subscriptionType = goldSipUpdateEvent.subscriptionType
                )
            ).collectUnwrapped(
                onSuccess = {
                    updatedSipDay = goldSipUpdateEvent.sipDay
                    updatedSipDayValue = goldSipUpdateEvent.sipDayValue
                    if (it.second.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS) {
                        fetchMandatePaymentStatusResponse = it.second
                        viewModel.updateGoldSip(
                            com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails(
                                goldSipUpdateEvent.sipAmount,
                                goldSipUpdateEvent.sipDayValue,
                                goldSipUpdateEvent.subscriptionType
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
                                    sipSubscriptionType = sipSubscriptionType!!.name,
                                    isSetupFlow = false,
                                    subscriptionDay = updatedSipDay.orEmpty(),
                                    sipDayValue = updatedSipDayValue.orZero(),
                                    nextDeductionDate = null,
                                    sipAmount = sipAmount
                                )
                            )
                        )

                        navigateTo(
                            "android-app://com.jar.app/goldSipAutoPayPendingOrFailure/$mandatePaymentResultFromSDK/$fetchMandatePaymentStatusResponse/$postSetupSipData",
                            popUpTo = R.id.goldSipTypeSelectionFragment,
                            inclusive = true
                        )
                    }

                },
                onError = { message, errorCode -> }
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDisableSipEvent(disableSipEvent: DisableSipEvent) {
        popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        mandatePaymentJob?.cancel()
        EventBus.getDefault().unregister(this)
    }
}