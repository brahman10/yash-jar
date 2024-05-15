package com.jar.app.feature_lending.impl.ui.repayments.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.core.text.underline
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentRepaymentStatusBinding
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class RepaymentStatusFragment : BaseFragment<FragmentRepaymentStatusBinding>() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<RepaymentStatusFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRepaymentStatusBinding
        get() = FragmentRepaymentStatusBinding::inflate

    override fun setupAppBar() {
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        var url: String = ""
        var statusText: String = ""

        when (args.status) {
            PAYMENT_STATUS_SUCCESS -> {
                url = LendingConstants.LottieUrls.TICK_WITH_CELEBRATION
                statusText = getCustomString(MR.strings.feature_lending_payment_successful)
                binding.tvContactUs.isVisible = false
                binding.btnAction.setText(getCustomString(MR.strings.feature_lending_done))
            }
            PAYMENT_STATUS_PENDING -> {
                url = LendingConstants.LottieUrls.GENERIC_LOADING
                statusText = getCustomString(MR.strings.feature_lending_payment_pending)
                binding.btnAction.setDisabled(true)
                binding.btnAction.setText(getCustomString(MR.strings.feature_lending_pay_again))
            }
            PAYMENT_STATUS_FAILURE -> {
                url = LendingConstants.LottieUrls.GENERIC_ERROR
                statusText = getCustomString(MR.strings.feature_lending_payment_failed)
                binding.btnAction.setText(getCustomString(MR.strings.feature_lending_pay_again))
            }
        }
        binding.lottieView.playLottieWithUrlAndExceptionHandling(requireContext(), url)
        binding.tvStatus.text = statusText
        binding.tvAmountTitle.text = args.title
        binding.tvAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, args.amount.orZero().toInt().getFormattedAmount())
        val spannable = buildSpannedString {
            append(getCustomString(MR.strings.feature_lending_for_concern_dispute_please))
            append(" ")
            underline {
                append(getCustomString(MR.strings.feature_lending_contact_us))
            }
        }
        binding.tvContactUs.text = spannable

        analyticsApi.postEvent(
            event = LendingEventKeyV2.Repay_PostPaymentScreenLaunched, values =
            mapOf(
                LendingEventKeyV2.payment_status to args.status
            )
        )
    }

    private fun setupListeners() {
        binding.tvContactUs.setDebounceClickListener {
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), "")
        }

        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_PostPaymentScreenClicked, values =
                mapOf(
                    LendingEventKeyV2.button_type to binding.btnAction.getText(),
                    LendingEventKeyV2.payment_status to args.status
                )
            )
            if (args.status == PAYMENT_STATUS_SUCCESS)
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(LendingConstants.STATUS_SCREEN_ACTION, Pair<String, Float>(LendingConstants.SCREEN_ACTION_DONE, 0f))
            else if (args.status == PAYMENT_STATUS_FAILURE)
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(LendingConstants.STATUS_SCREEN_ACTION, Pair<String, Float>(LendingConstants.SCREEN_ACTION_PAY_AGAIN, args.amount))
            popBackStack()
        }

        binding.ivBack.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_PostPaymentScreenClicked, values =
                mapOf(
                    LendingEventKeyV2.button_type to LendingEventKeyV2.cancel
                )
            )
            popBackStack()
        }
    }

    companion object {
        const val PAYMENT_STATUS_SUCCESS = "PAYMENT_STATUS_SUCCESS"
        const val PAYMENT_STATUS_PENDING = "PAYMENT_STATUS_PENDING"
        const val PAYMENT_STATUS_FAILURE = "PAYMENT_STATUS_FAILURE"
    }
}