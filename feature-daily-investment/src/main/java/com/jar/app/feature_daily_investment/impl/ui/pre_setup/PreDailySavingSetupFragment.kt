package com.jar.app.feature_daily_investment.impl.ui.pre_setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.DailyInvestmentSetupArguments
import com.jar.app.base.data.model.FeatureFlowData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentPreDailySavingSetupBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatusConverter
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PreDailySavingSetupFragment :
    BaseFragment<FeatureDailyInvestmentFragmentPreDailySavingSetupBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    private val args: PreDailySavingSetupFragmentArgs by navArgs()
    private val viewModel: PreDailySavingSetupViewModel by viewModels()
    private var isMultipleTimes = false
    private var retry = 0

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentPreDailySavingSetupBinding
        get() = FeatureDailyInvestmentFragmentPreDailySavingSetupBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getString(R.string.feature_daily_savings)
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_daily_investment_ic_daily_saving_tab)
        binding.toolbar.separator.isVisible = true
        OverScrollDecoratorHelper.setUpOverScroll(binding.root)
        binding.clHelpHolder.isVisible = isMultipleTimes
        setUIAccordingToStatus(MandatePaymentProgressStatusConverter.getFromString(args.status))
        analyticsHandler.postEvent(
            DailySavingsEventKey.Shown_IntermediaryScreen_DailySavingsSettings,
            mapOf(
                DailySavingsEventKey.CurrentStatus to args.status,
                DailySavingsEventKey.CTA to binding.btnAction.getText(),
            )
        )
    }

    private fun setUIAccordingToStatus(mandateStatus: com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus) {
        when (mandateStatus) {
            com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS -> {
                binding.tvStatus.text =
                    getString(R.string.feature_daily_investment_setup_daily_savings_amount)
                binding.tvDesc.text =
                    getString(R.string.feature_daily_investment_save_a_fixed_amount_and_grow_message)
                binding.btnAction.setText(getString(R.string.feature_daily_savings_setup_now))
            }

            MandatePaymentProgressStatus.PENDING -> {
                binding.tvStatus.text =
                    getString(R.string.feature_daily_investment_setup_ds_setup_in_progress)
                binding.tvDesc.text =
                    getString(R.string.feature_daily_investment_setup_dont_worry_will_update_soon)
                binding.btnAction.setText(getString(R.string.feature_daily_investment_refresh))
            }

            MandatePaymentProgressStatus.FAILURE -> {
                binding.tvStatus.text =
                    getString(R.string.feature_daily_investment_setup_ds_setup_failed)
                binding.tvDesc.text =
                    getString(R.string.feature_daily_investment_setup_ds_setup_failed_try_agian)
                binding.btnAction.setText(getString(R.string.feature_daily_investment_retry))

            }
        }
    }

    private fun setupListeners() {
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
        binding.btnAction.setDebounceClickListener {
            analyticsHandler.postEvent(DailySavingsEventKey.Clicked_SetupNow_DailySavingsScreen)
            when (MandatePaymentProgressStatusConverter.getFromString(args.status)) {
                com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS -> {
                    analyticsHandler.postEvent(DailySavingsEventKey.Clicked_SetupNow_DailySavingsScreen)
                    val data = encodeUrl(
                        serializer.encodeToString(
                            DailyInvestmentSetupArguments(
                                FeatureFlowData(fromScreen = "PreDailySavingSetup")
                            )
                        )
                    )
                    navigateTo(
                        "android-app://com.jar.app/setupDailyInvestment/$data",
                        popUpTo = R.id.preSetupFragment,
                        inclusive = true
                    )
                }

                MandatePaymentProgressStatus.PENDING -> {
                    retry++
                    viewModel.fetchUserDailySavingsDetails()
                }

                MandatePaymentProgressStatus.FAILURE -> {
                    analyticsHandler.postEvent(DailySavingsEventKey.Clicked_SetupNow_DailySavingsScreen)
                    val data = encodeUrl(
                        serializer.encodeToString(
                            DailyInvestmentSetupArguments(
                                FeatureFlowData(fromScreen = "PreDailySavingSetup")
                            )
                        )
                    )
                    navigateTo(
                        "android-app://com.jar.app/setupDailyInvestment/$data",
                        popUpTo = R.id.preSetupFragment,
                        inclusive = true
                    )
                }
            }
        }
        binding.tvContactSupport.setDebounceClickListener {
            val number = remoteConfigManager.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getString(R.string.feature_daily_investment_hi_i_am_facing_some_issue_in_setting_up_my_daily_savings)
            )
        }
    }

    private fun observeLiveData() {
        viewModel.dailySavingsDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                binding.clHelpHolder.isVisible = retry > 2
                if (it.subscriptionStatus.isNullOrEmpty().not()) {
                    val status =
                        MandatePaymentProgressStatusConverter.getFromString(it.subscriptionStatus!!)
                    when (status) {
                        com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS ->
                            navigateTo("android-app://com.jar.app/dailySavings")
                        com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.PENDING -> {
                            getString(R.string.feature_daily_investment_setup_ds_setup_in_progress).snackBar(
                                binding.root
                            )
                            setUIAccordingToStatus(status)
                        }
                        com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.FAILURE -> setUIAccordingToStatus(status)
                    }
                }
            },
            onError = { dismissProgressBar() }
        )
    }
}