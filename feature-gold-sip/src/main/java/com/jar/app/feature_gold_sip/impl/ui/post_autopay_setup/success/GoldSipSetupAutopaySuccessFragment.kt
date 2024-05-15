package com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.capitaliseFirstChar
import com.jar.app.base.util.getDayOfMonthAndItsSuffix
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_gold_sip.NavigationGoldSipDirections
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipFragmentSipSetupAutopaySuccessBinding
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldSipSetupAutopaySuccessFragment :
    BaseFragment<FeatureGoldSipFragmentSipSetupAutopaySuccessBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipFragmentSipSetupAutopaySuccessBinding
        get() = FeatureGoldSipFragmentSipSetupAutopaySuccessBinding::inflate

    @Inject
    lateinit var prefs: PrefsApi

    private val args: GoldSipSetupAutopaySuccessFragmentArgs by navArgs()
    private val sipSubscriptionType by lazy {
        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(args.goldSipUpdateEvent.subscriptionType)
    }


    private val viewModelProvider by viewModels<GoldSipAutoPaySuccessViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var recommendedDay: Int = 0

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                initiateGoToHomeEvent()
                isEnabled = false
            }
        }

    companion object {
        const val GoldSipSetupAutopaySuccessFragment = "GoldSipSetupAutopaySuccessFragment"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
        viewModel.fireSipAutoPaySuccessEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PostSetupScreen, mapOf(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupStatus to ManualPaymentStatus.SUCCESS,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(sipSubscriptionType.textRes),
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to args.goldSipUpdateEvent.sipAmount,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Date to args.goldSipUpdateEvent.sipDay,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.FromFlow to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupFlow
            )
        )
        recommendedDay = args.goldSipUpdateEvent.sipDayValue
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.CONFETTI_FROM_TOP
        )
        binding.successLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.SMALL_CHECK
        )

        binding.tvDescription.text = getCustomStringFormatted(
            GoldSipMR.strings.feature_gold_sip_s_saving_active,
            getCustomString(sipSubscriptionType.textRes)
        )

        binding.tvSSavingWillBe.text = getCustomStringFormatted(
            GoldSipMR.strings.feature_gold_sip_s_savings_will_be_debited_on_every,
            getCustomString(sipSubscriptionType.textRes)
        )

        binding.tvAmountAndGold.text = getString(
            com.jar.app.core_ui.R.string.core_ui_rs_x_int,
            args.goldSipUpdateEvent.sipAmount.toInt()
        )

        binding.tvDayOrDate.text = when (sipSubscriptionType) {
            com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> args.goldSipUpdateEvent.sipDay.lowercase()
                .capitaliseFirstChar()
            com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> args.goldSipUpdateEvent.sipDayValue.getDayOfMonthAndItsSuffix()
        }
    }

    private fun setupListener() {
        binding.tvChange.setDebounceClickListener {
            navigateTo(
                NavigationGoldSipDirections.actionToSelectSipDayOrDateBottomSheet(
                    amount = args.goldSipUpdateEvent.sipAmount,
                    sipSubscriptionType = sipSubscriptionType,
                    recommendedDay = recommendedDay,
                    true
                )
            )
        }
        binding.btnGoToHome.setDebounceClickListener {
            initiateGoToHomeEvent()
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchSetupGoldSipFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        recommendedDay = it.recommendedDay
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.DAY_OR_DATE_UPDATED
        )?.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvDayOrDate.text = it
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun initiateGoToHomeEvent() {
        EventBus.getDefault().post(
            GoToHomeEvent(
                GoldSipSetupAutopaySuccessFragment,
                BaseConstants.HomeBottomNavigationScreen.HOME
            )
        )
    }
}