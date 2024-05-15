package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.forEachIndexedVisibleHolder
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.databinding.CellRecentlyUsePaymentMethodsSectionBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethod
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethodUpiIntent
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.RecentlyUsedPaymentMethodSection
import com.jar.app.feature_payment.impl.ui.recently_used.RecentlyUsedPaymentMethodAdapter
import com.jar.app.feature_payment.impl.ui.recently_used.RecentlyUsedPaymentMethodViewHolder

internal class RecentlyUsedPaymentMethodAdapterDelegate(
    private val onCardClick: (paymentMethod: PaymentMethod?) -> Unit,
    private val onPayClick: (paymentMethod: PaymentMethod) -> Unit,
) : AdapterDelegate<List<PaymentSection>>() {

    override fun isForViewType(items: List<PaymentSection>, position: Int): Boolean {
        return items[position] is RecentlyUsedPaymentMethodSection
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellRecentlyUsePaymentMethodsSectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecentlyUsedPaymentMethodSectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PaymentSection>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is RecentlyUsedPaymentMethodSection && holder is RecentlyUsedPaymentMethodSectionViewHolder)
            holder.setSection(item)
    }

    inner class RecentlyUsedPaymentMethodSectionViewHolder(
        private val binding: CellRecentlyUsePaymentMethodsSectionBinding
    ) : BaseViewHolder(binding.root) {

        private var adapter: RecentlyUsedPaymentMethodAdapter? = null

        fun setSection(recentlyUsedPaymentMethodSection: RecentlyUsedPaymentMethodSection) {
            binding.rvRecentlyUsed.layoutManager = LinearLayoutManager(context)
            adapter = RecentlyUsedPaymentMethodAdapter(
                onCardClick = { pos, paymentMethod ->
                    binding.rvRecentlyUsed.forEachIndexedVisibleHolder<RecentlyUsedPaymentMethodViewHolder> { holder, holderPos ->
                        if (holderPos == pos) {
                            if (paymentMethod is PaymentMethodUpiIntent) {
                                onPayClick.invoke(paymentMethod)
                            } else {
                                holder.toggle()
                            }
                        } else {
                            holder.collapse()
                        }
                    }
                    onCardClick(paymentMethod)
                },
                onPayClick = {
                    onPayClick.invoke(it)
                }
            )
            binding.rvRecentlyUsed.adapter = adapter
            adapter?.submitList(recentlyUsedPaymentMethodSection.recentlyUsedPaymentMethods)
        }
    }
}