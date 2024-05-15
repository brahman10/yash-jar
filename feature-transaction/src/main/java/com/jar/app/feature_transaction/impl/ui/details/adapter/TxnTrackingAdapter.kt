package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardTxnTrackingBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class TxnTrackingAdapter(private val onClicked: (url: String) -> Unit) :
    AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.TxnTrackingData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardTxnTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TxnTrackingVH(binding)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.TxnTrackingData && holder is TxnTrackingVH) {
            holder.bindData(item)
        }
    }

    inner class TxnTrackingVH(
        private val binding: FeatureTransactionCardTxnTrackingBinding
    ) : BaseViewHolder(binding.root) {

        private var data: com.jar.app.feature_transaction.shared.domain.model.TxnTrackingData? = null

        init {
            binding.tvTrackNow.setDebounceClickListener {
                data?.trackingLink?.let {
                    onClicked(it)
                }
            }
        }

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.TxnTrackingData) {
            this.data = data
            if (data.trackingLink.isNullOrBlank()) {
                binding.tvTrackingLink.isVisible = true
                binding.tvTrackingIdOrMessage.text =
                    context.getString(R.string.feature_transaction_tracking_id_will_be_generated_after_gold_shipped)
                binding.tvTrackNow.isVisible = false
            } else {
                binding.tvTrackingLink.isVisible = false
                binding.tvTrackingIdOrMessage.text =
                    context.getString(R.string.feature_transaction_tracking_link_available)
                binding.tvTrackNow.isVisible = true
            }
        }
    }
}