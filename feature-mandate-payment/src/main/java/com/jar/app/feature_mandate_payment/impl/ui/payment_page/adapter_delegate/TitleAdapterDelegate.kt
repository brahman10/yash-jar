package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPaymentPageTitleBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.TitlePaymentPageItem

internal class TitleAdapterDelegate : AdapterDelegate<List<BasePaymentPageItem>>() {

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is TitlePaymentPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePaymentCellPaymentPageTitleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TitleViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as TitleViewHolder).setTitle(items[position] as TitlePaymentPageItem)
    }


    inner class TitleViewHolder(private val binding: FeatureMandatePaymentCellPaymentPageTitleBinding) :
        BaseViewHolder(binding.root) {

        fun setTitle(titlePaymentPageItem: TitlePaymentPageItem) {
            binding.tvTitle.text =
                if (titlePaymentPageItem.title != null)
                    getCustomString(titlePaymentPageItem.title!!)
                else
                    titlePaymentPageItem.titleString

            titlePaymentPageItem.textSize?.let {
                binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, it.toFloat())
            }
            titlePaymentPageItem.bgColor?.let {
                binding.root.setBackgroundColor(it.getColor(context))
            }
        }
    }
}