package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardTxnDetailsBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class TxnDetailsCardAdapter(
    private val onCopyClicked: (String) -> Unit,
    private val onExpandClicked: (String) -> Unit,
) : AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.TxnDetailsData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardTxnDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TxnDetailsCardVH(binding, onCopyClicked)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.TxnDetailsData && holder is TxnDetailsCardVH) {
            holder.bindData(item)
        }
    }

    inner class TxnDetailsCardVH(
        private val binding: FeatureTransactionCardTxnDetailsBinding,
        private val onCopyClicked: (String) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var data: com.jar.app.feature_transaction.shared.domain.model.TxnDetailsData? = null

        init {
            binding.tvTxnId.setDebounceClickListener {
                data?.value?.let {
                    onCopyClicked(it)
                }
            }

            binding.ivExpand.setOnClickListener {
                binding.ivExpand.animate()
                    .rotation(if (binding.expandableLayout.isExpanded) 0f else 180f).start()
                binding.expandableLayout.toggle()
                onExpandClicked(if (binding.expandableLayout.isExpanded) "Open" else "Close")
            }
        }

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.TxnDetailsData) {
            this.data = data
            binding.ivExpand.rotation = if (binding.expandableLayout.isExpanded) 180f else 0f
            binding.tvTxnId.text = data.value
            binding.tvTxnTitle.text = data.title
            val shouldShowExpandableList = data.list.isNullOrEmpty().not()
            if (shouldShowExpandableList) {
                val adapter = TransactionDetailsAdapter()
                binding.rvTransactionDetails.adapter = adapter
                adapter.submitList(data.list)
            }
            binding.rvTransactionDetails.isVisible = shouldShowExpandableList
            binding.ivExpand.isVisible = shouldShowExpandableList
        }
    }
}
