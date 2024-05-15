package com.jar.app.core_ui.pull_to_refresh_overlay

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.CommonPullToRefreshOverlayBinding
import com.jar.app.core_ui.listener.OnSwipeTouchListener

@SuppressLint("ClickableViewAccessibility")
class PullToRefreshOverlayView @JvmOverloads constructor(
    private val ctx: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    private var pullDownListener: PullToRefreshListener? = null

    init {
        val binding = CommonPullToRefreshOverlayBinding.inflate(LayoutInflater.from(ctx), this, true)
        binding.ivHand.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.slide_down))
        binding.root.setOnTouchListener(object : OnSwipeTouchListener(ctx) {
            override fun onSwipeDown() {
                super.onSwipeDown()
                pullDownListener?.onPulledToRefresh()
            }

            override fun onClickedSomewhere() {
                pullDownListener?.onClickedSomewhereElse()
            }
        })
    }

    fun setPullListener(listener: PullToRefreshListener) {
        this.pullDownListener = listener
    }
}