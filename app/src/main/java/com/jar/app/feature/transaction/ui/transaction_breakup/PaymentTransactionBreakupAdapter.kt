package com.jar.app.feature.transaction.ui.transaction_breakup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.databinding.CellTransactionBreakupBinding
import com.jar.app.feature_transaction.shared.domain.model.Transaction

class PaymentTransactionBreakupAdapter :
    ListAdapter<Transaction, PaymentTransactionBreakupViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.txnId == newItem.txnId
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentTransactionBreakupViewHolder {
        val binding = CellTransactionBreakupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentTransactionBreakupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentTransactionBreakupViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setTransactionBreakup(it)
        }
    }
}