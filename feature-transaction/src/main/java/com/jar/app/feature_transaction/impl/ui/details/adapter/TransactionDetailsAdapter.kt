package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellTransactionDetailsBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetails

class TransactionDetailsAdapter : ListAdapter<com.jar.app.feature_transaction.shared.domain.model.TxnDetails, TransactionDetailsAdapter.TxnDetailsVH>(
    DIFF_CALLBACK
) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_transaction.shared.domain.model.TxnDetails>() {
            override fun areItemsTheSame(oldItem: com.jar.app.feature_transaction.shared.domain.model.TxnDetails, newItem: com.jar.app.feature_transaction.shared.domain.model.TxnDetails): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: com.jar.app.feature_transaction.shared.domain.model.TxnDetails, newItem: com.jar.app.feature_transaction.shared.domain.model.TxnDetails): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TxnDetailsVH {
        val binding = FeatureTransactionCellTransactionDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TxnDetailsVH(binding)
    }

    override fun onBindViewHolder(holder: TxnDetailsVH, position: Int) {
        getItem(position)?.let {
            holder.setTxnDetails(it)
        }
    }

    inner class TxnDetailsVH(
        private val binding: FeatureTransactionCellTransactionDetailsBinding
    ) : BaseViewHolder(binding.root) {

        fun setTxnDetails(data: com.jar.app.feature_transaction.shared.domain.model.TxnDetails) {
            binding.tvTxnType.text = data.title
            binding.tvTxnTypeDetail.text = data.value
        }
    }
}