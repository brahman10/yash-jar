package com.jar.app.feature_lending.impl.ui.step_view

import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.jar.app.feature_lending.databinding.CellLendingHorizontalStepBinding
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepStatus

internal class LendingHorizontalStepViewHolder(
    private val binding: CellLendingHorizontalStepBinding,
    private val adapter: LendingStepAdapter
) : LendingBaseStepHolder(binding) {
    override fun bindStep(step: LendingProgressStep) {
        binding.tvStepTitle.text = context.getString(step.titleResId.resourceId)
        binding.tvStepTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (step.status == LendingStepStatus.FAILURE) com.jar.app.core_ui.R.color.color_776E94
                else com.jar.app.core_ui.R.color.white
            )
        )
        binding.tvStepTitle.textSize = 12f
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