package com.jar.app.feature_buy_gold_v2.impl.ui.order_status.custom_card

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_buy_gold_v2.databinding.WeeklyChallengeRewardNewBinding
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard

class WeeklyMagicNew @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding: WeeklyChallengeRewardNewBinding? = null

    private val fromYDelta = -10f
    private val toYDelta = 10f
    val cardAnimationDuration = 1000L
    private var cardAnimation: TranslateAnimation? = null
    private val CTA_PLOTLINE_TAG = "btnUnlockCardsOrderStatus"

    init {
        binding = WeeklyChallengeRewardNewBinding.inflate(LayoutInflater.from(context))
        binding?.let {
            addView(it.root)
        }
    }

    fun setData(postPaymentRewardCard: PostPaymentRewardCard,onButtonClicked:(String)->Unit) {

        binding?.apply {
            btnAction.setPlotlineViewTag(CTA_PLOTLINE_TAG)
            postPaymentRewardCard.bannerText?.let { _bannerText ->
                labelTop.text = _bannerText
                labelTop.isVisible = true
            } ?: {
                labelTop.isVisible = false
            }

            tvTitle.text=postPaymentRewardCard.title
            tvSecondaryText.text =
                postPaymentRewardCard.secondaryTitle?.let { _secondaryText ->
                    HtmlCompat.fromHtml(_secondaryText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            tvTertiaryText.text = postPaymentRewardCard.tertiaryTitle?.let{_tertiaryTitle->
                    HtmlCompat.fromHtml(_tertiaryTitle, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            if(postPaymentRewardCard.targetCards!=null && postPaymentRewardCard.cardsWon!=null){
            createStepView(postPaymentRewardCard.targetCards!!,postPaymentRewardCard.cardsWon!!)
            }
            postPaymentRewardCard.ctaText?.let { btnAction.setText(it) }
            btnAction.setDebounceClickListener {
                postPaymentRewardCard.deepLink?.let { _deeplink ->
                    onButtonClicked(_deeplink)
                }
            }

            cardAnimation = TranslateAnimation(0f, 0f, fromYDelta, toYDelta).apply {
                duration = cardAnimationDuration
                repeatMode = Animation.REVERSE
                repeatCount = Animation.INFINITE
            }


            ivCard.startAnimation(cardAnimation)
        }
    }

    private fun createStepView(numberOfSteps: Int, currentStep: Int) {
        for (i in 0 until numberOfSteps) {
            var layoutParams: LinearLayout.LayoutParams?
            val lineView = View(context)
            val imageView = ImageView(context)
            val drawable: Drawable? = when {
                i < currentStep -> {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, LAYOUT_WEIGHT)
                    ContextCompat.getDrawable(
                    context,
                    com.jar.app.feature_buy_gold_v2.R.drawable.green_tick
                )}

                i < numberOfSteps - 1 -> {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, LAYOUT_WEIGHT)
                    ContextCompat.getDrawable(
                    context,
                    com.jar.app.feature_buy_gold_v2.R.drawable.dot_cirlce
                )
                }

                else -> {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f)
                    ContextCompat.getDrawable(
                        context,
                        com.jar.app.feature_buy_gold_v2.R.drawable.trophy_1
                    )
                }
            }


            imageView.setImageDrawable(drawable)
            imageView.layoutParams = layoutParams
            binding?.stepView?.addView(imageView)

            if (i < numberOfSteps - 1) {
                // Add line view except for the last step

               // val lineLayoutParams = ViewGroup.LayoutParams(50, 4)
                val lineLayoutParams = LinearLayout.LayoutParams(0, LINE_HEIGHT, LAYOUT_WEIGHT)
                if (i < currentStep - 1) {
                    lineView.setBackgroundColor(Color.parseColor("#1EA787"))
                } else {
                    lineView.setBackgroundResource(com.jar.app.feature_buy_gold_v2.R.drawable.feature_wm_reward_dotted_line)
                }

                lineView.layoutParams = lineLayoutParams
                binding?.stepView?.addView(lineView)
            }
        }
    }

    private fun teardown() {
        binding?.ivCard?.clearAnimation()
        cardAnimation?.cancel()
        cardAnimation = null
    }

    override fun onDetachedFromWindow() {
        teardown()
        super.onDetachedFromWindow()
    }
}
private const val LINE_HEIGHT = 4
private const val LAYOUT_WEIGHT = 1f

