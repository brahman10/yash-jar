package com.jar.app.core_ui.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Spannable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.CoreUiPriceValidityTimerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class PriceValidityTimer @JvmOverloads constructor(
    private val ctx: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    var binding: CoreUiPriceValidityTimerBinding =
        CoreUiPriceValidityTimerBinding.inflate(LayoutInflater.from(ctx), this, true)
    private var animation: ObjectAnimator? = null
    private var buyPriceTimerJob: Job? = null
    private var shouldShowLiveTag: Boolean = false

    fun setProgressColor(color: String) {
        binding.goldPriceProgress.progressTintList = ColorStateList.valueOf(Color.parseColor(color))
    }

    fun setTextAppearance(@StyleRes resId: Int) {
        TextViewCompat.setTextAppearance(binding.tvLiveMessage, resId)
        TextViewCompat.setTextAppearance(binding.tvValidFor, resId)
    }

    fun start(
        livePriceMessage: String,
        validityInMillis: Long,
        uiScope: CoroutineScope,
        onInterval: (interval: Long) -> Unit = {},
        onFinish: () -> Unit = {},
        spannedPriceMessage: Spannable? = null
    ) {
        setTimerState(TimerState.SHOW_TIMER)
        binding.tvLiveMessage.text = spannedPriceMessage ?: livePriceMessage
        animation?.cancel()
        binding.goldPriceProgress.isVisible = true
        animation = ObjectAnimator.ofFloat(
            binding.goldPriceProgress,
            "translationX",
            0f,
            -binding.root.width.toFloat()
        )
        animation?.duration = validityInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.start()

        buyPriceTimerJob?.cancel()
        buyPriceTimerJob = uiScope.countDownTimer(
            validityInMillis,
            onInterval = {
                onInterval(it)
                binding.tvValidFor.text = ctx.getString(R.string.core_ui_valid_for_s, it.milliSecondsToCountDown())
                binding.goldPriceProgress.scaleY = 10f
                binding.goldPriceProgress.progress = it.toInt()
            },
            onFinished = {
                onFinish()
            }
        )
    }

    fun shouldShowLiveTag(shouldShow: Boolean) {
        this.shouldShowLiveTag = shouldShow
    }

    fun setProgressBarTintColor(color: Int) {
        binding.goldPriceProgress.progressTintList = null
        binding.goldPriceProgress.progressTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context, color
            )
        )
    }

    fun setRootBackground(color: Int) {
        binding.root.setBackgroundColor(
            ContextCompat.getColor(
                context, color
            )
        )
    }

    fun setTimerState(currentState: TimerState) {
        when (currentState) {
            TimerState.FETCHING_PRICE,
            TimerState.NEW_PRICE_FETCHED -> {
                binding.shimmerFetchingPrice.startShimmer()
                binding.tvFetchingPriceState.setText(currentState.text)
                binding.tvFetchingPriceState.setTextColor(
                    ContextCompat.getColor(
                        context, currentState.textColor
                    )
                )
                binding.groupTimer.isVisible = false
                binding.tvLiveTag.isVisible = false
                binding.groupFetchingPrice.isVisible = true
            }
            TimerState.SHOW_TIMER -> {
                binding.shimmerFetchingPrice.stopShimmer()
                binding.groupTimer.isVisible = true
                binding.tvLiveTag.isVisible = shouldShowLiveTag
                binding.groupFetchingPrice.isVisible = false
            }
        }
    }

    enum class TimerState(@ColorRes val textColor: Int, @StringRes val text: Int) {
        FETCHING_PRICE(R.color.white, R.string.core_ui_fetching_new_gold_price),
        NEW_PRICE_FETCHED(R.color.color_58DDC8, R.string.core_ui_new_gold_price_fetched),
        SHOW_TIMER(0,0)
    }
}