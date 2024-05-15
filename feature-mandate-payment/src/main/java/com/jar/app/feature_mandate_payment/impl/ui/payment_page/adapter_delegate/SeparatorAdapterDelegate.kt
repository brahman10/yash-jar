package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPaymentPageSeparatorBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.SeparatorPaymentPageItem

internal class SeparatorAdapterDelegate : AdapterDelegate<List<BasePaymentPageItem>>() {

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is SeparatorPaymentPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePaymentCellPaymentPageSeparatorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SeparatorViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as SeparatorViewHolder).setSeparator(items[position] as SeparatorPaymentPageItem)
    }

    inner class SeparatorViewHolder(private val binding: FeatureMandatePaymentCellPaymentPageSeparatorBinding) :
        BaseViewHolder(binding.root) {

        fun setSeparator(separatorPaymentPageItem: SeparatorPaymentPageItem) {
            binding.root.setBackgroundColor(
                separatorPaymentPageItem.bgColor.getColor(context)
            )
        }
    }
}