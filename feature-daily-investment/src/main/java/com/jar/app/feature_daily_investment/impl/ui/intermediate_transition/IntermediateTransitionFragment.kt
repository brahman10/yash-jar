package com.jar.app.feature_daily_investment.impl.ui.intermediate_transition

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.feature_daily_investment.databinding.FeatureDailySavingsFragmentIntermediateTransitionBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration

@AndroidEntryPoint
internal class IntermediateTransitionFragment :
    BaseFragment<FeatureDailySavingsFragmentIntermediateTransitionBinding>() {
    private var animation: ObjectAnimator? = null

    private val args by navArgs<IntermediateTransitionFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailySavingsFragmentIntermediateTransitionBinding
        get() = FeatureDailySavingsFragmentIntermediateTransitionBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
    }

    private fun setupUi() {
        dismissProgressBar()
        val durationInMillis = Duration.ofSeconds(3).toMillis()
        animation = ObjectAnimator.ofInt(binding.progressHorizontal, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.doOnEnd {
            EventBus.getDefault().post(RefreshDailySavingEvent())
            popBackStack()
        }
        animation?.start()
        Glide.with(requireContext()).load(args.data.illustrationRes).into(binding.ivIllustration)
        binding.tvHeader.text = args.data.title
        binding.tvDesc.text = args.data.subtitle
        binding.progressHorizontal.isVisible = args.data.shouldShowProgress
        if (args.data.shouldShowConfettiAnimation)
            binding.animationView.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/confetti.json"
            )
    }

    override fun onDestroyView() {
        animation?.cancel()
        super.onDestroyView()
    }
}