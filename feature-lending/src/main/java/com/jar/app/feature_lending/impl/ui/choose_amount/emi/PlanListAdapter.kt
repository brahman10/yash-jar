package com.jar.app.feature_lending.impl.ui.choose_amount.emi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.CellItemEmiPlansBinding
import com.jar.app.feature_lending.shared.domain.model.temp.CreditLineScheme
import com.jar.app.feature_lending.shared.MR

internal class PlanListAdapter(
    private val onItemClick: (scheme: CreditLineScheme) -> Unit
) : ListAdapter<CreditLineScheme, PlanListAdapter.EmiPlanViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CreditLineScheme>() {
            override fun areItemsTheSame(oldItem: CreditLineScheme, newItem: CreditLineScheme): Boolean {
                return oldItem.tenure == newItem.tenure
            }

            override fun areContentsTheSame(oldItem: CreditLineScheme, newItem: CreditLineScheme): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmiPlanViewHolder {
        val binding = CellItemEmiPlansBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmiPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmiPlanViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class EmiPlanViewHolder(
        private val binding: CellItemEmiPlansBinding
    ) : BaseViewHolder(binding.root) {

        private var scheme: CreditLineScheme? = null

        init {
            binding.root.setDebounceClickListener {
                scheme?.let {
                    onItemClick(it)
                }
            }
        }

        fun bindData(data: CreditLineScheme) {
            scheme = data
            if (data.isSelected) {
                binding.expandableLayout.expand()
                binding.root.setBackgroundResource(R.drawable.feature_lending_bg_select_emi_card)
                binding.viewSelectedLine.isVisible = true
                binding.seperator.isVisible = true
            } else {
                binding.viewSelectedLine.isVisible = false
                binding.seperator.isInvisible = true
                binding.expandableLayout.collapse()
                binding.root.setBackgroundResource(R.drawable.feature_lending_bg_select_emi_unselected_card)
            }
            binding.tvMonth.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (data.isSelected) R.drawable.feature_lending_ic_radio_selected else R.drawable.feature_lending_ic_radio_unselected,
                0,
                0,
                0
            )
            binding.tvMonth.text = getCustomStringFormatted(MR.strings.feature_lending_month_prefix, data.tenure)
            binding.firstDate.text = data.firstEmiDate
            binding.lastDate.text = data.lastEmiDate
            binding.tvRecommended.isVisible = data.isRecommended

            binding.tvAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, data.amountPerMonth.getFormattedAmount())
            data.repaymentAmount?.let {
                binding.repaymentAmountTitle.isVisible = true
                binding.repaymentAmount.isVisible = true
                binding.repaymentAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, it.getFormattedAmount())
            }?:run{
                binding.repaymentAmountTitle.isVisible = false
                binding.repaymentAmount.isVisible = false
            }
        }
    }
}