package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.databinding.CellAddCardPaymentSectionBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.AddCardPaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection

internal class AddCardSectionAdapterDelegate(
    private val onClick: () -> Unit
) : AdapterDelegate<List<PaymentSection>>() {

    override fun isForViewType(items: List<PaymentSection>, position: Int): Boolean {
        return items[position] is AddCardPaymentSection
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellAddCardPaymentSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddCardSectionViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(
        items: List<PaymentSection>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is AddCardSectionViewHolder && item is AddCardPaymentSection)
            holder.bind(item.bankLogoUrl)
    }

    inner class AddCardSectionViewHolder(
        private val binding: CellAddCardPaymentSectionBinding,
        private val onClick: () -> Unit
    ) : BaseViewHolder(binding.root) {

        init {
            binding.root.setDebounceClickListener {
                onClick.invoke()
            }
        }

        fun bind(bankLogoUrl: String) {
            Glide.with(itemView).load(bankLogoUrl).into(binding.ivBankLogo)
        }

    }
}