package com.jar.app.feature_lending.impl.ui.step_view

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepStatus

internal abstract class LendingBaseStepHolder(binding: ViewBinding) : BaseViewHolder(binding.root) {

    abstract fun bindStep(step: LendingProgressStep)

    protected fun getMarkerContainerBackground(step: LendingProgressStep): Drawable? {
        return ContextCompat.getDrawable(
            context,
            if (step.status == LendingStepStatus.PENDING)
                R.drawable.feature_lending_bg_solid_mark
            else
                R.drawable.feature_lending_bg_d5cdf2_ring
        )
    }

    protected fun shouldHideBeforeLine() = bindingAdapterPosition == 0

    protected fun shouldHideAfterLine(stepSize: Int) = bindingAdapterPosition == stepSize - 1

    protected fun getStepNumberTextColor(step: LendingProgressStep): Int {
        return ContextCompat.getColor(
            context,
            if (step.status == LendingStepStatus.PENDING) com.jar.app.core_ui.R.color.color_776E94 else com.jar.app.core_ui.R.color.white
        )
    }

    protected fun shouldShowTickIcon(step: LendingProgressStep) = step.status == LendingStepStatus.COMPLETED

    protected fun shouldShowStepNumberText(step: LendingProgressStep) = step.status != LendingStepStatus.COMPLETED

    protected fun getBeforeLineBackgroundColor(step: LendingProgressStep): Int {
        return ContextCompat.getColor(
            context,
            if (step.status == LendingStepStatus.PENDING) com.jar.app.core_ui.R.color.color_3C3357 else com.jar.app.core_ui.R.color.white
        )
    }

    protected fun getAfterLineBackgroundColor(step: LendingProgressStep): Int {
        return ContextCompat.getColor(
            context,
            if (step.status == LendingStepStatus.COMPLETED) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_3C3357
        )
    }
}