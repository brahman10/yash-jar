package com.jar.app.feature_spin.impl.ui

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.core_analytics.EventKey.FeatureType
import com.jar.app.core_analytics.EventKey.Screen
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_spin.databinding.FragmentJackpotBinding
import com.jar.app.feature_spin.impl.custom.util.getWidthAndHeight
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.CTA
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ClickedButtonRewardsScreen
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ScreenJackpotJar
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ShownSpinRewardsScreen
import com.jar.app.core_base.domain.model.JackpotResponse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class SpinJackpotFragment : BaseDialogFragment<FragmentJackpotBinding>() {

    private val args by navArgs<SpinJackpotFragmentArgs>()

    private val jackpotResponse: JackpotResponse by lazy {
        args.resultJackpot
    }

    private var rotationAnimator: ValueAnimator? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentJackpotBinding
        get() = FragmentJackpotBinding::inflate

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val widthAndHeight = getWidthAndHeight(this.requireActivity())
        val verticalMargin = widthAndHeight.first * 0.1
        val horizontalMargin = widthAndHeight.second * 0.1
        dialog?.window?.setLayout(
            widthAndHeight.first - verticalMargin.toInt(),
            widthAndHeight.second - horizontalMargin.toInt()
        )
    }

    override fun setup() {
        setupUI()
        setupClickListener()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setValidForCounter(jackpotResponse.spinCouponMetadata?.validity)
        }
        val  percentAgeCoupons = if ((jackpotResponse.spinCouponMetadata?.title?.length ?: 0) >= 2)
            jackpotResponse.spinCouponMetadata?.title?.substring(0,2)
        else ""
        analyticsHandler.postEvent(
            ShownSpinRewardsScreen,
            mapOf(
                FeatureType to FeatureTypeSpinAndWin,
                Screen to "${ScreenJackpotJar}${percentAgeCoupons}"
            )
        )
    }

    private fun setupClickListener() {
        with(binding) {
            val  percentAgeCoupons = if ((jackpotResponse.spinCouponMetadata?.title?.length ?: 0) >= 2)
                jackpotResponse.spinCouponMetadata?.title?.substring(0,2)
            else ""
            //commenting this because of product requirement
//            ivClose.setDebounceClickListener {
//                analyticsHandler.postEvent(
//                    ClickedButtonRewardsScreen,
//                    mapOf(
//                        FeatureType to FeatureTypeSpinAndWin,
//                        Screen to "${ScreenJackpotJar}${percentAgeCoupons}",
//                        CTA to Close
//                    )
//                )
//                popBackStack()
//            }
            btnPlayAgain.setDebounceClickListener {
                analyticsHandler.postEvent(
                    ClickedButtonRewardsScreen,
                    mapOf(
                        FeatureType to FeatureTypeSpinAndWin,
                        Screen to "${ScreenJackpotJar}${percentAgeCoupons}",
                        CTA to jackpotResponse.spinAgainCta?.text!!
                    )
                )
                popBackStack()
            }
            btnRedeemCoupon.setDebounceClickListener {
                jackpotResponse.redeemCouponCta?.deeplink?.let { it ->
                    analyticsHandler.postEvent(
                        ClickedButtonRewardsScreen,
                        mapOf(
                            FeatureType to FeatureTypeSpinAndWin,
                            Screen to "${ScreenJackpotJar}${percentAgeCoupons}",
                            CTA to jackpotResponse.redeemCouponCta?.text!!
                        )
                    )
                    EventBus.getDefault().post(
                        HandleDeepLinkEvent(
                            it
                        )
                    )
                }
            }
        }
    }

    private fun setupUI() {
        with(binding) {
            setupRotationAnimation()
            startRotationAnimation()

            tvHeading.text = "${jackpotResponse.header}"

            tvMessageTwo.text = "${jackpotResponse.spinCouponMetadata?.title}"
            tvCouponDescription.text = "${jackpotResponse.spinCouponMetadata?.description}"

            btnRedeemCoupon.text = "${jackpotResponse.redeemCouponCta?.text}"
            btnPlayAgain.text = "${jackpotResponse.spinAgainCta?.text}"

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setValidForCounter(validity: Long?) {
        validity?.takeIf { it != 0L }?.let { validity ->
            val till = Instant.ofEpochMilli(validity).atZone(ZoneId.systemDefault())
            val now = Instant.now().atZone(ZoneId.systemDefault())
            val timeLeft = (till.toEpochSecond() - now.toEpochSecond()) * 1000
            uiScope.countDownTimer(
                timeLeft,
                onInterval = {
                    val values = it.milliSecondsToCountDown(true)

                    val arr = values.split(":")

                    val hour = arr.getOrNull(0)
                    val minute = arr.getOrNull(1)
                    val second = arr.getOrNull(2)

                    binding.tvTxt1.text = hour?.getOrNull(0)?.toString()
                    binding.tvTxt2.text = hour?.getOrNull(1)?.toString()

                    binding.tvTxt3.text = minute?.getOrNull(0)?.toString()
                    binding.tvTxt4.text = minute?.getOrNull(1)?.toString()

                    binding.tvTxt5.text = second?.getOrNull(0)?.toString()
                    binding.tvTxt6.text = second?.getOrNull(1)?.toString()
                },
                onFinished = {

                }
            )
        } ?: kotlin.run {
            binding.tvTxt1.isVisible = false
            binding.tvTxt2.isVisible = false
            binding.tvTxt3.isVisible = false
            binding.tvTxt4.isVisible = false
            binding.tvTxt5.isVisible = false
            binding.tvTxt6.isVisible = false
            binding.tvLabelValidFor.isVisible = false
            binding.tvTxtHour.isVisible = false
            binding.tvTxtSecond.isVisible = false
            binding.tvTxtMinute.isVisible = false
        }
    }

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(
            true,
            shouldShowFullScreen = true
        )

    private fun setupRotationAnimation() {
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 6000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animation ->
                val rotation = animation.animatedValue as Float
                if (isBindingInitialized())
                    binding.appCompatImageView.rotation = rotation
            }
        }
    }

    private fun startRotationAnimation() {
        if (rotationAnimator?.isStarted == false) {
            rotationAnimator?.start()
        }
    }

    private fun stopRotationAnimation() {
        if (rotationAnimator?.isStarted == true) {
            rotationAnimator?.cancel()
        }
    }

    private fun zoomInAndOutAnimation() {
        if (isBindingInitialized()) {
            // Create the zoom in animation
            uiScope.launch {
                binding.ivGoldBox.animate()
                    ?.scaleX(4f)
                    ?.scaleY(4f)
                    ?.setDuration(2000)
                    ?.withEndAction {
                        // When the zoom in animation ends, start the zoom out animation
                        if (isBindingInitialized()) {
                            binding.ivGoldBox.animate()
                                .scaleX(3.8f)
                                .scaleY(3.8f)
                                .setDuration(2000)
                                .withEndAction {
                                    zoomInAndOutAnimation()
                                }
                                .start()
                        }
                    }
                    ?.start()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBindingInitialized()) {
            stopRotationAnimation()
            binding.ivGoldBox.clearAnimation()
        }
    }

}