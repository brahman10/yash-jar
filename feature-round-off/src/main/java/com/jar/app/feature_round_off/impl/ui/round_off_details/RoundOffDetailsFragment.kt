package com.jar.app.feature_round_off.impl.ui.round_off_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentDetailsBinding
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_savings_common.shared.domain.model.RoundOffTo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsPauseStatusData
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.base.data.model.PauseSavingOption
import com.jar.app.core_analytics.EventKey
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffDetailsFragment : BaseFragment<FeatureRoundOffFragmentDetailsBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentDetailsBinding
        get() = FeatureRoundOffFragmentDetailsBinding::inflate

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel: RoundOffDetailsViewModel by viewModels()

    private val args by navArgs<RoundOffDetailsFragmentArgs>()

    private var roundOffAmount = 0f
    private var mandateAmount = 0f
    private var savingsPauseStatusData: SavingsPauseStatusData? = null
    private var roundOffDetailsAdapter: LabelAndValueAdapter? = null
    private var autoSaveDetailsAdapter: LabelAndValueAdapter? = null
    private val spaceItemDecorationRoundOffDetails = SpaceItemDecoration(0.dp, 8.dp)
    private val spaceItemDecorationAutoSaveDetails = SpaceItemDecoration(0.dp, 8.dp)
    private var isAutoSaveEnabled = false
    private var autoInvestForNoSpendsEnabled = false
    private var upiApp = ""
    private var bankAccount = ""
    private var paymentType = ""
    private var isItemDecorationAdded = false
    private var isAutoPayResetRequired = false

    companion object {
        private const val RoundOffDetailsFragment = "RoundOffDetailsFragment"
        private const val Automatic = "Automatic"
        private const val Manual = "Manual"
        private const val Resumed = "Resumed"
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.getExitSurveyData()
            }
        }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun getData() {
        viewModel.fetchUserRoundOffDetails()
    }

    private fun setupUI() {
        binding.toolBar.tvTitle.text = getCustomString(MR.strings.feature_round_off_label)
        binding.toolBar.ivTitleImage.setImageResource(R.drawable.feature_round_off_ic_round_off)
        binding.toolBar.separator.isVisible = true
    }

    private fun setupListeners() {
        binding.switchInvest10.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isItemDecorationAdded) {
                sendEventAccordingToAction(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.UpdatedAutoSave10)
                coreUiApi.openGenericPostActionStatusFragment(
                    GenericPostActionStatusData(
                        postActionStatus = if (isChecked) PostActionStatus.ENABLED.name else PostActionStatus.DISABLED.name,
                        header = if (isChecked) getCustomString(MR.strings.feature_round_off_activated)
                        else getCustomString(MR.strings.feature_round_off_disabled),
                        headerTextSize = 18f,
                        headerColorRes = if (isChecked) com.jar.app.core_ui.R.color.color_1EA787 else com.jar.app.core_ui.R.color.color_EB6A6E,
                        description = if (isChecked) getCustomString(MR.strings.feature_round_off_you_will_be_saving_10)
                        else getCustomString(MR.strings.feature_round_off_nothing_will_be_saved_for_the_days),
                        descriptionColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                        imageRes = if (isChecked) com.jar.app.core_ui.R.drawable.core_ui_ic_tick else com.jar.app.core_ui.R.drawable.core_ui_ic_disabled,
                        shouldShowConfettiFromTop = isChecked
                    )
                ) {
                    viewModel.toggleAutoInvest(isChecked)
                }
            }
        }

        binding.clAutoSaveDetailsContainer.setDebounceClickListener {
            if (isAutoSaveEnabled.not())
                if ((roundOffAmount >= mandateAmount || roundOffAmount == 0f) && isAutoPayResetRequired)
                    navigateTo(
                        NavigationRoundOffDirections.actionToRoundOffAutoSaveResumeBottomSheet(
                            roundOffAmount, mandateAmount
                        )
                    )
                else
                    viewModel.enableAutomaticRoundOff()
        }

        binding.clManualDetailsContainer.setDebounceClickListener {
            if (isAutoSaveEnabled) {
                sendEventAccordingToAction(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualPaymentMethod)
                navigateTo(NavigationRoundOffDirections.actionToRoundOffChangeToManualBottomSheet())
            }
        }
        binding.btnRoundOffPrimaryAction.setDebounceClickListener {
            if (savingsPauseStatusData?.savingsPaused.orFalse()) viewModel.resumeRoundOff()
        }

        binding.toolBar.btnBack.setDebounceClickListener {
            backPressCallback.handleOnBackPressed()
        }
        binding.btnRoundOffSecondaryAction.setDebounceClickListener {
            handleSecondaryCta()
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            }, onSuccess = {
                dismissProgressBar()
                setUserSavingViews(it)
                viewModel.apiExecutionCount += 1
                val currentTime = System.currentTimeMillis()
                if (args.screenFlow == EventKey.HOME_SCREEN && viewModel.apiExecutionCount == 1) {
                    analyticsHandler.postEvent(
                        RoundOffEventKey.Shown_RoundOff_Settings_Screen_Ts,
                        mapOf(
                            EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(
                                endTimeTime = currentTime,
                                startTime = args.clickTime.toLong()
                            )
                        )
                    )
                }
            }, onError = {
                dismissProgressBar()
            }
        )
        viewModel.roundOffPausedLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            }, onSuccess = {
                dismissProgressBar()
                coreUiApi.openGenericPostActionStatusFragment(
                    GenericPostActionStatusData(
                        postActionStatus = PostActionStatus.RESUMED.name,
                        header = getCustomString(MR.strings.feature_round_off_yay_round_off_resumed_successfully),
                        title = getCustomString(MR.strings.feature_round_off_your_savings_will_continue_tomorrow),
                        titleColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                        description = null,
                        imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                        headerTextSize = 20f,
                    )
                ) {
                    analyticsHandler.postEvent(
                        RoundOffEventKey.Shown_StopConfirmation_RoundoffSettings,
                        mapOf(
                            RoundOffEventKey.Status to Resumed,
                            RoundOffEventKey.PaymentType to paymentType
                        )
                    )
                    EventBus.getDefault()
                        .post(com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent())
                    viewModel.fetchUserRoundOffDetails()
                }
            }, onError = {
                dismissProgressBar()
            }
        )

        viewModel.managePreferenceLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                analyticsHandler.postEvent(RoundOffEventKey.Roundoff_AutosaveActivatedFromDetails_Screenshown)
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
                    EventBus.getDefault().post(RefreshRoundOffStateEvent())
                    setUserSavingViews(it)
                }
            }
        )

        viewModel.initialRoundOffLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                mandateAmount = it?.mandateAmount.orZero()
                viewModel.isAutoPayResetRequired(newAmount = mandateAmount)
            },
            onSuccessWithNullData = { dismissProgressBar() },
            onError = { dismissProgressBar() }
        )

        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                isAutoPayResetRequired = it.isResetRequired
                if (it.isResetRequired)
                    mandateAmount = it.getFinalMandateAmount()
            },
            onError = { dismissProgressBar() }
        )

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.PAUSE_ROUND_OFFS
        )?.observe(viewLifecycleOwner) {
            viewModel.fetchUserRoundOffDetails()
            EventBus.getDefault().post(RefreshRoundOffStateEvent())
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.ROUND_OFF_AUTO_SAVE_DISABLED
        )?.observe(viewLifecycleOwner) {
            binding.autoSaveExpandView.isExpanded = false
            autoSaveDetailsAdapter = null
            viewModel.fetchUserRoundOffDetails()
            EventBus.getDefault().post(RefreshRoundOffStateEvent())
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.DISABLE_ROUND_OFF
        )?.observe(viewLifecycleOwner) {
            uiScope.launch {
                delay(100)
                EventBus.getDefault().post(RefreshRoundOffStateEvent())
                popBackStack(
                    R.id.roundOffDetailsFragment,
                    true
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.exitSurveyResponse.collectLatest {
                    it?.let {
                        if (it) {
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent("${BaseConstants.EXIT_SURVEY_DEEPLINK}/${ExitSurveyRequestEnum.ROUND_OFFS.name}")
                            )
                        } else {
                            popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun setUserSavingViews(userSavingsDetails: UserSavingsDetails) {
        savingsPauseStatusData = userSavingsDetails.pauseStatus
        roundOffAmount = userSavingsDetails.subscriptionAmount
        mandateAmount = userSavingsDetails.mandateAmount.orZero()
        createRoundOffDetailsList(userSavingsDetails)
        isAutoSaveEnabled = userSavingsDetails.autoSaveEnabled.orFalse()
        autoInvestForNoSpendsEnabled = userSavingsDetails.autoInvestForNoSpends.orFalse()
        paymentType = if (isAutoSaveEnabled) Automatic else Manual
        if (isAutoSaveEnabled) createAutoSaveDetailsList(userSavingsDetails)

        if (roundOffAmount == 0f) {
            viewModel.fetchInitialRoundOffsData()
        }
        sendEventAccordingToAction(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
        binding.ivAutoSaveRadio.isSelected = isAutoSaveEnabled
        binding.ivManualRadio.isSelected = isAutoSaveEnabled.not()
        binding.groupInvest10.isVisible = (userSavingsDetails.autoInvestForNoSpends == null).not()
        binding.switchInvest10.isChecked = autoInvestForNoSpendsEnabled.orFalse()
        isItemDecorationAdded = true
        checkIsPausedAndSetViews(userSavingsDetails)
        setupButtonsAccordingToRoundOffState()
        binding.tvSave10.text = HtmlCompat.fromHtml(
            getCustomString(MR.strings.feature_round_off_save_10_if_no_spends_detected),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun checkIsPausedAndSetViews(userSavingsDetails: UserSavingsDetails) {
        binding.clPauseContainer.isVisible =
            userSavingsDetails.pauseStatus != null && userSavingsDetails.pauseStatus?.savingsPaused.orFalse()

        if (userSavingsDetails.pauseStatus?.savingsPaused == true) {
            userSavingsDetails.pauseStatus?.let {
                it.pausedFor?.let {
                    PauseSavingOption.valueOf(it)
                }?.let {
                    val durationRes = it.durationType.durationRes
                    binding.tvRoundOffIsPausedFor.text = getCustomStringFormatted(
                        MR.strings.feature_round_off_is_paused_for_next_s,
                        (it.timeValue.toString() + " " + getCustomString(StringResource(durationRes)))
                    )
                }
                binding.tvAutoResumingIn.text = getString(
                    com.jar.app.core_ui.R.string.core_ui_auto_reuming_on_s,
                    it.willResumeOn?.getDateShortMonthNameAndYear().orEmpty()
                )
                binding.tvPauseOrActive.text = getCustomString(MR.strings.feature_round_off_pause)
                binding.tvPauseOrActive.setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_EBB46A
                    )
                )
                binding.tvPauseOrActive.backgroundTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_EBB46A_10
                    )
            }
        } else {
            binding.tvPauseOrActive.text = getString(com.jar.app.core_ui.R.string.core_ui_active)
            binding.tvPauseOrActive.setTextColor(
                ContextCompat.getColorStateList(
                    requireContext(),
                    com.jar.app.core_ui.R.color.color_58DDC8
                )
            )
            binding.tvPauseOrActive.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireContext(),
                    com.jar.app.core_ui.R.color.color_273442
                )
        }
    }

    private fun createRoundOffDetailsList(userSavingsDetails: UserSavingsDetails) {
        binding.tvRoundOffSetupDate.text = getCustomStringFormatted(
            MR.strings.feature_round_off_was_setup_on,
            userSavingsDetails.updateDate?.epochToDate()?.getFormattedDate("d MMM''yy HH:mm a")
                .orEmpty()
        )
        if (isItemDecorationAdded.not()) {
            binding.rvRoundOffSetupDetails.layoutManager = LinearLayoutManager(requireContext())
            binding.rvRoundOffSetupDetails.addItemDecoration(spaceItemDecorationRoundOffDetails)
            roundOffDetailsAdapter = LabelAndValueAdapter()
            binding.rvRoundOffSetupDetails.adapter = roundOffDetailsAdapter
        }
        val list = ArrayList<LabelAndValue>()
        if (userSavingsDetails.provider.isNullOrEmpty()
                .not() && userSavingsDetails.subscriptionAmount != 0f
        ) list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_amount),
                getCustomStringFormatted(
                    MR.strings.feature_round_off_upto_x, userSavingsDetails.subscriptionAmount
                ),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                valueColorRes = com.jar.app.core_ui.R.color.white,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        else if (userSavingsDetails.subscriptionAmount == 0f && userSavingsDetails.roundOffTo != null) list.add(
            LabelAndValue(
                getCustomString(MR.strings.feature_round_off_rounded_off_to),
                getCustomStringFormatted(
                    MR.strings.feature_round_off_nearest_d,
                    when (userSavingsDetails.roundOffTo!!) {
                        RoundOffTo.NEAREST_FIVE -> 5
                        RoundOffTo.NEAREST_TEN -> 10
                    }
                ),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                valueColorRes = com.jar.app.core_ui.R.color.white,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_frequency),
                getCustomString(MR.strings.feature_round_off_daily),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                valueColorRes = com.jar.app.core_ui.R.color.white,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        roundOffDetailsAdapter?.submitList(list)
    }

    private fun createAutoSaveDetailsList(userSavingsDetails: UserSavingsDetails) {
        upiApp = userSavingsDetails.provider.orEmpty()
        bankAccount = userSavingsDetails.bankName.orEmpty()
        binding.autoSaveExpandView.isExpanded = true
        if (isItemDecorationAdded.not()) {
            binding.rvAutoSaveDetails.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAutoSaveDetails.addItemDecoration(spaceItemDecorationAutoSaveDetails)
            autoSaveDetailsAdapter = LabelAndValueAdapter()
            binding.rvAutoSaveDetails.adapter = autoSaveDetailsAdapter
        }
        val list = ArrayList<LabelAndValue>()
        if (userSavingsDetails.provider.isNullOrEmpty().not()) list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_upi_app),
                userSavingsDetails.provider.orEmpty(),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                valueColorRes = com.jar.app.core_ui.R.color.white,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        if (userSavingsDetails.upiId.isNullOrEmpty().not()) list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_upi_id),
                userSavingsDetails.upiId.orEmpty(),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                valueColorRes = com.jar.app.core_ui.R.color.white,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        if ((userSavingsDetails.bankLogo ?: userSavingsDetails.bankName).isNullOrEmpty()
                .not()
        ) list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_bank_account),
                userSavingsDetails.bankLogo ?: userSavingsDetails.bankName.orEmpty(),
                isTextualValue = userSavingsDetails.bankLogo.isNullOrEmpty(),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                valueColorRes = com.jar.app.core_ui.R.color.white,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        autoSaveDetailsAdapter?.submitList(list)
        sendEventAccordingToAction(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
    }

    private fun setupButtonsAccordingToRoundOffState() {
        if (savingsPauseStatusData?.savingsPaused == true) {
            binding.btnRoundOffPrimaryAction.isVisible = true
            binding.btnRoundOffPrimaryAction.setText(getCustomString(MR.strings.feature_round_off_resume_now))
            binding.btnRoundOffSecondaryAction.setText(getString(com.jar.app.core_ui.R.string.core_ui_disable))
        } else {
            binding.btnRoundOffPrimaryAction.isVisible = false
            binding.btnRoundOffSecondaryAction.setText(getCustomString(MR.strings.feature_round_off_stop_round_off))
        }
    }

    private fun handleSecondaryCta() {
        sendEventAccordingToAction(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.StopRoundoff)
        if (savingsPauseStatusData?.savingsPaused == true) navigateTo(
            NavigationRoundOffDirections.actionToDisableRoundOffBottomSheet(
                paymentType
            )
        )
        else navigateTo(
            NavigationRoundOffDirections.actionToPauseOrDisableRoundOffBottomSheet(
                paymentType
            )
        )
    }

    private fun sendEventAccordingToAction(action: String) {
        if (savingsPauseStatusData?.savingsPaused == true) analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_RoundoffSettings_PausedStateScreen,
            mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to action,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutoSave10Enabled to autoInvestForNoSpendsEnabled,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateAmount to mandateAmount,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutopayUPI to upiApp,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.BankAccount to bankAccount,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to paymentType
            )
        )
        else if (isAutoSaveEnabled) analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_Automatic_RoundoffSettingsScreen,
            mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to action,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutoSave10Enabled to autoInvestForNoSpendsEnabled,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateAmount to mandateAmount,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutopayUPI to upiApp,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.BankAccount to bankAccount
            )
        )
        else analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_Manual_RoundoffSettingsScreen,
            mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to action,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutoSave10Enabled to autoInvestForNoSpendsEnabled
            )
        )
    }

    override fun onDestroyView() {
        autoSaveDetailsAdapter = null
        roundOffDetailsAdapter = null
        isItemDecorationAdded = false
        super.onDestroyView()
    }


}