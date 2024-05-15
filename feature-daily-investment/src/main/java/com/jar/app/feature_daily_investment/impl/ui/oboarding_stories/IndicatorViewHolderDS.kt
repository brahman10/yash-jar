package com.jar.app.feature_daily_investment.impl.ui.oboarding_stories

import android.animation.ValueAnimator
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.databinding.CellIndicatorViewBinding
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.shared.domain.model.OnboardingStoryIndicatorData

class IndicatorViewHolderDS(
    private val binding: CellIndicatorViewBinding
) :
    BaseViewHolder(binding.root) {

    private val startWidth = 4.dp
    private val endWidth = 28.dp
    private var animator: ValueAnimator = ValueAnimator.ofInt(startWidth, endWidth)

    private var listener = ValueAnimator.AnimatorUpdateListener {
        val layoutParams = binding.cvProgressBar.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = animator.animatedValue as Int
        binding.cvProgressBar.layoutParams = layoutParams
    }

    init {
        animator.addUpdateListener(listener)
    }

    fun bind(data: OnboardingStoryIndicatorData){
        if (data.isSelected) {
            selectCell()
        }
        if (data.isSelected.not()) {
            deselectCell()
        }
    }

    fun selectCell() {
        val layoutParams = binding.root.layoutParams
        layoutParams.width = 28.dp
        binding.root.layoutParams = layoutParams
        binding.cvProgressBar.visibility = View.VISIBLE
        cellAnimation(BaseConstants.AnimationOperation.START)
    }

    fun deselectCell() {
        binding.cvProgressBar.isVisible = false
        val layoutParams = binding.root.layoutParams
        layoutParams.width = 8.dp
        binding.root.layoutParams = layoutParams
        cellAnimation(BaseConstants.AnimationOperation.START)
    }

    fun cellAnimation(operation: String) {
        animator.duration = 6000L
        if (operation == BaseConstants.AnimationOperation.START) {
            animator.start()
        } else if (operation == BaseConstants.AnimationOperation.PAUSE) {
            animator.pause()
        } else if (operation == BaseConstants.AnimationOperation.RESUME) {
            if (animator.isPaused) {
                animator.resume()
            }
        } else if (operation == BaseConstants.AnimationOperation.STOP) {
            if (animator.isRunning) {
                animator.cancel()
            }
        }
    }
}