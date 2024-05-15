package com.jar.app.feature_gold_sip.impl.ui.pause_sip

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellPauseDurationBinding
import dev.icerock.moko.resources.StringResource

internal class PauseGoldSipViewHolder constructor(
    private val binding: FeatureGoldSipCellPauseDurationBinding,
    private val onClick: (pauseOption: PauseGoldSipOption, position: Int) -> Unit
) : BaseViewHolder(binding.root) {

    private var pauseOption: PauseGoldSipOption? = null

    init {
        binding.root.setOnClickListener {
            if (pauseOption != null) {
                onClick.invoke(pauseOption!!, bindingAdapterPosition)
            }
        }
    }

    fun setPauseOption(pauseGoldSipOption: PauseGoldSipOption) {
        this.pauseOption = pauseGoldSipOption
        binding.tvNumDuration.text = pauseGoldSipOption.pauseSavingOption.timeValue.toString()
        val string = getCustomString(StringResource( pauseGoldSipOption.pauseSavingOption.durationType.durationRes))
        binding.tvDurationHeader.text = string
        binding.root.isSelected = pauseGoldSipOption.isSelected
    }
}