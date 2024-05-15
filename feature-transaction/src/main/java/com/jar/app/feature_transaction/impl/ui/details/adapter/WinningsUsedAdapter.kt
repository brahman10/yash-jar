package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardTxnWinningDetailBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class WinningsUsedAdapter : AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.WinningsUsedData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardTxnWinningDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return WinningMessageVH(binding)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.WinningsUsedData && holder is WinningMessageVH) {
            holder.bindData(item)
        }
    }

    inner class WinningMessageVH(
        private val binding: FeatureTransactionCardTxnWinningDetailBinding,
    ) : BaseViewHolder(binding.root) {

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.WinningsUsedData) {
            binding.tvWinningsMessage.text = data.jarWinningsUsedText
        }
    }
}