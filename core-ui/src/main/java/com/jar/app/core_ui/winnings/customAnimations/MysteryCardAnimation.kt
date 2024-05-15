package com.jar.app.core_ui.winnings.customAnimations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.MysteryCardAnimationBinding
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.util.dp
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard

class MysteryCardAnimation(
    context: Context,
    private val isForTransactionScreen: Boolean,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var binding: MysteryCardAnimationBinding? = null
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
        binding = MysteryCardAnimationBinding.inflate(LayoutInflater.from(context))
        binding?.let {
            addView(it.root)
        }
        animUp =
            ObjectAnimator.ofFloat(binding!!.backgroundRotating, "translationY", 0f, -10f).apply {
                duration = 1000
                interpolator = linearInterpolator
            }
        animDown =
            ObjectAnimator.ofFloat(binding!!.backgroundRotating, "translationY", -10f, 0f).apply {
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
        binding?.apply {
            val countText = if (postPaymentRewardCard.bannerText.isNullOrBlank().not()
                && (postPaymentRewardCard.bannerText?.get(0) ?: "") == '+'
            ) {
                postPaymentRewardCard.bannerText?.substring(
                    1,
                    postPaymentRewardCard.bannerText?.length.orZero()
                )
            } else {
                postPaymentRewardCard.bannerText
            }
            if (isForTransactionScreen) {
                root.background = ContextCompat.getDrawable(context, R.drawable.winnings_rounded_background)
                animationBg.background = null
                tvCount.visibility = View.INVISIBLE
                tvCountSuccess.visibility = View.VISIBLE
                tvCountSuccess.text = postPaymentRewardCard.bannerText

                val newRootLayoutParam = LayoutParams(
                    120.dp,
                    135.dp
                ).apply {
                    setPadding(45, 0, 0, 0)
                }

                root.layoutParams = newRootLayoutParam

                ConstraintSet().apply {
                    clone(root)
                    setMargin(root.id, ConstraintSet.START, 100)
                    connect(
                        tvTitle.id,
                        ConstraintSet.TOP,
                        backgroundRotating.id,
                        ConstraintSet.BOTTOM
                    )
                }.applyTo(root)
            } else {
                tvCount.text = context.getString(R.string.banner_text, countText).replace(" ", "")
            }

            tvTitle.text = postPaymentRewardCard.title

            postPaymentRewardCard?.trophyImage?.let {
                rewardImage?.let { it1 ->
                    Glide.with(context)
                        .load(it)
                        .into(it1)
                }
            }
            postPaymentRewardCard?.ctaText?.let {
                awardText.text = it
                awardText.isVisible = true
            } ?: let {
                awardText.isVisible = false
            }

            postPaymentRewardCard?.takeIf { it.targetCards.orZero() > 0 && it.cardsWon.orZero() >= 0 }?.let {
                var percent = it.cardsWon.orZero().toFloat().div(it.targetCards.orZero()).coerceAtMost(0.87f).coerceAtLeast(0.18f)
                guideline.setGuidelinePercent(percent) // it.targetCards
                awardContainer.isVisible = true
                tickImage.isInvisible = it.targetCards == it.cardsWon
            } ?: run {
                awardContainer.isVisible = false
            }

            postPaymentRewardCard.boundaryColor?.let {
                if (it.equals("#1ea787", ignoreCase = true)) {
                    animationBg.background = ContextCompat.getDrawable(context, R.drawable.winnings_background_1ea787_v2)
                } else if (it.equals("#7745ff", ignoreCase = true)) {
                    animationBg.background = ContextCompat.getDrawable(context, R.drawable.winnings_background_7745ff_v2)
                }

                DrawableCompat.setTint(indicatorLine.background, Color.parseColor(it))
                DrawableCompat.setTint(tvCountSuccess.background, Color.parseColor(it))
                DrawableCompat.setTint(tvCount.background, Color.parseColor(it))
            }
            postPaymentRewardCard.lottieUrl?.let {
                animationBg.playLottieWithUrlAndExceptionHandling(
                    context,
                    it
                )
                backgroundRotating.isVisible = false
            } ?: run {
                backgroundRotating.isVisible = true
            }
        }
    }
}