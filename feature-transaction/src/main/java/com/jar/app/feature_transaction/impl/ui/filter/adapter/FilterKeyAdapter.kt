package com.jar.app.feature_transaction.impl.ui.filter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellFilterTypeBinding
import com.jar.app.feature_transaction.shared.domain.model.FilterKeyData

class FilterKeyAdapter(
    private val onFilterKeySelected: (filterKeyData: FilterKeyData) -> Unit
) : ListAdapter<FilterKeyData, FilterKeyAdapter.FilterKeyVH>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FilterKeyData>() {
            override fun areItemsTheSame(oldItem: FilterKeyData, newItem: FilterKeyData): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: FilterKeyData,
                newItem: FilterKeyData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterKeyVH {
        val binding =
            FeatureTransactionCellFilterTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterKeyVH(binding)
    }

    override fun onBindViewHolder(holder: FilterKeyVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class FilterKeyVH(
        private val binding: FeatureTransactionCellFilterTypeBinding
    ) : BaseViewHolder(binding.root) {

        private var filterKeyData: FilterKeyData? = null

        init {
            binding.root.setDebounceClickListener {
                filterKeyData?.let {
                    onFilterKeySelected(it)
                }
            }
        }

        fun bindData(data: FilterKeyData) {
            filterKeyData = data
            binding.root.text = data.displayName
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (data.isSelected.orFalse()) com.jar.app.core_ui.R.color.color_604E9F else com.jar.app.core_ui.R.color.color_2E2942
                )
            )
        }
    }
}