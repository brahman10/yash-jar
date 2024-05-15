package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardTxnCoupnDetailBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class CouponCodeAdapter : AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.CouponCodeData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardTxnCoupnDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponCodeVH(binding)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.CouponCodeData && holder is CouponCodeVH) {
            holder.bindData(item)
        }
    }

    inner class CouponCodeVH(
        private val binding: FeatureTransactionCardTxnCoupnDetailBinding,
    ) : BaseViewHolder(binding.root) {

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.CouponCodeData) {
            binding.tvCouponApplied.text = data.couponCode
            binding.tvCouponCodeDescription.text = data.couponCodeDescription
        }
    }
}