package com.jar.app.feature_gold_sip.impl.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipFragmentSetupBinding
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class SetupGoldSipFragment : BaseFragment<FeatureGoldSipFragmentSetupBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipFragmentSetupBinding
        get() = FeatureGoldSipFragmentSetupBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args: SetupGoldSipFragmentArgs by navArgs()
    private var refreshCount = 0

    private val viewModelProvider by viewModels<SetupGoldSipViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val mandatePaymentProgressStatus by lazy {
        serializer.decodeFromString<MandatePaymentProgressStatus>(
            decodeUrl(args.mandatePaymentProgressStatus)
        )
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
        binding.toolBar.tvTitle.text = getCustomString(GoldSipMR.strings.feature_gold_sip_label)
        binding.toolBar.separator.isVisible = true
        viewModel.fireSetupSipEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_IntermediaryScreen_SIPsettings,
            mapOf(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIPStatus to mandatePaymentProgressStatus.name
            )
        )
        updateUIAccordingToMandatePaymentProgress(mandatePaymentProgressStatus)
    }

    private fun updateUIAccordingToMandatePaymentProgress(mandatePaymentProgressStatus: MandatePaymentProgressStatus) {
        when (mandatePaymentProgressStatus) {
            MandatePaymentProgressStatus.SUCCESS -> {
                binding.tvStatus.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_setup_gold_sip)
                binding.tvDesc.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_save_a_fixed_amount)
                binding.btnAction.setText(getCustomString(GoldSipMR.strings.feature_gold_sip_setup_now))
            }

            MandatePaymentProgressStatus.PENDING -> {
                binding.tvStatus.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_hold_up_gold_sip_setup_in_progress)
                binding.tvDesc.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_we_will_notify_you_as_soon_as_we_get_udpate)
                binding.btnAction.setText(getString(com.jar.app.core_ui.R.string.feature_buy_gold_refresh))
            }

            MandatePaymentProgressStatus.FAILURE -> {
                binding.tvStatus.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_uh_oh_gold_sip_setup_failed)
                binding.tvDesc.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_your_attempt_to_setup_gold_sip_failed)
                binding.btnAction.setText(getString(com.jar.app.core_ui.R.string.retry))
            }

            else -> {
                binding.tvStatus.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_setup_gold_sip)
                binding.tvDesc.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_save_a_fixed_amount)
                binding.btnAction.setText(getCustomString(GoldSipMR.strings.feature_gold_sip_setup_now))
            }
        }
    }

    private fun setupListener() {
        binding.toolBar.btnBack.setDebounceClickListener {
            popBackStack()
        }
        binding.btnAction.setDebounceClickListener {
            val action: String
            when (mandatePaymentProgressStatus) {
                MandatePaymentProgressStatus.SUCCESS -> {
                    action = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Setup
                    navigateTo("android-app://com.jar.app/goldSipIntro")
                }

                MandatePaymentProgressStatus.PENDING -> {
                    action = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Refresh
                    refreshCount++
                    viewModel.fetchGoldSipDetails()
                }

                MandatePaymentProgressStatus.FAILURE -> {
                    action = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Retry
                    val sipTypeSelectionScreenData = null
                    navigateTo(
                        "android-app://com.jar.app/goldSipTypeSelection/$sipTypeSelectionScreenData",
                        popUpTo = R.id.setupGoldSipFragment
                    )
                }

                else -> {
                    action = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Setup
                    navigateTo("android-app://com.jar.app/goldSipIntro")
                }
            }
            viewModel.fireSetupSipEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_IntermediaryScreen_SIPsettings,
                mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to action,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIPStatus to mandatePaymentProgressStatus.name
                )
            )
        }

        binding.tvContactSupport.setDebounceClickListener {
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomString(GoldSipMR.strings.feature_gold_sip_hi_i_am_facing_some_issue_in_setting_up_my_gold_sip)
            )
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.goldSipDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            binding.pendingGroup.isVisible = refreshCount > 2
                            if (
                                it.subscriptionStatus.isNullOrEmpty().not()
                            )
                                when (MandatePaymentProgressStatus.valueOf(it.subscriptionStatus!!)) {
                                    MandatePaymentProgressStatus.SUCCESS -> navigateTo("android-app://com.jar.app/goldSipDetails")
                                    MandatePaymentProgressStatus.PENDING -> getCustomString(
                                        GoldSipMR.strings.feature_gold_sip_payment_still_in_progress
                                    ).snackBar(
                                        binding.root
                                    )

                                    MandatePaymentProgressStatus.FAILURE -> updateUIAccordingToMandatePaymentProgress(
                                        MandatePaymentProgressStatus.valueOf(it.subscriptionStatus!!)
                                    )
                                }
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
}