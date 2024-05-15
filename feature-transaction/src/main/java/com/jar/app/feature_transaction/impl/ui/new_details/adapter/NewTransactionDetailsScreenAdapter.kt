package com.jar.app.feature_transaction.impl.ui.new_details.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.*

internal class NewTransactionDetailsScreenAdapter(delegates: List<AdapterDelegate<List<NewTransactionDetailsCardView>>>) :
    AsyncListDifferDelegationAdapter<NewTransactionDetailsCardView>(ITEM_CALLBACK) {

    init {
        delegates.forEach {
            delegatesManager.addDelegate(it)
        }
    }

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<NewTransactionDetailsCardView>() {
            override fun areItemsTheSame(
                oldItem: NewTransactionDetailsCardView,
                newItem: NewTransactionDetailsCardView
            ): Boolean {
                return oldItem.getSortKey() == newItem.getSortKey()
            }

            override fun areContentsTheSame(
                oldItem: NewTransactionDetailsCardView,
                newItem: NewTransactionDetailsCardView
            ): Boolean {
                return if (oldItem is TransactionHeaderCard && newItem is TransactionHeaderCard)
                    oldItem == newItem
                else if (oldItem is TransactionOrderDetailsComponent && newItem is TransactionOrderDetailsComponent)
                    oldItem == newItem
                else if (oldItem is TransactionStatusCard && newItem is TransactionStatusCard)
                    oldItem == newItem
                else if (oldItem is NewTransactionContactUsCard && newItem is NewTransactionContactUsCard)
                    oldItem == newItem
                else
                    false
            }
        }
    }
}