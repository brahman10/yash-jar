package com.jar.app.feature_round_off.impl.ui.round_off_settings.round_off_resumed


import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_round_off.databinding.FeatureRoundOffResumedBinding
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.Duration
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffResumedFragment  : BaseBottomSheetDialogFragment<FeatureRoundOffResumedBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var animation: ObjectAnimator? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffResumedBinding
        get() = FeatureRoundOffResumedBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            skipCollapsed = true,
            shouldShowFullHeight = true,
            isCancellable = false,
            isDraggable = false
        )

    override fun setup() {
        setupUI()
        analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_Success_RoundOffResumedScreen)
    }

    private fun setupUI() {
        binding.animationView.playLottieWithUrlAndExceptionHandling(requireContext(),"${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/confetti.json")

        val durationInMillis = Duration.ofSeconds(3).toMillis()

        animation = ObjectAnimator.ofInt(binding.progressHorizontal, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.doOnEnd {
            dismissAllowingStateLoss()
        }
        animation?.start()
    }

    override fun onDestroyView() {
        animation?.cancel()
        super.onDestroyView()
    }
}