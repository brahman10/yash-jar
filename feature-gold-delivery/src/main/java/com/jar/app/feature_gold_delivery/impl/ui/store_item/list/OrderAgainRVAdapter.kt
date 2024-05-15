package com.jar.app.feature_gold_delivery.impl.ui.store_item.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_delivery.databinding.OrderAgainCardBinding
import com.jar.app.feature_transaction.shared.domain.model.TransactionData

class OrderAgainRVAdapter(
    private val onOrderAgainFlow: (transactionData: TransactionData) -> Unit,
    private val onTrackHistory: ((transactionData: TransactionData) -> Unit)?
) : ListAdapter<TransactionData, OrderAgainViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<TransactionData>() {
            override fun areItemsTheSame(oldItem: TransactionData, newItem: TransactionData): Boolean {
                return oldItem == newItem // todo compare with id
            }

            override fun areContentsTheSame(oldItem: TransactionData, newItem: TransactionData): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAgainViewHolder {
        val binding =
            OrderAgainCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderAgainViewHolder(binding, onOrderAgainFlow, onTrackHistory)
    }

    override fun onBindViewHolder(holder: OrderAgainViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setStoreItem(it)
        }
    }
}