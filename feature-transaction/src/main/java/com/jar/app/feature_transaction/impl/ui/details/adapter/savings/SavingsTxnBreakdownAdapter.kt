package com.jar.app.feature_transaction.impl.ui.details.adapter.savings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellTxnBreakdownDetailsBinding
import com.jar.app.feature_transaction.shared.domain.model.SavingBreakdownListItem

class SavingsTxnBreakdownAdapter :
    ListAdapter<com.jar.app.feature_transaction.shared.domain.model.SavingBreakdownListItem, SavingsTxnBreakdownAdapter.SavingsTxnBreakdownViewHolder>(
        DIFF_UTIL
    ) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<com.jar.app.feature_transaction.shared.domain.model.SavingBreakdownListItem>() {
            override fun areItemsTheSame(
                oldItem: com.jar.app.feature_transaction.shared.domain.model.SavingBreakdownListItem,
                newItem: com.jar.app.feature_transaction.shared.domain.model.SavingBreakdownListItem
            ): Boolean {
                return oldItem.label == newItem.label
            }

            override fun areContentsTheSame(
                oldItem: com.jar.app.feature_transaction.shared.domain.model.SavingBreakdownListItem,
                newItem: com.jar.app.feature_transaction.shared.domain.model.SavingBreakdownListItem
            ): Boolean {
                return oldItem.label == newItem.label
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavingsTxnBreakdownViewHolder {
        val binding = FeatureTransactionCellTxnBreakdownDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavingsTxnBreakdownViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavingsTxnBreakdownViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class SavingsTxnBreakdownViewHolder(
        private val binding: FeatureTransactionCellTxnBreakdownDetailsBinding
    ) : BaseViewHolder(binding.root) {

        fun bind(data: com.jar.app.feature_transaction.shared.domain.model.SavingBreakdownListItem) {
            binding.tvLabel.text = data.label.orEmpty()
            binding.tvValue.text = data.value.orEmpty()
        }

    }

}