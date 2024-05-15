package com.jar.app.feature_payment.impl.ui.payment_option

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.*
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.OrderSummarySection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.RecentlyUsedPaymentMethodSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.UpiCollectPaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.UpiIntentAppsPaymentSection

internal class PaymentSectionAdapter(delegates: List<AdapterDelegate<List<PaymentSection>>>) :
    AsyncListDifferDelegationAdapter<PaymentSection>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PaymentSection>() {

            override fun areItemsTheSame(
                oldItem: PaymentSection,
                newItem: PaymentSection
            ): Boolean {
                return if (oldItem is UpiIntentAppsPaymentSection && newItem is UpiIntentAppsPaymentSection)
                    oldItem.availableUpiApps.size == newItem.availableUpiApps.size
                else if (oldItem is UpiCollectPaymentSection && newItem is UpiCollectPaymentSection)
                    oldItem.appLogoUrl == newItem.appLogoUrl
                else if (oldItem is SavedUpiIdSection && newItem is SavedUpiIdSection)
                    oldItem.savedUpiIds.size == newItem.savedUpiIds.size
                else if (oldItem is RecentlyUsedPaymentMethodSection && newItem is RecentlyUsedPaymentMethodSection)
                    oldItem.recentlyUsedPaymentMethods.size == newItem.recentlyUsedPaymentMethods.size
                else if (oldItem is OrderSummarySection && newItem is OrderSummarySection)
                    oldItem.amount == newItem.amount
                else if (oldItem is AddCardPaymentSection && newItem is AddCardPaymentSection)
                    oldItem.bankLogoUrl == newItem.bankLogoUrl
                else if (oldItem is SavedCardPaymentSection && newItem is SavedCardPaymentSection)
                    oldItem.cards.size == newItem.cards.size
                else if (oldItem is SecurePaymentSection && newItem is SecurePaymentSection)
                    oldItem.id.name == newItem.id.name
                else false
            }

            override fun areContentsTheSame(
                oldItem: PaymentSection,
                newItem: PaymentSection
            ): Boolean {
                return if (oldItem is UpiIntentAppsPaymentSection && newItem is UpiIntentAppsPaymentSection)
                    oldItem == newItem
                else if (oldItem is UpiCollectPaymentSection && newItem is UpiCollectPaymentSection)
                    oldItem == newItem
                else if (oldItem is SavedUpiIdSection && newItem is SavedUpiIdSection)
                    oldItem == newItem
                else if (oldItem is RecentlyUsedPaymentMethodSection && newItem is RecentlyUsedPaymentMethodSection)
                    oldItem == newItem
                else if (oldItem is OrderSummarySection && newItem is OrderSummarySection)
                    oldItem == newItem
                else if (oldItem is AddCardPaymentSection && newItem is AddCardPaymentSection)
                    oldItem == newItem
                else if (oldItem is SavedCardPaymentSection && newItem is SavedCardPaymentSection)
                    oldItem == newItem
                else if (oldItem is SecurePaymentSection && newItem is SecurePaymentSection)
                    oldItem == newItem
                else false
            }
        }
    }

    init {
        delegates.forEach {
            delegatesManager.addDelegate(it)
        }
    }
}