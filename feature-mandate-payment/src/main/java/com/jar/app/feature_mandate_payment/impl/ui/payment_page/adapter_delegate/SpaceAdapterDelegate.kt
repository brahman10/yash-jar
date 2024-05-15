package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.dp
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPaymentPageSpaceBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.SpacePaymentPageItem

internal class SpaceAdapterDelegate : AdapterDelegate<List<BasePaymentPageItem>>() {

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is SpacePaymentPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePaymentCellPaymentPageSpaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpaceViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as SpaceViewHolder).setSpace(items[position] as SpacePaymentPageItem)
    }

    inner class SpaceViewHolder(private val binding: FeatureMandatePaymentCellPaymentPageSpaceBinding) :
        BaseViewHolder(binding.root) {

        fun setSpace(spacePaymentPageItem: SpacePaymentPageItem) {
            binding.root.updateLayoutParams {
                height = spacePaymentPageItem.space.dp
            }
            binding.root.setBackgroundColor(
                spacePaymentPageItem.bgColor.getColor(context)
            )
        }
    }
}