package com.jar.app.feature_daily_investment.impl.ui.daily_savings_resumed

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.feature_daily_investment.databinding.FeatureDailySavingsFragmentDailySavingsResumedBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingsResumedFragment :
    BaseDialogFragment<FeatureDailySavingsFragmentDailySavingsResumedBinding>() {

    private var animation: ObjectAnimator? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailySavingsFragmentDailySavingsResumedBinding
        get() = FeatureDailySavingsFragmentDailySavingsResumedBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        analyticsHandler.postEvent(DailySavingsEventKey.Shown_Success_DailySavingsResumedScreen)
        setupUI()
    }
    private fun setupUI() {
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        binding.animationView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/confetti.json"
        )

        val durationInMillis = Duration.ofSeconds(3).toMillis()

        animation = ObjectAnimator.ofInt(binding.progressHorizontal, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.doOnEnd {
            EventBus.getDefault().post(RefreshDailySavingEvent())
            dismissAllowingStateLoss()
        }
        animation?.start()
    }

    override fun onDestroyView() {
        animation?.cancel()
        super.onDestroyView()
    }
}