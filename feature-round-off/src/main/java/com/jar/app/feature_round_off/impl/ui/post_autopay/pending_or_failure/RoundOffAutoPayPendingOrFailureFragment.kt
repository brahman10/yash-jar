package com.jar.app.feature_round_off.impl.ui.post_autopay.pending_or_failure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentAutopayPendingOrFailureBinding
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.feature_round_off.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffAutoPayPendingOrFailureFragment :
    BaseFragment<FeatureRoundOffFragmentAutopayPendingOrFailureBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentAutopayPendingOrFailureBinding
        get() = FeatureRoundOffFragmentAutopayPendingOrFailureBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args: RoundOffAutoPayPendingOrFailureFragmentArgs by navArgs()
    private val viewModel: RoundOffAutoPayPendingViewModel by viewModels()

    private val mandatePaymentResultFromSDK by lazy {
        val decoded = decodeUrl(args.mandatePaymentResultFromSDK)
        serializer.decodeFromString<com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK>(decoded)
    }

    private val fetchMandatePaymentStatusResponse by lazy {
        val decoded = decodeUrl(args.fetchMandatePaymentStatusResponse)
        serializer.decodeFromString<FetchMandatePaymentStatusResponse>(decoded)
    }

    companion object {
        const val RoundOffAutoPayPendingOrFailureFragment =
            "RoundOffAutoPayPendingOrFailureFragment"
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
        dismissProgressBar()
        binding.tvTitle.text = fetchMandatePaymentStatusResponse.title
        binding.tvDescription.text = fetchMandatePaymentStatusResponse.description
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.PROCESSING_RUPEE
        )
        if (fetchMandatePaymentStatusResponse.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.FAILURE) {
            binding.btnRetryOrRefresh.setText(getString(com.jar.app.core_ui.R.string.retry))
            binding.tvTitle.text =
                getCustomString(MR.strings.feature_round_off_automatic_round_off_not_setup)
            binding.tvDescription.text =
                getCustomString(MR.strings.feature_round_off_any_amount_debited_will_be_credited)
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_FailureScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
            )
        } else {
            binding.tvTitle.text =
                getCustomString(MR.strings.feature_round_off_round_off_automation_in_progress)
            binding.tvDescription.text =
                getCustomString(MR.strings.feature_round_off_its_taking_a_little_longer_to_setup)
            binding.btnRetryOrRefresh.setText(getString(com.jar.app.core_ui.R.string.feature_buy_gold_refresh))
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_PendingScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
            )
        }
    }

    private fun setupListener() {
        binding.btnRetryOrRefresh.setDebounceClickListener {
            if (fetchMandatePaymentStatusResponse.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.FAILURE) {
                navigateTo(
                    NavigationRoundOffDirections.actionToPreRoundOffAutopaySetupFragment(),
                    popUpTo = R.id.roundOffAutoPayPendingOrFailureFragment,
                    inclusive = true
                )
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_FailureScreen,
                    mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.RetryClicked)
                )
            } else {
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_PendingScreen,
                    mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.RefreshClicked)
                )
                viewModel.fetchAutoInvestStatus(mandatePaymentResultFromSDK)
            }
        }

        binding.btnGoToHome.setDebounceClickListener {
            analyticsHandler.postEvent(
                if (fetchMandatePaymentStatusResponse.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.FAILURE) com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_FailureScreen else com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_PendingScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.GoToHomeClicked)
            )
            EventBus.getDefault().post(GoToHomeEvent(RoundOffAutoPayPendingOrFailureFragment))
        }

        binding.tvContactSupport.setDebounceClickListener {
            analyticsHandler.postEvent(
                if (fetchMandatePaymentStatusResponse.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.FAILURE) com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_FailureScreen else com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_PendingScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ContactSupportClicked)
            )
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomString(MR.strings.feature_round_off_having_issues_automating_savings)
            )
        }
    }

    private fun observeLiveData() {
        viewModel.statusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                if (it?.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS)
                    navigateTo(
                        NavigationRoundOffDirections.actionToRoundOffAutoPaySuccessFragment(
                            encodeUrl(serializer.encodeToString(it))
                        )
                    )
            },
            onError = {
                dismissProgressBar()
            }
        )
    }
}