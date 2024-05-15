package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.CellOrderSummarySectionBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.OrderSummarySection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection

internal class OrderSummarySectionAdapterDelegate : AdapterDelegate<List<PaymentSection>>() {
    override fun isForViewType(items: List<PaymentSection>, position: Int): Boolean {
        return items[position] is OrderSummarySection
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellOrderSummarySectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderSummarySectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PaymentSection>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is OrderSummarySection && holder is OrderSummarySectionViewHolder)
            holder.setSection(item)
    }

    inner class OrderSummarySectionViewHolder(
        private val binding: CellOrderSummarySectionBinding
    ) : BaseViewHolder(binding.root) {

        fun setSection(orderSummarySection: OrderSummarySection) {
            binding.tvAmount.text =
                context.getString(R.string.feature_payment_to_pay_n, orderSummarySection.amount)
        }
    }
}