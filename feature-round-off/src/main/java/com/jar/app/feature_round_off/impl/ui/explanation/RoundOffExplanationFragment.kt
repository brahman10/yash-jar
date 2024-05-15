package com.jar.app.feature_round_off.impl.ui.explanation

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.hasSmsPermission
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentExplanationBinding
import com.jar.app.feature_round_off.shared.util.RoundOffConstants
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffExplanationFragment :
    BaseFragment<FeatureRoundOffFragmentExplanationBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentExplanationBinding
        get() = FeatureRoundOffFragmentExplanationBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args: RoundOffExplanationFragmentArgs by navArgs()

    private val roundOffLottieUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it.animatedValue.toString().toFloat().orZero() * 100).toInt()
        uiScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.lpiProgress.setProgress(progress, true)
            } else {
                binding.lpiProgress.progress = progress
            }
            if (progress == 0) {
                dismissProgressBar()
            }
            if (progress == 100) {
                goToNext()
            }
        }
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvSkip.paint.isUnderlineText = true
        binding.roundOffLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(), com.jar.app.feature_round_off.shared.util.RoundOffConstants.Lottie.ROUND_OFF_EDUCATION
        )
        binding.roundOffLottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                goToNext()
            }
        })
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_Education_Screen,
            mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
        )

        binding.roundOffLottie.addAnimatorUpdateListener(roundOffLottieUpdateListener)
    }

    private fun setupListeners() {
        binding.tvSkip.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_Education_Screen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.SkipClicked)
            )
            goToNext()
        }
    }

    private fun goToNext() {

        navigateTo(
            if (requireContext().hasSmsPermission())
                if (args.isHelpFlow)
                    NavigationRoundOffDirections.actionToRoundOffCalculatedFragment(
                        clickTime = args.clickTime,
                        screenFlow = args.fromScreen
                    )
                else
                    NavigationRoundOffDirections.actionToRoundOffCalculationLoadingFragment(
                        clickTime = args.clickTime,
                        screenFlow = args.fromScreen
                    )
            else
                NavigationRoundOffDirections.actionToSmsPermissionFromRoundOffFragment(),
            popUpTo = R.id.roundOffExplanationFragment,
            inclusive = true
        )
    }
}