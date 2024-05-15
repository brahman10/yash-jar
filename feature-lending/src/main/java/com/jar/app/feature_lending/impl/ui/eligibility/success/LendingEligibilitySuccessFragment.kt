package com.jar.app.feature_lending.impl.ui.eligibility.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingEligibilitySuccessBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.app.feature_lending.shared.util.LendingConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class LendingEligibilitySuccessFragment : BaseFragment<FragmentLendingEligibilitySuccessBinding>() {

    private val args by navArgs<LendingEligibilitySuccessFragmentArgs>()

    private val preApprovedData: PreApprovedData by lazy {
        args.preApprovedData
    }

    private var redirectionJob: Job? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingEligibilitySuccessBinding
        get() = FragmentLendingEligibilitySuccessBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Don't allow user to navigate back
                EventBus.getDefault()
                    .post(LendingBackPressEvent(LendingEventKeyV2.RCASH_ELIGIBILTY_SCREEN))
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = false, LendingStep.KYC)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        startRedirectionTimer()
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            LendingConstants.LottieUrls.ROTATING_COIN
        )
        binding.lottieViewCoinRain.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            LendingConstants.LottieUrls.COIN_RAIN
        )

        val creditScore = preApprovedData.creditScore.orZero()
        val spannable = if (creditScore > 0) buildSpannedString {
            append(getCustomStringFormatted(MR.strings.feature_lending_your_credit_score_is_x_and_lending_partner, creditScore))
            append(" ")
            color(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)) {
                append(preApprovedData.lenderName)
            }
        }
        else
            buildSpannedString {
                append(getCustomString(MR.strings.feature_lending_your_lending_partner_is))
                append(" ")
                color(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)) {
                    append(preApprovedData.lenderName)
                }
            }

        binding.tvCreditScore.text = spannable
        binding.tvCreditScore.isVisible = true
        binding.tvEligibleAmount.text = getCustomStringFormatted(MR.strings.feature_lending_currency_sign_x_str, preApprovedData.availableLimit.toString())

        preApprovedData.lenderLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivLender)
        } ?: kotlin.run {
            binding.ivLender.isVisible = false
        }

        preApprovedData.creditProviderLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivCreditProvider)
        } ?: kotlin.run {
            binding.ivCreditProvider.isVisible = false
        }
    }

    private fun startRedirectionTimer() {
        redirectionJob?.cancel()
        redirectionJob = uiScope.launch {
            countDownTimer(totalMillis = 2_000, onFinished = {

            })
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        redirectionJob?.cancel()
    }
}