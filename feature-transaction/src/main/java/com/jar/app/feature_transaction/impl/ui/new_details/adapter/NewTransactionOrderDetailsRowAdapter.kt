package com.jar.app.feature_transaction.impl.ui.new_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellOrderDetailsRowBinding
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.OrderDetailsCardList

class NewTransactionOrderDetailsRowAdapter :
    ListAdapter<OrderDetailsCardList, NewTransactionOrderDetailsRowAdapter.NewTransactionOrderDetailsRowViewHolder>(
        DIFF_UTIL
    ) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<OrderDetailsCardList>() {
            override fun areItemsTheSame(
                oldItem: OrderDetailsCardList,
                newItem: OrderDetailsCardList
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: OrderDetailsCardList,
                newItem: OrderDetailsCardList
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class NewTransactionOrderDetailsRowViewHolder(
        private val binding: FeatureTransactionCellOrderDetailsRowBinding
    ) : BaseViewHolder(binding.root) {

        private var transactionTitleValuePair: TransactionTitleValuePairAdapter? = null
        private val spaceItemDecorationVertical = SpaceItemDecoration(0.dp, 4.dp)

        fun bind(data: OrderDetailsCardList) {
            binding.tvPostOrderDetailsTitle.isVisible = data.title != null
            binding.tvPostOrderDetailsTitle.setHtmlText(data.title.orEmpty())
            transactionTitleValuePair = TransactionTitleValuePairAdapter {
                context.copyToClipboard(it.value.orEmpty())
            }
            binding.rvPostOrderDetailsList.adapter = transactionTitleValuePair
            binding.rvPostOrderDetailsList.layoutManager = LinearLayoutManager(context)
            binding.rvPostOrderDetailsList.addItemDecorationIfNoneAdded(spaceItemDecorationVertical)
            transactionTitleValuePair?.submitList(data.orderDetailsCardRowList)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewTransactionOrderDetailsRowViewHolder {
        val binding = FeatureTransactionCellOrderDetailsRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NewTransactionOrderDetailsRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewTransactionOrderDetailsRowViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}