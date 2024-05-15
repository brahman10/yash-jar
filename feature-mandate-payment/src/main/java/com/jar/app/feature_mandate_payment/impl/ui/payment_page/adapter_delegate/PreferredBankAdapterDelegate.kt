package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPreferredBankBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PreferredBankPageItem
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder.PreferredBankViewHolder

internal class PreferredBankAdapterDelegate(
    private val onPreferredBankClicked: (preferredBankPageItem: PreferredBankPageItem) -> Unit,
    private val onCardShown: () -> Unit,
) : AdapterDelegate<List<BasePaymentPageItem>>() {

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is PreferredBankPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePaymentCellPreferredBankBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PreferredBankViewHolder(binding, onPreferredBankClicked)
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as? PreferredBankViewHolder)?.bind(items[position] as PreferredBankPageItem)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        onCardShown.invoke()
    }
}