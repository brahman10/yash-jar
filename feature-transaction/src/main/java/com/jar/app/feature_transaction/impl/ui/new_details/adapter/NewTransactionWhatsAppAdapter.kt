package com.jar.app.feature_transaction.impl.ui.new_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardWhatsappBinding
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.NewTransactionContactUsCard
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.NewTransactionDetailsCardView

internal class NewTransactionWhatsAppAdapter(private val onClickListener: (NewTransactionContactUsCard) -> Unit) :
    AdapterDelegate<List<NewTransactionDetailsCardView>>() {

    override fun isForViewType(items: List<NewTransactionDetailsCardView>, position: Int): Boolean {
        return items[position] is NewTransactionContactUsCard
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardWhatsappBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewTransactionWhatsAppViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(
        items: List<NewTransactionDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is NewTransactionContactUsCard && holder is NewTransactionWhatsAppViewHolder) {
            holder.bindData(item)
        }
    }

    inner class NewTransactionWhatsAppViewHolder (
        binding: FeatureTransactionCardWhatsappBinding,
        private val onClickListener: (NewTransactionContactUsCard) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var data: NewTransactionContactUsCard? = null

        init {
            binding.root.setDebounceClickListener {
                data?.let {
                    onClickListener(it)
                }
            }
        }

        fun bindData(data: NewTransactionContactUsCard) {
            this.data = data
        }
    }
}