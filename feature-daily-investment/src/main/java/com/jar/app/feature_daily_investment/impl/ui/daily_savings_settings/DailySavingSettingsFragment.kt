package com.jar.app.feature_daily_investment.impl.ui.daily_savings_settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.data.model.PauseSavingOption
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_daily_investment.api.domain.event.SetupMandateEvent
import com.jar.app.feature_daily_investment.databinding.FeatureDailySavingsSettingsFragmentBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.disabled
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.enabled
import com.jar.app.feature_daily_investment.impl.ui.disable_savings.DisableDailySavingEvent
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_savings_common.shared.domain.model.SavingsPauseStatusData
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingSettingsFragment :
    BaseFragment<FeatureDailySavingsSettingsFragmentBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailySavingsSettingsFragmentBinding
        get() = FeatureDailySavingsSettingsFragmentBinding::inflate

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val detailsAdapter = LabelAndValueAdapter()

    private val spaceItemDecorationAutoSaveDetails = SpaceItemDecoration(0.dp, 6.dp)

    private var savingsPauseStatusData: SavingsPauseStatusData? = null

    private val viewModelProvider by viewModels<DailySavingsSettingsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var dailySavingAmount: Float? = null

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUserDailySavingsDetails()
        EventBus.getDefault().register(this)
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun sendEvent() {
        analyticsHandler.postEvent(
            DailySavingsEventKey.Shown_DailySavingsScreen,
            mapOf(
                DailySavingsEventKey.currentState to if (viewModel.isSavingsEnabled) enabled else disabled,
                DailySavingsEventKey.UserLifecycle to prefs.getUserLifeCycleForMandate().orEmpty()
            )
        )
    }

    private fun setupUI() {
        binding.rvAutoSaveDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAutoSaveDetails.addItemDecoration(spaceItemDecorationAutoSaveDetails)
        binding.rvAutoSaveDetails.adapter = detailsAdapter
        binding.toolbar.tvTitle.text = getString(R.string.feature_daily_savings)
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_daily_investment_ic_daily_saving_tab)
        binding.toolbar.separator.isVisible = true
        OverScrollDecoratorHelper.setUpOverScroll(binding.scrollView)
    }

    private fun setupListeners() {
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
        binding.btnDailySavingPrimaryAction.setDebounceClickListener {
            if (savingsPauseStatusData?.savingsPaused.orFalse()) {
                viewModel.updateAutoInvestPauseDuration(false, null)
            }
        }

        binding.tvChangeAmount.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.Clicked_EditAmount_DailySavingsScreen, mapOf(
                    DailySavingsEventKey.currentState to viewModel.isSavingsEnabled,
                    DailySavingsEventKey.currentValue to dailySavingAmount.orZero().toString()
                )
            )
            navigateTo(
                DailySavingSettingsFragmentDirections.actionToUpdateSetupDailySavingsBottomSheet(
                    defaultAmount = dailySavingAmount ?: 0f,
                    isFromCancellationFlow = false
                )
            )
        }

        binding.btnDailySavingSecondaryAction.setDebounceClickListener {
            analyticsHandler.postEvent(DailySavingsEventKey.Clicked_Pause_DailySavingsScreen)
            val showNewBottomSheet = remoteConfigManager.isShowNewDSBottomSheet()
            if (showNewBottomSheet) {
                navigateTo(
                    DailySavingSettingsFragmentDirections.actionToPauseOrDisableDailySavingsConfirmationBottomSheetNew(
                        viewModel.isSavingPaused, dailySavingAmount ?: 0f
                    )
                )
            } else {
                navigateTo(
                    DailySavingSettingsFragmentDirections.actionToPauseOrDisableDailySavingsConfirmationBottomSheet(
                        viewModel.isSavingPaused
                    )
                )
            }

        }

        binding.clTrackSavings.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DS_Settings_ManageMySavingsClicked,
                mapOf(DailySavingsEventKey.DailySavingsAmount to dailySavingAmount.orZero())
            )
            navigateTo(BaseConstants.InternalDeepLinks.POST_SETUP)
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updatePauseDurationFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        if (!it.isSavingPaused.orFalse()) {
                            navigateTo(DailySavingSettingsFragmentDirections.actionToResumeDailySavings())
                        }
                    },
                    onError = {errorMessage,_->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.dailySavingsDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.isSavingPaused = it.pauseStatus?.savingsPaused.orFalse()
                        viewModel.isSavingsEnabled = it.enabled.orFalse()
                        savingsPauseStatusData = it.pauseStatus
                        setSubscriptionAmount(it.subscriptionAmount)
                        createDailySavingsDetailList(it)
                        togglePausedOrActiveView(it)
                        setupButtonsAccordingToState(it)
                        binding.tvSetupDoneAt.text = getString(
                            R.string.feature_daily_investment_daily_saving_was_setup_on_n,
                            it.updateDate?.getDateMonthNameAndYear("d MMM''yy hh:mm a").orEmpty()
                        )
                        if (it.enabled.orFalse()) {
                            dailySavingAmount = it.subscriptionAmount
                            viewModel.isSavingsEnabled = it.enabled.orFalse()
                            sendEvent()
                        } else {
                            popBackStack()
                        }
                        if (it.pauseStatus?.savingsPaused.orFalse()) {
                            binding.btnDailySavingSecondaryAction.setText(getString(com.jar.app.core_ui.R.string.core_ui_disable))
                        } else {
                            binding.btnDailySavingSecondaryAction.setText(getString(R.string.feature_daily_investment_stop_daily_saving))
                        }
                        binding.clTrackSavings.isVisible =
                            it.savingsMetaData?.showPostSetupData.orFalse()
                    },
                    onError = {errorMessage,_->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun setSubscriptionAmount(subscriptionAmount: Float) {
        binding.tvAmountValue.text =
            getString(R.string.feature_daily_investment_currency_sign) + subscriptionAmount.toInt()
                .toString()
    }

    private fun setupButtonsAccordingToState(userSavingsDetails: UserSavingsDetails) {
        binding.btnDailySavingPrimaryAction.isVisible =
            userSavingsDetails.pauseStatus?.savingsPaused.orFalse()
    }

    private fun createDailySavingsDetailList(userSavingsDetails: UserSavingsDetails) {
        val list = ArrayList<LabelAndValue>()
        if (userSavingsDetails.provider.isNullOrEmpty().not()) list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_upi_app),
                userSavingsDetails.provider.orEmpty(),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        if (userSavingsDetails.upiId.isNullOrEmpty().not()) list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_upi_id),
                userSavingsDetails.upiId.orEmpty(),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
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
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        detailsAdapter.submitList(list)
        binding.rvAutoSaveDetails.isVisible = list.size > 0
    }

    private fun togglePausedOrActiveView(userSavingsDetails: UserSavingsDetails) {
        binding.clPausedHolder.isVisible =
            userSavingsDetails.pauseStatus != null && userSavingsDetails.pauseStatus?.savingsPaused.orFalse()

        if (userSavingsDetails.pauseStatus?.savingsPaused == true) {
            userSavingsDetails.pauseStatus?.let {
                it.pausedFor?.let {
                    PauseSavingOption.valueOf(it)
                }?.let {
                    binding.tvPausedFor.text = getString(
                        R.string.feature_daily_investment_daily_saving_paused_for_n_days,
                        it.timeValue
                    )
                }
                binding.tvAutoResumingOn.text = getString(
                    com.jar.app.core_ui.R.string.core_ui_auto_reuming_on_s,
                    it.willResumeOn?.getDateShortMonthNameAndYear().orEmpty()
                )
                binding.tvActiveStatus.text = getString(R.string.feature_daily_investment_paused)
                binding.tvActiveStatus.setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_EBB46A
                    )
                )
                binding.tvActiveStatus.backgroundTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_EBB46A_10
                    )
            }
        } else {
            binding.tvActiveStatus.text = getString(com.jar.app.core_ui.R.string.core_ui_active)
            binding.tvActiveStatus.setTextColor(
                ContextCompat.getColorStateList(
                    requireContext(),
                    com.jar.app.core_ui.R.color.color_58DDC8
                )
            )
            binding.tvActiveStatus.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireContext(),
                    com.jar.app.core_ui.R.color.color_273442
                )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDailySavingEvent(refreshDailySavingEvent: RefreshDailySavingEvent) {
        viewModel.fetchUserDailySavingsDetails()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDisableDailySavingEvent(disableDailySavingEvent: DisableDailySavingEvent) {
        popBackStack()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSetupMandateEvent(setupMandateEvent: SetupMandateEvent) {
        setupMandateEvent.newDailySavingAmount?.let {
            dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                mandateAmount = setupMandateEvent.newMandateAmount,
                source = MandatePaymentEventKey.FeatureFlows.UpdateDailySaving,
                authWorkflowType = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType.PENNY_DROP,
                newDailySavingAmount = it,
                popUpToId = R.id.updateSetupDailySavingsBottomSheet,
                userLifecycle = prefs.getUserLifeCycleForMandate()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}