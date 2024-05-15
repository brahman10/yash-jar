package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.databinding.CellUpiIntentAppPaymentMethodSectionBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.UpiIntentAppsPaymentSection
import com.jar.app.feature_payment.impl.ui.payment_option.UpiAppAdapter

internal class UpiIntentAppsPaymentMethodAdapterDelegate(
    private val onClick: (app: UpiApp) -> Unit
) :
    AdapterDelegate<List<PaymentSection>>() {
    override fun isForViewType(items: List<PaymentSection>, position: Int): Boolean {
        return items[position] is UpiIntentAppsPaymentSection
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellUpiIntentAppPaymentMethodSectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UpiIntentAppsPaymentMethodSectionViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(
        items: List<PaymentSection>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is UpiIntentAppsPaymentSection && holder is UpiIntentAppsPaymentMethodSectionViewHolder)
            holder.setPaymentSection(item)
    }

    inner class UpiIntentAppsPaymentMethodSectionViewHolder(
        private val binding: CellUpiIntentAppPaymentMethodSectionBinding,
        private val onClick: (app: UpiApp) -> Unit
    ) :
        BaseViewHolder(binding.root) {

        private val spaceItemDecoration =
            SpaceItemDecoration(20.dp, 0.dp, RecyclerView.HORIZONTAL, true)

        private var adapterUpi: UpiAppAdapter? = null

        fun setPaymentSection(upiIntentAppsPaymentSection: UpiIntentAppsPaymentSection) {
            val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.rvUpiApps.layoutManager = layoutManager
            binding.rvUpiApps.addItemDecorationIfNoneAdded(spaceItemDecoration)
            adapterUpi = UpiAppAdapter {
                onClick.invoke(it)
            }
            binding.rvUpiApps.adapter = adapterUpi
            adapterUpi?.submitList(upiIntentAppsPaymentSection.availableUpiApps)
        }
    }
}