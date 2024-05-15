package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.forEachIndexedVisibleHolder
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.databinding.CellSavedCardsSectionBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.SavedCard
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.SavedCardPaymentSection

internal class SavedCardsSectionAdapterDelegate(
    private val onClick: (savedCard: SavedCard) -> Unit
) : AdapterDelegate<List<PaymentSection>>() {

    override fun isForViewType(items: List<PaymentSection>, position: Int): Boolean {
        return items[position] is SavedCardPaymentSection
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellSavedCardsSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedCardSectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PaymentSection>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is SavedCardPaymentSection && holder is SavedCardSectionViewHolder)
            holder.bind(item.cards)
    }

    inner class SavedCardSectionViewHolder(private val binding: CellSavedCardsSectionBinding) :
        BaseViewHolder(binding.root) {

        private var adapter: SavedCardAdapter? = null

        fun bind(cards: List<SavedCard>) {
            adapter = SavedCardAdapter(
                onClick = { pos ->
                    binding.rvSavedCards.forEachIndexedVisibleHolder<SavedCardViewHolder> { holder, holderPos ->
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
            binding.rvSavedCards.layoutManager = LinearLayoutManager(context)
            binding.rvSavedCards.adapter = adapter
            adapter?.submitList(cards)
        }

    }
}