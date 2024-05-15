package com.jar.app.feature.promo_code.ui.success

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.databinding.FragmentPromoCodeSuccessBinding
import com.jar.app.base.data.event.RefreshPromoCodeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.extension.scrollToBottom
import com.jar.app.core_ui.extension.setDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import javax.inject.Inject

@AndroidEntryPoint
internal class PromoCodeSuccessFragment : BaseFragment<FragmentPromoCodeSuccessBinding>() {

    companion object {
        private const val SCREEN_TIMER = 3000L
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<PromoCodeSuccessFragmentArgs>()

    private var animation: ObjectAnimator? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPromoCodeSuccessBinding
        get() = FragmentPromoCodeSuccessBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        binding.root.scrollToBottom()
        analyticsHandler.postEvent(
            EventKey.Shown_RewardCredited_PromoCodeScreen,
            mapOf(
                EventKey.AMOUNT to args.applyPromoCodeResponse.amount.orZero(),
                EventKey.PromoCode to args.applyPromoCodeResponse.promoCode.orEmpty()
            )
        )
    }

    private fun setupUI() {
        binding.tvTitle.text = args.applyPromoCodeResponse.title
        binding.tvDescription.text = args.applyPromoCodeResponse.description
        binding.tvAmount.isVisible =
            args.applyPromoCodeResponse.amount != null && args.applyPromoCodeResponse.amount != 0.0
        binding.tvAmount.text =
            getString(R.string.rupee_x_in_double, args.applyPromoCodeResponse.amount)
        binding.btnNext.setText(getString(R.string.okay_2))
        startProgressBar()
    }

    private fun setupListeners() {
        binding.btnNext.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun startProgressBar() {
        binding.progressHorizontal.isVisible = true
        animation = ObjectAnimator.ofInt(binding.progressHorizontal, "progress", 0, 100)
        animation?.duration = Duration.ofMillis(SCREEN_TIMER).toMillis()
        animation?.interpolator = LinearInterpolator()

        animation?.doOnEnd {
            popBackStack()
        }

        animation?.start()
    }

    override fun onDestroyView() {
        animation?.cancel()
        EventBus.getDefault().postSticky(RefreshPromoCodeEvent())
        super.onDestroyView()
    }
}