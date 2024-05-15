package com.jar.app.feature_lending_kyc.impl.ui.step_view

import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycCellVerticalStepBinding
import com.jar.app.feature_lending_kyc.impl.data.KycStep
import com.jar.app.feature_lending_kyc.impl.data.KycStepStatus

internal class VerticalStepViewHolder(
    private val binding: FeatureLendingKycCellVerticalStepBinding,
    private val adapter: KycStepsAdapter
) : BaseStepHolder(binding) {
    override fun bindStep(step: KycStep) {
        binding.tvStepTitle.text = step.text
        binding.tvStepStatus.text = context.getString(step.status.statusText)
        binding.tvStepStatus.setTextColor(
            ContextCompat.getColor(
                context,
                step.status.statusTextColor
            )
        )
        binding.tvStepTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (step.status == KycStepStatus.NOT_YET_VISITED) com.jar.app.core_ui.R.color.color_776E94
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