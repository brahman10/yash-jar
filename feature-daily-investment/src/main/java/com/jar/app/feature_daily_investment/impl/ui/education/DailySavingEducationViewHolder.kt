package com.jar.app.feature_daily_investment.impl.ui.education

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentCellEducationDsBinding
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingEducation

internal class DailySavingEducationViewHolder(
    private val binding: FeatureDailyInvestmentCellEducationDsBinding,
    private val onClick: (Int, DailySavingEducation, Boolean) -> Unit
) : BaseViewHolder(binding.root) {

    fun setStepsData(dailySavingEducation: DailySavingEducation) {
        binding.tvNumber.text = (bindingAdapterPosition + 1).toString()
        binding.tvTitle.text = dailySavingEducation.title
        binding.elImageContainer.isExpanded = dailySavingEducation.isExpanded
        binding.dashedCircleView.setImageResource(
            if (dailySavingEducation.isListIterated) R.drawable.feature_daily_investment_dotted_circle_14aca1d3
            else if (dailySavingEducation.isExpanded) R.drawable.feature_daily_investment_dotted_circle_14ebb46a
            else R.drawable.feature_daily_investment_dotted_circle_14ffffff
        )
        binding.tvNumber.setTextColor(
            ContextCompat.getColor(
                binding.tvTitle.context,
                if (dailySavingEducation.isListIterated) com.jar.app.core_ui.R.color.color_ACA1D3
                else if (dailySavingEducation.isExpanded) com.jar.app.core_ui.R.color.color_EBB46A
                else com.jar.app.core_ui.R.color.white
            )
        )
        binding.tvTitle.setTextColor(
            ContextCompat.getColor(
                binding.tvTitle.context,
                if (dailySavingEducation.isListIterated) com.jar.app.core_ui.R.color.color_ACA1D3
                else if (dailySavingEducation.isExpanded) com.jar.app.core_ui.R.color.color_EBB46A
                else com.jar.app.core_ui.R.color.white
            )
        )
        binding.tvDescription.text = dailySavingEducation.description
        binding.ivDropDown.setImageResource(if (dailySavingEducation.isExpanded) com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_up else com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_down)
        Glide.with(binding.root.context).load(dailySavingEducation.imageUrl).into(binding.ivDetail)

        binding.ivDropDown.setDebounceClickListener {
            dropDownToggleListener(dailySavingEducation)
        }
        binding.tvTitle.setDebounceClickListener {
            dropDownToggleListener(dailySavingEducation)
        }
    }

    private fun dropDownToggleListener(dailySavingEducation: DailySavingEducation){
        onClick.invoke(
            bindingAdapterPosition.inc(),
            dailySavingEducation,
            !binding.elImageContainer.isExpanded
        )
        binding.elImageContainer.isExpanded = !binding.elImageContainer.isExpanded
        binding.ivDropDown.setImageResource(
            if (binding.elImageContainer.isExpanded) com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_up
            else com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_down
        )
    }
}