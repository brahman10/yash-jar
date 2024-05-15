package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardPauseTxnDetailsBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

internal class PauseTxnDetailAdapter : AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureTransactionCardPauseTxnDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PauseTxnDetailViewHolder(binding)
    }

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.PauseTxnDetails
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.PauseTxnDetails && holder is PauseTxnDetailViewHolder) {
            holder.bind(item)
        }
    }

    inner class PauseTxnDetailViewHolder(
        val binding: FeatureTransactionCardPauseTxnDetailsBinding
    ) : BaseViewHolder(binding.root) {

        fun bind(data: com.jar.app.feature_transaction.shared.domain.model.PauseTxnDetails) {
            binding.tvTitle.text = data.title.orEmpty()
            binding.tvDescription.text = data.description.orEmpty()
        }
    }

}