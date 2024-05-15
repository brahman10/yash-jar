package com.jar.app.core_ui.winnings.customAnimations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.WeeklyMagicAnimationBinding
import com.jar.app.core_ui.util.dp
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard

class WeeklyMagicAnimation(context: Context, private val isForTransactionScreen: Boolean, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private var binding: WeeklyMagicAnimationBinding? = null
    private var animUp: ObjectAnimator? = null
    private var animDown: ObjectAnimator? = null
    private val linearInterpolator: LinearInterpolator by lazy {
        LinearInterpolator()
    }

    // Play the animation
    private val animSet: AnimatorSet by lazy {
        AnimatorSet()
    }

    init {
        binding = WeeklyMagicAnimationBinding.inflate(LayoutInflater.from(context))
        binding?.let {
            addView(it.root)
        }
        animUp =
            ObjectAnimator.ofFloat(binding!!.backgroundRotating, "translationY", 0f, -20f).apply {
                duration = 1000
                interpolator = linearInterpolator
            }

        animDown =
            ObjectAnimator.ofFloat(binding!!.backgroundRotating, "translationY", -20f, 0f).apply {
                duration = 1000
                interpolator = linearInterpolator
            }

        animSet.doOnEnd {
            animSet.start()
        }
        animSet.playSequentially(animUp, animDown)
        animSet.start()
    }

    fun setData(postPaymentRewardCard: PostPaymentRewardCard) {
        val countText = if (postPaymentRewardCard?.bannerText?.toCharArray()
                ?.isNotEmpty() == true && (postPaymentRewardCard?.bannerText?.toCharArray()?.get(0)
                ?: "") == '+'
        )
            postPaymentRewardCard.bannerText?.substring(1, postPaymentRewardCard.bannerText?.length.orZero())
        else
            postPaymentRewardCard.title
        binding?.apply {
            if (isForTransactionScreen) {
                root.background =
                    ContextCompat.getDrawable(context, R.drawable.winnings_rounded_background)
                val newRootLayoutParam = LayoutParams(
                    120.dp,
                    135.dp,
                ).apply {
                    setPadding(45,0,0,0)
                }
                root.layoutParams = newRootLayoutParam
                tvYou.visibility = View.GONE
                tvWon.visibility = View.GONE
                tvCountSuccess.visibility = View.VISIBLE

                ConstraintSet().apply {
                    clone(root)
                    connect(
                        tvTitle.id,
                        ConstraintSet.TOP,
                        backgroundRotating.id,
                        ConstraintSet.BOTTOM
                    )
                }.applyTo(root)

            }
            tvTitle.text = countText

            postPaymentRewardCard?.ctaText?.let {
                awardText.text = it
                awardText.isVisible = true
            } ?: let {
                awardText.isVisible = false
            }

            postPaymentRewardCard?.takeIf { it.targetCards.orZero() > 0 && it.cardsWon.orZero() >= 0 }?.let {
                if (it.targetCards == it.cardsWon) {
                    tickImage.isVisible = false
                    guideline.setGuidelinePercent(0.9f)
                } else {
                    awardContainer.isVisible = true
                    tickImage.isVisible = true
                    val percent = it.cardsWon.orZero().toFloat().div(it.targetCards.orZero()).coerceAtMost(0.85f)
                    guideline.setGuidelinePercent(percent) // it.targetCards
                }
            } ?: run {
                awardContainer.isVisible = false
            }

            postPaymentRewardCard?.trophyImage?.let {
                binding?.rewardImage?.let { it1 ->
                    Glide.with(context)
                        .load(it)
                        .into(it1)
                }
            }
            postPaymentRewardCard.boundaryColor?.let {
                if (it.equals("#1ea787", ignoreCase = true)) {
                    animationBg.background = ContextCompat.getDrawable(context, R.drawable.winnings_background_1ea787_v2)
                } else if (it.equals("#7745ff", ignoreCase = true)) {
                    animationBg.background = ContextCompat.getDrawable(context, R.drawable.winnings_background_7745ff_v2)
                }

                DrawableCompat.setTint(indicatorLine.background, Color.parseColor(it))
                DrawableCompat.setTint(tvCountSuccess.background, Color.parseColor(it))
                DrawableCompat.setTint(tvYou.background, Color.parseColor(it))
                DrawableCompat.setTint(tvWon.background, Color.parseColor(it))

                val drawable = tickImage.drawable

                if(drawable != null) {
                    val wrappedDrawable = DrawableCompat.wrap(drawable.mutate())
                    DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(Color.parseColor(it)))
                    tickImage.setImageDrawable(wrappedDrawable)
                }
            }

            postPaymentRewardCard.bannerImage?.let {
                backgroundRotating?.let { it1 ->
                    Glide.with(context)
                        .load(it)
                        .into(it1)
                }
            }
        }
    }

    fun startAnimation() {
        if (animSet.isStarted.not()) {
            animSet.start()
        }
    }

    fun pauseAnimation() {
        if (animSet.isStarted) {
            animSet.pause()
        }
    }

    fun resumeAnimation() {
        if (animSet.isPaused) {
            animSet.resume()
        } else {
            startAnimation()
        }
    }

    fun stopAnimation() {
        if (animSet.isStarted) {
            animSet.end()
        }
    }
}