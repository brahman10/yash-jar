package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardRoundoffBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class RoundOffAdapter(private val onClicked: (com.jar.app.feature_transaction.shared.domain.model.RoundOffData) -> Unit) :
    AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.RoundOffData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardRoundoffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoundOffVH(binding)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.RoundOffData && holder is RoundOffVH) {
            holder.bindData(item)
        }
    }

    inner class RoundOffVH(
        private val binding: FeatureTransactionCardRoundoffBinding,
    ) : BaseViewHolder(binding.root) {

        private var data: com.jar.app.feature_transaction.shared.domain.model.RoundOffData? = null

        init {
            binding.root.setDebounceClickListener {
                data?.let {
                    onClicked(it)
                }
            }
        }

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.RoundOffData) {
            this.data = data
            //binding.tvRoundOff.text = context.getString(R.string.feature_transaction_x_roundoff, data.roundoffCount)
        }
    }
}