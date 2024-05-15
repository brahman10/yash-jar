package com.jar.app.feature_transaction.impl.ui.details.adapter.gold_lease

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellTxnBreakdownDetailsBinding
import com.jar.app.feature_transaction.shared.domain.model.LeaseBreakdownListItem

internal class GoldLeaseTxnBreakdownAdapter :
    ListAdapter<LeaseBreakdownListItem, GoldLeaseTxnBreakdownAdapter.GoldLeaseTxnBreakdownViewHolder>
        (DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LeaseBreakdownListItem>() {
            override fun areItemsTheSame(
                oldItem: LeaseBreakdownListItem,
                newItem: LeaseBreakdownListItem
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: LeaseBreakdownListItem,
                newItem: LeaseBreakdownListItem
            ): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoldLeaseTxnBreakdownViewHolder {
        val binding = FeatureTransactionCellTxnBreakdownDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoldLeaseTxnBreakdownViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoldLeaseTxnBreakdownViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class GoldLeaseTxnBreakdownViewHolder(
        private val binding: FeatureTransactionCellTxnBreakdownDetailsBinding
    ) : BaseViewHolder(binding.root) {

        fun bind(data: LeaseBreakdownListItem) {
            binding.tvLabel.text = data.title
            binding.tvValue.text = data.volume?.let {
                getCustomStringFormatted(
                    itemView.context,
                    MR.strings.feature_buy_gold_v2_x_gm,
                    it
                )
            }
        }

    }

}