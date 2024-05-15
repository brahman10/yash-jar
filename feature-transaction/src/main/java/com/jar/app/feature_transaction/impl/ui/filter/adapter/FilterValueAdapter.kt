package com.jar.app.feature_transaction.impl.ui.filter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellFilterOptionBinding
import com.jar.app.feature_transaction.impl.domain.model.getDateString
import com.jar.app.feature_transaction.shared.domain.model.FilterValueData

class FilterValueAdapter(
    private val onFilterValueSelected: (filterValueData: FilterValueData) -> Unit
) : ListAdapter<FilterValueData, FilterValueAdapter.FilterValueVH>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FilterValueData>() {
            override fun areItemsTheSame(
                oldItem: FilterValueData,
                newItem: FilterValueData
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: FilterValueData,
                newItem: FilterValueData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterValueVH {
        val binding =
            FeatureTransactionCellFilterOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterValueVH(binding)
    }

    override fun onBindViewHolder(holder: FilterValueVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class FilterValueVH(
        private val binding: FeatureTransactionCellFilterOptionBinding
    ) : BaseViewHolder(binding.root) {

        private var filterValueData: FilterValueData? = null

        init {
            binding.root.setDebounceClickListener {
                filterValueData?.let {
                    onFilterValueSelected(it)
                }
            }
        }

        fun bindData(data: FilterValueData) {
            filterValueData = data
            binding.tvTitle.text = data.displayName
            if (data.keyName.equals(BaseConstants.FilterValues.DATE_FILTER, true)) {
                binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    if (data.name.equals(BaseConstants.FilterValues.DATE_FILTER_CUSTOM, true)) {
                        if (data.isSelected.orFalse())
                            com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selected
                        else
                            com.jar.app.core_ui.R.drawable.core_ui_ic_calender
                    } else if (data.isSelected.orFalse())
                        com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selected
                    else
                        com.jar.app.core_ui.R.drawable.core_ui_bg_radio_unselected,
                    0
                )
                if (data.name.equals(
                        BaseConstants.FilterValues.DATE_FILTER_CUSTOM,
                        true
                    ) && getDateString(data).isNullOrBlank().not()
                ) {
                    binding.tvSubtitle.isVisible = true
                    binding.tvSubtitle.text = getDateString(data)
                } else
                    binding.tvSubtitle.isVisible = false
            } else {
                binding.tvSubtitle.isVisible = false
                binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    if (data.isSelected.orFalse()) R.drawable.feature_transaction_ic_checkbox_green_seleced
                    else R.drawable.feature_transaction_ic_checkbox_green_unselectd,
                    0
                )
            }
        }
    }
}