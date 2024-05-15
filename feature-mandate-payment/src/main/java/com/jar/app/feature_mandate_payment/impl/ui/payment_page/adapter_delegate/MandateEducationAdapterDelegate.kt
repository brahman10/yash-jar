package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPaymentPageMandateEducationBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.MandateEducationPageItem
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder.MandateEducationViewHolder

internal class MandateEducationAdapterDelegate(
    private val onVideoClicked: (videoUrl: String) -> Unit
) : AdapterDelegate<List<BasePaymentPageItem>>() {

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is MandateEducationPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePaymentCellPaymentPageMandateEducationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MandateEducationViewHolder(binding, onVideoClicked)
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as MandateEducationViewHolder).setMandateEducationDetails(items[position] as MandateEducationPageItem)
    }

}