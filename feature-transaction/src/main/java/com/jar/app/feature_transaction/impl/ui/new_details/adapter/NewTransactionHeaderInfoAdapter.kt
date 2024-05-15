package com.jar.app.feature_transaction.impl.ui.new_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardHeaderBinding
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.NewTransactionDetailsCardView
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.TransactionHeaderCard

internal class NewTransactionHeaderInfoAdapter : AdapterDelegate<List<NewTransactionDetailsCardView>>() {
    override fun isForViewType(items: List<NewTransactionDetailsCardView>, position: Int): Boolean {
        return items[position] is TransactionHeaderCard
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureTransactionCardHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NewTransactionHeaderInfoViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<NewTransactionDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is TransactionHeaderCard && holder is NewTransactionHeaderInfoViewHolder) {
            holder.bind(item)
        }
    }

    inner class NewTransactionHeaderInfoViewHolder(
        private val binding: FeatureTransactionCardHeaderBinding
    ): BaseViewHolder(binding.root) {

        fun bind(data: TransactionHeaderCard) {
            binding.tvSubTitle.isVisible = false
            binding.ivSubIcon.isVisible = false

            Glide.with(context).load(data.icon.orEmpty()).into(binding.ivDetailsIcon)
            binding.tvDetailsTitle.setHtmlText(data.title.orEmpty())
            binding.tvDetailsDescription.setHtmlText(data.description.orEmpty())
            binding.tvDetailsValue.setHtmlText(data.value.orEmpty())

            data.subtitleComponentText?.let {
                binding.tvSubTitle.setHtmlText(it)
                binding.tvSubTitle.isVisible = true
            }

            data.subtitleComponentIcon?.let {
                Glide.with(context).load(it).into(binding.ivSubIcon)
                binding.ivSubIcon.isVisible = true
            }
        }
    }
}