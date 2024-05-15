package com.jar.app.core_ui.pause_savings

import com.jar.app.core_ui.databinding.CoreUiCellPauseDurationBinding
import com.jar.app.core_ui.view_holder.BaseViewHolder
import dev.icerock.moko.resources.StringResource

class PauseSavingsViewHolder constructor(
    private val binding: CoreUiCellPauseDurationBinding,
    private val onClick: (pauseOption: com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper, position: Int) -> Unit
) : BaseViewHolder(binding.root) {

    private var pauseOption: com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper? = null

    init {
        binding.root.setOnClickListener {
            if (pauseOption != null) {
                onClick.invoke(pauseOption!!, bindingAdapterPosition)
            }
        }
    }

    fun setPauseOption(pauseSavingOptionWrapper: com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper) {
        this.pauseOption = pauseSavingOptionWrapper
        binding.tvNumDuration.text = pauseSavingOptionWrapper.pauseSavingOption.timeValue.toString()
        binding.tvDurationHeader.text =
            getCustomString(StringResource(pauseSavingOptionWrapper.pauseSavingOption.durationType.durationRes.resourceId))
        binding.root.isSelected = pauseSavingOptionWrapper.isSelected
    }
}