package com.jar.app.feature_lending.impl.ui.step_view

import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.jar.app.feature_lending.databinding.CellLendingVerticalStepBinding
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepStatus

internal class LendingVerticalStepViewHolder(
    private val binding: CellLendingVerticalStepBinding,
    private val adapter: LendingStepAdapter
) : LendingBaseStepHolder(binding) {
    override fun bindStep(step: LendingProgressStep) {
        binding.tvStepTitle.text = getCustomString(step.titleResId)
        binding.tvStepStatus.text = getCustomString(step.status.statusText)
        binding.tvStepStatus.setTextColor(
            ContextCompat.getColor(
                context,
                step.status.statusTextColor.resourceId
            )
        )
        binding.tvStepTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (step.status == LendingStepStatus.PENDING) com.jar.app.core_ui.R.color.color_776E94
                else com.jar.app.core_ui.R.color.white
            )
        )
        binding.lineTop.isGone = shouldHideBeforeLine()
        binding.lineBottom.isGone = shouldHideAfterLine(adapter.itemCount)
        binding.lineTop.setBackgroundColor(getBeforeLineBackgroundColor(step))
        binding.lineBottom.setBackgroundColor(getAfterLineBackgroundColor(step))
        binding.markContainer.background = getMarkerContainerBackground(step)
        binding.ivTickIcon.isVisible = shouldShowTickIcon(step)
        binding.tvStepNumber.text = "${bindingAdapterPosition + 1}"
        binding.tvStepNumber.isVisible = shouldShowStepNumberText(step)
        binding.tvStepNumber.setTextColor(getStepNumberTextColor(step))

    }
}