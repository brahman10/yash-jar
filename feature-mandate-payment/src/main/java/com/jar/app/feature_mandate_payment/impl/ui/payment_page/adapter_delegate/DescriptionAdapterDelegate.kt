package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPaymentPageDescriptionBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.DescriptionPaymentPageItem

internal class DescriptionAdapterDelegate : AdapterDelegate<List<BasePaymentPageItem>>() {

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is DescriptionPaymentPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePaymentCellPaymentPageDescriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as DescriptionViewHolder).setDescription(items[position] as DescriptionPaymentPageItem)
    }

    inner class DescriptionViewHolder(private val binding: FeatureMandatePaymentCellPaymentPageDescriptionBinding) :
        BaseViewHolder(binding.root) {

        fun setDescription(descriptionPaymentPageItem: DescriptionPaymentPageItem) {
            binding.tvDescription.text = getCustomString(descriptionPaymentPageItem.description)
            binding.tvDescription.setCompoundDrawablesRelativeWithIntrinsicBounds(
                descriptionPaymentPageItem.icon.orZero(),
                0,
                0,
                0
            )
            binding.tvDescription.compoundDrawablePadding =
                if (descriptionPaymentPageItem.icon != null) 12.dp else 0
        }
    }
}