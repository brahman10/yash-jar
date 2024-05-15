package com.jar.app.feature_round_off.impl.ui.post_one_time_payment.pending_or_failure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentPaymentPendingOrFailureBinding
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_round_off.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffPaymentPendingOrFailureFragment :
    BaseFragment<FeatureRoundOffFragmentPaymentPendingOrFailureBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentPaymentPendingOrFailureBinding
        get() = FeatureRoundOffFragmentPaymentPendingOrFailureBinding::inflate

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnGoToHome.performClick()
            }
        }

    private val fetchManualPaymentStatusResponse by lazy {
        serializer.decodeFromString<FetchManualPaymentStatusResponse>(
            decodeUrl(args.fetchManualPaymentStatusResponse)
        )
    }

    private val args: RoundOffPaymentPendingOrFailureFragmentArgs by navArgs()

    private val viewModel: RoundOffPaymentPendingViewModel by viewModels()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        dismissProgressBar()
        if (fetchManualPaymentStatusResponse.getManualPaymentStatus() == com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING) {
            binding.tvTitle.text =
                getCustomStringFormatted(
                    MR.strings.feature_round_off_we_are_trying_to_buy_gold_worth_rs_x_for_you,
                    fetchManualPaymentStatusResponse.amount.orZero().toInt()
                )
            binding.tvDescription.text =
                getCustomString(MR.strings.feature_round_off_its_taking_a_little_longer_to_setup)
            binding.btnRetryOrRefresh.setText(getString(com.jar.app.core_ui.R.string.feature_buy_gold_refresh))
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_PendingScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
            )
            binding.lottieView.setAnimation(com.jar.app.core_ui.R.raw.purchase_processing)
            binding.lottieView.playAnimation()
        } else {
            binding.tvTitle.text =
                getCustomStringFormatted(
                    MR.strings.feature_round_off_we_are_unable_to_save_your_rs_x_in_gold,
                    fetchManualPaymentStatusResponse.amount.orZero().toInt()
                )
            binding.tvDescription.text =
                getCustomString(MR.strings.feature_round_off_there_is_some_issue_with_your_bank)
            binding.btnRetryOrRefresh.setText(getString(com.jar.app.core_ui.R.string.retry))
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_FailureScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
            )
            binding.lottieView.setAnimation(com.jar.app.core_ui.R.raw.purchase_processing)
            binding.lottieView.playAnimation()
        }
    }

    private fun setupListener() {
        binding.btnRetryOrRefresh.setDebounceClickListener {
            if (fetchManualPaymentStatusResponse.getManualPaymentStatus() == com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING) {
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_PendingScreen,
                    mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.RefreshClicked)
                )
                viewModel.fetchManualPaymentStatus(
                    fetchManualPaymentStatusResponse.transactionId.orEmpty(),
                    fetchManualPaymentStatusResponse.paymentProvider.orEmpty()
                )
            } else {
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_FailureScreen,
                    mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.RetryClicked)
                )
                navigateTo(
                    NavigationRoundOffDirections.actionToSelectRoundOffSaveMethodFragment(),
                    popUpTo = R.id.roundOffAutoPayPendingOrFailureFragment,
                    inclusive = true
                )
            }
        }

        binding.tvContactSupport.setDebounceClickListener {
            analyticsHandler.postEvent(
                if (fetchManualPaymentStatusResponse.getManualPaymentStatus() == com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING) com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_PendingScreen else com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_FailureScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ContactSupportClicked)
            )
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomStringFormatted(
                    MR.strings.feature_round_off_im_having_issues_buying_gold_for_x_transactionId,
                    fetchManualPaymentStatusResponse.transactionId.orEmpty()
                )
            )
        }

        binding.btnGoToHome.setDebounceClickListener {
            EventBus.getDefault()
                .post(GoToHomeEvent(RoundOffPaymentPendingOrFailureFragment::javaClass.name))
        }
    }

    private fun observeLiveData() {
        viewModel.fetchManualPaymentResponseLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                if (it.getManualPaymentStatus() == com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS) {
                    navigateTo(
                        NavigationRoundOffDirections.actionToRoundOffAutoPaySuccessFragment(
                            serializer.encodeToString(it)
                        ),
                        popUpTo = R.id.roundOffAutoPayPendingOrFailureFragment,
                        inclusive = true
                    )
                } else {

                }
            },
            onError = { dismissProgressBar() }
        )
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }
}