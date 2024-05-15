package com.jar.app.feature_round_off.impl.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentSetupBinding
import com.jar.app.feature_round_off.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SetupRoundOffFragment : BaseFragment<FeatureRoundOffFragmentSetupBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentSetupBinding
        get() = FeatureRoundOffFragmentSetupBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    companion object {
        const val SETUP_ROUND_OFF = "SetupRoundOff"
    }

    private val args: SetupRoundOffFragmentArgs by navArgs()
    private var refreshCount = 0
    private val viewModel: SetupRoundOffViewModel by viewModels()

    private val mandatePaymentProgressStatus by lazy {
        serializer.decodeFromString<MandatePaymentProgressStatus>(
            decodeUrl(args.mandatePaymentProgressStatus)
        )
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
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        binding.toolBar.tvTitle.text = getCustomString(MR.strings.feature_round_off_label)
        binding.toolBar.ivTitleImage.setImageResource(R.drawable.feature_round_off_ic_round_off)
        binding.toolBar.separator.isVisible = true
        updateUIAccordingToMandatePaymentProgress(mandatePaymentProgressStatus)
    }

    private fun updateUIAccordingToMandatePaymentProgress(mandatePaymentProgressStatus: MandatePaymentProgressStatus) {
        when (mandatePaymentProgressStatus) {
            MandatePaymentProgressStatus.SUCCESS -> {
                binding.tvStatus.text =
                    getCustomString(MR.strings.feature_round_off_save_smart_with_round_off)
                binding.tvDesc.text =
                    getCustomString(MR.strings.feature_round_off_setup_round_offs_and_save_as_you_spend)
                binding.btnAction.setText(getString(com.jar.app.feature_daily_investment.R.string.feature_daily_savings_setup_now))
            }
            MandatePaymentProgressStatus.PENDING -> {
                binding.tvStatus.text =
                    getCustomString(MR.strings.feature_round_off_hold_up_round_off_setup_in_progress)
                binding.tvDesc.text =
                    getCustomString(MR.strings.feature_round_off_dont_worry_we_will_notify_you_as_soon_as_we_get_an_update)
                binding.btnAction.setText(getString(com.jar.app.core_ui.R.string.feature_buy_gold_refresh))
            }
            MandatePaymentProgressStatus.FAILURE -> {
                binding.tvStatus.text =
                    getCustomString(MR.strings.feature_round_off_uh_oh_round_offs_setup_failed)
                binding.tvDesc.text =
                    getCustomString(MR.strings.feature_round_off_your_attempt_at_setting_up_round_offs_failed)
                binding.btnAction.setText(getString(com.jar.app.core_ui.R.string.retry))
            }
            else -> {
                binding.tvStatus.text =
                    getCustomString(MR.strings.feature_round_off_save_smart_with_round_off)
                binding.tvDesc.text =
                    getCustomString(MR.strings.feature_round_off_setup_round_offs_and_save_as_you_spend)
                binding.btnAction.setText(getString(com.jar.app.feature_daily_investment.R.string.feature_daily_savings_setup_now))
            }
        }

        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_IntermediaryScreen_RoundoffSettingsScreen, mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.CurrentStatus to mandatePaymentProgressStatus.name,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.CTA to binding.btnAction.getText()
            )
        )
    }

    private fun setupListener() {
        val currentTime = System.currentTimeMillis()
        binding.btnAction.setDebounceClickListener {
            when (mandatePaymentProgressStatus) {
                MandatePaymentProgressStatus.SUCCESS -> {
                    navigateTo("android-app://com.jar.app/roundOffExplanation/${false}/$currentTime/${"Settings"}")
                }
                MandatePaymentProgressStatus.PENDING -> {
                    refreshCount++
                    viewModel.fetchUserRoundOffDetails()
                }
                MandatePaymentProgressStatus.FAILURE -> {
                    val currentTime = System.currentTimeMillis()
                    navigateTo(
                        "android-app://com.jar.app/roundOffCalculated/$SETUP_ROUND_OFF/$currentTime",
                        popUpTo = R.id.setupRoundOffFragment,
                        inclusive = true
                    )
                }
                else -> {
                    navigateTo("android-app://com.jar.app/roundOffExplanation/${false}/${System.currentTimeMillis()}/$SETUP_ROUND_OFF")
                }
            }
        }

        binding.tvContactSupport.setDebounceClickListener {
            val number = remoteConfigManager.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomString(MR.strings.feature_round_off_hi_i_am_facing_some_issue_in_setting_up_my_round_off)
            )
        }

        binding.toolBar.btnBack.setDebounceClickListener {
            backPressCallback.handleOnBackPressed()
        }
    }

    private fun observeLiveData() {
        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                val currentTime = System.currentTimeMillis()
                dismissProgressBar()
                binding.pendingGroup.isVisible = refreshCount > 2
                if (
                    it.subscriptionStatus.isNullOrEmpty().not()
                )
                    when (MandatePaymentProgressStatus.valueOf(it.subscriptionStatus!!)) {
                        MandatePaymentProgressStatus.SUCCESS -> navigateTo("android-app://com.jar.app/roundOffDetails/$currentTime")
                        MandatePaymentProgressStatus.PENDING -> getCustomString(MR.strings.feature_round_off_round_off_setup_in_progress).snackBar(
                            binding.root
                        )
                        MandatePaymentProgressStatus.FAILURE -> updateUIAccordingToMandatePaymentProgress(
                            MandatePaymentProgressStatus.valueOf(it.subscriptionStatus!!)
                        )
                    }
            },
            onError = { dismissProgressBar() }
        )

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
}