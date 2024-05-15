package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardContactUsBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class ContactUsAdapter(private val onClickListener: (com.jar.app.feature_transaction.shared.domain.model.ContactUsData) -> Unit) :
    AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.ContactUsData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardContactUsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactUsVH(binding, onClickListener)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.ContactUsData && holder is ContactUsVH) {
            holder.bindData(item)
        }
    }

    inner class ContactUsVH(
        private val binding: FeatureTransactionCardContactUsBinding,
        private val onClickListener: (com.jar.app.feature_transaction.shared.domain.model.ContactUsData) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var data: com.jar.app.feature_transaction.shared.domain.model.ContactUsData? = null

        init {
            binding.root.setDebounceClickListener {
                data?.let {
                    onClickListener(it)
                }
            }
        }

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.ContactUsData) {
            this.data = data
            binding.root.isClickable = true
            binding.root.isFocusable = true
            binding.btnContactUs.isClickable = false
            binding.btnContactUs.isFocusable = false
        }
    }
}