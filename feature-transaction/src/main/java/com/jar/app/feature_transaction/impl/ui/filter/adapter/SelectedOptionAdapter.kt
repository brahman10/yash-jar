package com.jar.app.feature_transaction.impl.ui.filter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellFilterChipsBinding
import com.jar.app.feature_transaction.impl.domain.model.getDateString
import com.jar.app.feature_transaction.shared.domain.model.FilterValueData

class SelectedOptionAdapter(
    private val onFilterRemoved: (filterValueData: FilterValueData) -> Unit
) : ListAdapter<FilterValueData, SelectedOptionAdapter.FilterTypeVH>(DIFF_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterTypeVH {
        val binding =
            FeatureTransactionCellFilterChipsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterTypeVH(binding)
    }

    override fun onBindViewHolder(holder: FilterTypeVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class FilterTypeVH(
        private val binding: FeatureTransactionCellFilterChipsBinding
    ) : BaseViewHolder(binding.root) {

        private var filterValueData: FilterValueData? = null

        init {
            binding.ivClose.setDebounceClickListener {
                filterValueData?.let {
                    onFilterRemoved(it)
                }
            }
        }

        fun bindData(data: FilterValueData) {
            filterValueData = data
            binding.tvVpaName.text =
                if (data.name.equals(
                        BaseConstants.FilterValues.DATE_FILTER_CUSTOM,
                        true
                    )
                ) getDateString(data)
                else data.displayName
        }
    }
}