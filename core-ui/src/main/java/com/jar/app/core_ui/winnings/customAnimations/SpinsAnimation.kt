package com.jar.app.core_ui.winnings.customAnimations

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.SpinsAnimationBinding
import com.jar.app.core_ui.util.applyRoundedRectBackground
import com.jar.app.core_ui.util.dp
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard

class SpinsAnimation(
    context: Context,
    private val isForTransactionScreen: Boolean,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: SpinsAnimationBinding? = null
    private var rotateAnimation: ObjectAnimator? = null

    init {
        binding = SpinsAnimationBinding.inflate(LayoutInflater.from(context))
        binding?.let {
            addView(it.root)
        }
        rotateAnimation =
            ObjectAnimator.ofFloat(binding?.backgroundRotating, View.ROTATION, 0f, 360f).apply {
                duration = 30000
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()
            }
        rotateAnimation?.start()
    }

    fun setData(postPaymentRewardCard: PostPaymentRewardCard) {
        binding?.apply {
            if (isForTransactionScreen) {
                root.background = ContextCompat.getDrawable(context, R.drawable.winnings_rounded_background)
                animationBg.background = null
                tvCountSpins.visibility = View.INVISIBLE
                tvCountSuccess2Spins.visibility = View.VISIBLE
                tvCountSuccess2Spins.text = postPaymentRewardCard.bannerText
                val newRootLayoutParam = LayoutParams(
                    120.dp,
                    135.dp
                )
                root.layoutParams = newRootLayoutParam
                ConstraintSet().apply {
                    clone(root)
                    connect(
                        tvTitle.id,
                        ConstraintSet.TOP,
                        backgroundRotating.id,
                        ConstraintSet.BOTTOM
                    )
                }.applyTo(root)

            } else {
                tvCountSpins.text = if (postPaymentRewardCard.bannerText?.startsWith("+") == true) {
                    postPaymentRewardCard.bannerText
                } else {
                    "+" + postPaymentRewardCard.bannerText.orEmpty().trim()
                }
            }
            tvTitle.text = postPaymentRewardCard.title

            postPaymentRewardCard?.ctaText?.let {
                awardText.text = it
                awardText.isVisible = true
            } ?: let {
                awardText.isVisible = false
            }

            postPaymentRewardCard.boundaryColor?.let {
                if (it.equals("#1ea787", ignoreCase = true)) {
                    animationBg.background = ContextCompat.getDrawable(context, R.drawable.winnings_background_1ea787_v2)
                } else if (it.equals("#7745ff", ignoreCase = true)) {
                    animationBg.background = ContextCompat.getDrawable(context, R.drawable.winnings_background_7745ff_v2)
                }

                applyRoundedRectBackground(
                    tvCountSuccess2Spins,
                    Color.parseColor(it),
                    context.resources.getDimensionPixelSize(R.dimen.dimen_12dp).toFloat()
                )
                applyRoundedRectBackground(
                    tvCountSpins,
                    Color.parseColor(it),
                    context.resources.getDimensionPixelSize(R.dimen.dimen_12dp).toFloat()
                )
            }
        }
    }

    fun startAnimation() {
        if (rotateAnimation?.isStarted == false) {
            rotateAnimation?.start()
        }
    }

    fun pauseAnimation() {
        if (rotateAnimation?.isStarted == true) {
            rotateAnimation?.pause()
        }
    }

    fun resumeAnimation() {
        if (rotateAnimation?.isPaused == true) {
            rotateAnimation?.resume()
        } else {
            startAnimation()
        }
    }

    fun stopAnimation() {
        if (rotateAnimation?.isStarted == true) {
            rotateAnimation?.end()
        }
    }
}