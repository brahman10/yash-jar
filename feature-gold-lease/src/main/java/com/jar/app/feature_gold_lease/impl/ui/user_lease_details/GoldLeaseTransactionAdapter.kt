package com.jar.app.feature_gold_lease.impl.ui.user_lease_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_lease.databinding.FeatureGoldLeaseCellTransactionWinningBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTransaction

internal class GoldLeaseTransactionAdapter(
    private val onTransactionClicked: (goldLeaseTransaction: GoldLeaseTransaction) -> Unit
) : ListAdapter<GoldLeaseTransaction, GoldLeaseTransactionViewHolder>(
    DIFF_UTIL
) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<GoldLeaseTransaction>() {
            override fun areItemsTheSame(
                oldItem: GoldLeaseTransaction,
                newItem: GoldLeaseTransaction
            ): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(
                oldItem: GoldLeaseTransaction,
                newItem: GoldLeaseTransaction
            ): Boolean {
                return oldItem.orderId == newItem.orderId
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoldLeaseTransactionViewHolder {
        val binding = FeatureGoldLeaseCellTransactionWinningBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoldLeaseTransactionViewHolder(binding, onTransactionClicked)
    }

    override fun onBindViewHolder(holder: GoldLeaseTransactionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}