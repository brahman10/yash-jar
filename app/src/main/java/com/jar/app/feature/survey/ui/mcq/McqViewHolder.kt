package com.jar.app.feature.survey.ui.mcq

import com.jar.app.R
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellMcqBinding
import com.jar.app.feature.survey.domain.model.ChoiceWrapper
import com.jar.app.core_ui.extension.setDebounceClickListener

class McqViewHolder(
    private val binding: CellMcqBinding,
    private val onClick: (Int,ChoiceWrapper) -> Unit
) : BaseViewHolder(binding.root) {

    private lateinit var choiceWrapper: ChoiceWrapper

    init {
        binding.root.setDebounceClickListener {
            if (::choiceWrapper.isInitialized)
                onClick.invoke(bindingAdapterPosition,choiceWrapper)
        }
    }

    fun setMcq(choiceWrapper: ChoiceWrapper) {
        this.choiceWrapper = choiceWrapper
        binding.tvMcqChoice.text = choiceWrapper.choice
        binding.tvMcqChoice.isSelected = choiceWrapper.isSelected
        if (choiceWrapper.isSelected)
            binding.tvMcqChoice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, com.jar.app.core_ui.R.drawable.ic_tick_green, 0)
        else
            binding.tvMcqChoice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_tick_gray, 0)

    }
}