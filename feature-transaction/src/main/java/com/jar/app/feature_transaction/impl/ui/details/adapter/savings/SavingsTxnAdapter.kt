package com.jar.app.feature_transaction.impl.ui.details.adapter.savings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardSavingsDetailsBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

internal class SavingsTxnAdapter : AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureTransactionCardSavingsDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavingsBreakdownViewHolder(binding)
    }

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.SavingTxnDetails
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.SavingTxnDetails && holder is SavingsBreakdownViewHolder) {
            holder.bind(item)
        }
    }

    inner class SavingsBreakdownViewHolder(
        val binding: FeatureTransactionCardSavingsDetailsBinding
    ) : BaseViewHolder(binding.root) {
        private val adapter = SavingsTxnBreakdownAdapter()
        private val spaceItemDecoration = SpaceItemDecoration(0.dp, 2.dp)

        fun bind(data: com.jar.app.feature_transaction.shared.domain.model.SavingTxnDetails) {
            binding.tvHeader.text = data.header
            binding.rvBreakdown.adapter = adapter
            binding.rvBreakdown.layoutManager = LinearLayoutManager(context)
            binding.rvBreakdown.addItemDecorationIfNoneAdded(spaceItemDecoration)
            adapter.submitList(data.savingBreakdownList)
        }
    }

}