package com.jar.app.feature_round_off.impl.ui.save_method

import androidx.core.view.isVisible
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_round_off.databinding.FeatureRoundOffCellStepsBinding

class RoundOffStepsViewHolder(private val binding: FeatureRoundOffCellStepsBinding) :
    BaseViewHolder(binding.root) {

    fun setTitle(title: String, listSize: Int) {
        binding.tvTitle.text = title
        binding.tvNumber.text = (bindingAdapterPosition + 1).toString()

        binding.topLine.isVisible = (bindingAdapterPosition != 0)
        binding.bottomLine.isVisible = (bindingAdapterPosition != listSize - 1)
    }
}