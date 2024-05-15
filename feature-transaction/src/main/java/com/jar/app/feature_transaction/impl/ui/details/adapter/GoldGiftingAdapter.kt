package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardGoldGiftingBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class GoldGiftingAdapter : AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.GoldGiftingData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardGoldGiftingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoldGiftingVH(binding)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.GoldGiftingData && holder is GoldGiftingVH) {
            holder.bindData(item)
        }
    }

    inner class GoldGiftingVH(
        private val binding: FeatureTransactionCardGoldGiftingBinding
    ) : BaseViewHolder(binding.root) {

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.GoldGiftingData) {
            if (data.isReceived.orFalse()) {
                binding.tvSentTo.text = context.getString(R.string.feature_transaction_received_from)
                binding.tvSentToName.text = "${data.senderName} (${data.senderPhoneNo})"
            } else {
                binding.tvSentTo.text = context.getString(R.string.feature_transaction_sent_to)
                binding.tvSentToName.text = "${data.receiverName} (${data.receiverPhoneNo})"
            }
        }
    }
}