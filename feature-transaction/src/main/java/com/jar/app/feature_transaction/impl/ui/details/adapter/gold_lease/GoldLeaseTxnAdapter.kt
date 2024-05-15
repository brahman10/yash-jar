package com.jar.app.feature_transaction.impl.ui.details.adapter.gold_lease

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardGoldLeaseBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

internal class GoldLeaseTxnAdapter: AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureTransactionCardGoldLeaseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoldLeaseTxnViewHolder(binding)
    }

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.LeasingTnxDetails
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.LeasingTnxDetails && holder is GoldLeaseTxnViewHolder) {
            holder.bind(item)
        }
    }

    inner class GoldLeaseTxnViewHolder(
        val binding: FeatureTransactionCardGoldLeaseBinding
    ): BaseViewHolder(binding.root) {
        private val adapter = GoldLeaseTxnBreakdownAdapter()
        private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)

        fun bind(data: com.jar.app.feature_transaction.shared.domain.model.LeasingTnxDetails) {
            binding.tvHeader.text = data.header
            binding.tvFooter.text = data.footer
            binding.rvGoldLeaseBreakdown.adapter = adapter
            binding.rvGoldLeaseBreakdown.layoutManager = LinearLayoutManager(context)
            binding.rvGoldLeaseBreakdown.addItemDecorationIfNoneAdded(spaceItemDecoration)
            adapter.submitList(data.leaseBreakdownList)
        }
    }

}