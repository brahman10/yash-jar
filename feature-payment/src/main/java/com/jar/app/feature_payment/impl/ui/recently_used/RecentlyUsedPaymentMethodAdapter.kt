package com.jar.app.feature_payment.impl.ui.recently_used

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_payment.databinding.CellRecentlyUsedPaymentMethodBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.*

class RecentlyUsedPaymentMethodAdapter(
    private val onCardClick: (position: Int, paymentMethod: PaymentMethod) -> Unit,
    private val onPayClick: (paymentMethod: PaymentMethod) -> Unit,
) :
    ListAdapter<PaymentMethod, RecentlyUsedPaymentMethodViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PaymentMethod>() {
            override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
                return if (oldItem is PaymentMethodNB && newItem is PaymentMethodNB)
                    oldItem.paymentMethodType == newItem.paymentMethodType
                else if (oldItem is PaymentMethodCard && newItem is PaymentMethodCard)
                    oldItem.cardFingerprint == newItem.cardFingerprint
                else if (oldItem is PaymentMethodUpiIntent && newItem is PaymentMethodUpiIntent)
                    oldItem.payerApp == newItem.payerApp
                else if (oldItem is PaymentMethodUpiCollect && newItem is PaymentMethodUpiCollect)
                    oldItem.payerVpa == newItem.payerVpa
                else false
            }

            override fun areContentsTheSame(
                oldItem: PaymentMethod,
                newItem: PaymentMethod
            ): Boolean {
                return if (oldItem is PaymentMethodNB && newItem is PaymentMethodNB)
                    oldItem == newItem
                else if (oldItem is PaymentMethodCard && newItem is PaymentMethodCard)
                    oldItem == newItem
                else if (oldItem is PaymentMethodUpiIntent && newItem is PaymentMethodUpiIntent)
                    oldItem == newItem
                else if (oldItem is PaymentMethodUpiCollect && newItem is PaymentMethodUpiCollect)
                    oldItem == newItem
                else false
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentlyUsedPaymentMethodViewHolder {
        val binding = CellRecentlyUsedPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentlyUsedPaymentMethodViewHolder(binding, onCardClick, onPayClick)
    }

    override fun onBindViewHolder(holder: RecentlyUsedPaymentMethodViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setPaymentMethod(it)
        }
    }
}