package com.jar.app.feature_daily_investment.impl.ui.auto_pay_redirection

import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.api.domain.event.SetupAutoPayEvent
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentAutopayRedirectionBinding
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import javax.inject.Inject


@AndroidEntryPoint
internal class AutoPayRedirectionFragment :
    BaseFragment<FeatureDailyInvestmentFragmentAutopayRedirectionBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentAutopayRedirectionBinding
        get() = FeatureDailyInvestmentFragmentAutopayRedirectionBinding::inflate

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    private var animation: ObjectAnimator? = null

    private val args by navArgs<AutoPayRedirectionFragmentArgs>()

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }

    private var glide: RequestManager? = null
    private var target: CustomTarget<Drawable> = object : CustomTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
            uiScope.launch {
                binding.ivSparkleLightBg.background = resource
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }


    companion object {
        private const val SCREEN_TIMER = 3000L
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()

        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/DailyInvestment/next_step_setup_mandate.json"
        )
        binding.tvStartNowAndGrowUpto.text = getString(
            R.string.feature_daily_investment_if_you_save_x_youll_earn_xgm,
            args.dailySavingAmount.toInt(),
            args.goldVolume
        )
        glide = Glide.with(this)
        glide?.load(BaseConstants.ImageUrlConstants.SPARKLE_LIGHT_BG)
            ?.into(target)

        startProgressAnimation()
    }

    private fun startProgressAnimation() {
        binding.progressHorizontal.isVisible = true
        val durationInMillis = Duration.ofMillis(SCREEN_TIMER).toMillis()

        animation = ObjectAnimator.ofInt(binding.progressHorizontal, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.start()
        animation?.doOnEnd {
            EventBus.getDefault().post(
                SetupAutoPayEvent(
                    oldDailySavingAmount = 0,
                    mandateAmount = args.mandateAmount,
                    newDailySavingAmount = args.dailySavingAmount,
                    isDailySavingAutoPayFlow = true,
                    shouldDirectlyShowAppSelectionScreen = true,
                    flowName = MandatePaymentEventKey.FeatureFlows.SetupDailySaving
                )
            )
        }
    }

    override fun onDestroyView() {
        glide?.clear(target)
        super.onDestroyView()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }
}