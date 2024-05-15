package com.jar.app.feature.transaction.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.databinding.CellTransactionRoutineBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnRoutine

class TransactionRoutineAdapter : ListAdapter<com.jar.app.feature_transaction.shared.domain.model.TxnRoutine, TransactionRoutineViewHolder>(DIFF_CALLBACK) {

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
    ): TransactionRoutineViewHolder {
        val binding = CellTransactionRoutineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionRoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionRoutineViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setTransactionRoutine(it)
        }
    }
}