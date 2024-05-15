package com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jar.app.feature_settings.domain.model.PaymentMethod
import com.jar.app.feature_settings.domain.model.SavedCardPaymentMethod
import com.jar.app.feature_settings.domain.model.SavedUpiIdsPaymentMethod

internal class PaymentMethodAdapter(delegates: List<AdapterDelegate<List<PaymentMethod>>>) :
    AsyncListDifferDelegationAdapter<PaymentMethod>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PaymentMethod>() {
            override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
                return oldItem.position == newItem.position
            }

            override fun areContentsTheSame(
                oldItem: PaymentMethod,
                newItem: PaymentMethod
            ): Boolean {
                return if (oldItem is SavedUpiIdsPaymentMethod && newItem is SavedUpiIdsPaymentMethod)
                    oldItem == newItem
                else if (oldItem is SavedCardPaymentMethod && newItem is SavedCardPaymentMethod)
                    oldItem == newItem
                else
                    false
            }
        }
    }

    init {
        delegates.forEach {
            delegatesManager.addDelegate(it)
        }
    }
}