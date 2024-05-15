package com.jar.app.core_ui.step

import com.jar.app.core_ui.databinding.CoreUiItemStepBinding
import com.jar.app.core_ui.view_holder.BaseViewHolder

class StepViewHolder(private val binding: CoreUiItemStepBinding) : BaseViewHolder(binding.root) {

    fun setStep(step: Step) {
        binding.tvStep.text = context.getString(step.textRes)
        binding.ivStepIcon.setImageResource(step.iconRes)
    }
}