package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.databinding.CellSecurePaymentBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.SecurePaymentSection

internal class SecurePaymentSectionAdapterDelegate : AdapterDelegate<List<PaymentSection>>() {

    override fun isForViewType(items: List<PaymentSection>, position: Int): Boolean {
        return (items[position] is SecurePaymentSection)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellSecurePaymentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SecurePaymentSectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PaymentSection>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {

    }

    inner class SecurePaymentSectionViewHolder(private val binding: CellSecurePaymentBinding) :
        BaseViewHolder(binding.root) {

    }
}