package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.forEachIndexedVisibleHolder
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.databinding.CellSavedUpiIdsPaymentMethodSectionBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.SavedUpiIdSection

internal class SavedUpiIdsAdapterDelegate(
    private val onClick: (upiId: String) -> Unit
) : AdapterDelegate<List<PaymentSection>>() {

    override fun isForViewType(items: List<PaymentSection>, position: Int): Boolean {
        return items[position] is SavedUpiIdSection
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellSavedUpiIdsPaymentMethodSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedUpiIdsPaymentSectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PaymentSection>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is SavedUpiIdSection && holder is SavedUpiIdsPaymentSectionViewHolder)
            holder.bind(item.savedUpiIds)
    }

    inner class SavedUpiIdsPaymentSectionViewHolder(private val binding: CellSavedUpiIdsPaymentMethodSectionBinding) :
        BaseViewHolder(binding.root) {

        private var adapter: SavedUpiIdAdapter? = null

        fun bind(savedUpiIds: List<String>) {
            adapter = SavedUpiIdAdapter(
                onClick = { pos ->
                    binding.rvSavedUpi.forEachIndexedVisibleHolder<SavedUpiIdViewHolder> { holder, holderPos ->
                        if (holderPos == pos)
                            holder.toggle()
                        else
                            holder.collapse()
                    }
                },
                onPayClick = {
                    onClick.invoke(it)
                }
            )
            binding.rvSavedUpi.layoutManager = LinearLayoutManager(context)
            binding.rvSavedUpi.adapter = adapter
            adapter?.submitList(savedUpiIds)
        }

    }
}