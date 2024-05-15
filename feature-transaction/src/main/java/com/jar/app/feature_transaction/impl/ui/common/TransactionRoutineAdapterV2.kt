package com.jar.app.feature_transaction.impl.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellTransactionRoutineBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnRoutine

class TransactionRoutineAdapterV2 : ListAdapter<com.jar.app.feature_transaction.shared.domain.model.TxnRoutine, TransactionRoutineViewHolderV2>(
    DIFF_CALLBACK
) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_transaction.shared.domain.model.TxnRoutine>() {
            override fun areItemsTheSame(oldItem: com.jar.app.feature_transaction.shared.domain.model.TxnRoutine, newItem: com.jar.app.feature_transaction.shared.domain.model.TxnRoutine): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: com.jar.app.feature_transaction.shared.domain.model.TxnRoutine, newItem: com.jar.app.feature_transaction.shared.domain.model.TxnRoutine): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionRoutineViewHolderV2 {
        val binding = FeatureTransactionCellTransactionRoutineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionRoutineViewHolderV2(binding)
    }

    override fun onBindViewHolder(holder: TransactionRoutineViewHolderV2, position: Int) {
        getItem(position)?.let {
            holder.setTransactionRoutine(it)
        }
    }
}