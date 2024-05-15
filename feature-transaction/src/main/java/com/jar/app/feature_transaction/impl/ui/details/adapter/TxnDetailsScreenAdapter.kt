package com.jar.app.feature_transaction.impl.ui.details.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class TxnDetailsScreenAdapter(delegates: List<AdapterDelegate<List<TxnDetailsCardView>>>) :
    AsyncListDifferDelegationAdapter<TxnDetailsCardView>(ITEM_CALLBACK) {

    init {
        delegates.forEach {
            delegatesManager.addDelegate(it)
        }
    }

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<TxnDetailsCardView>() {
            override fun areItemsTheSame(
                oldItem: TxnDetailsCardView,
                newItem: TxnDetailsCardView
            ): Boolean {
                return oldItem.getSortKey() == newItem.getSortKey()
            }

            override fun areContentsTheSame(
                oldItem: TxnDetailsCardView,
                newItem: TxnDetailsCardView
            ): Boolean {
                return if (oldItem is com.jar.app.feature_transaction.shared.domain.model.ContactUsData && newItem is com.jar.app.feature_transaction.shared.domain.model.ContactUsData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.CouponCodeData && newItem is com.jar.app.feature_transaction.shared.domain.model.CouponCodeData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.GoldGiftingData && newItem is com.jar.app.feature_transaction.shared.domain.model.GoldGiftingData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.ProductData && newItem is com.jar.app.feature_transaction.shared.domain.model.ProductData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.RoundOffData && newItem is com.jar.app.feature_transaction.shared.domain.model.RoundOffData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.SafegoldBannerData && newItem is com.jar.app.feature_transaction.shared.domain.model.SafegoldBannerData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.TxnDetailsData && newItem is com.jar.app.feature_transaction.shared.domain.model.TxnDetailsData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.TxnRoutineDetails && newItem is com.jar.app.feature_transaction.shared.domain.model.TxnRoutineDetails)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.CommonTxnWinningData && newItem is com.jar.app.feature_transaction.shared.domain.model.CommonTxnWinningData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.TxnTrackingData && newItem is com.jar.app.feature_transaction.shared.domain.model.TxnTrackingData)
                    oldItem == newItem
                else if (oldItem is com.jar.app.feature_transaction.shared.domain.model.LeasingTnxDetails && newItem is com.jar.app.feature_transaction.shared.domain.model.LeasingTnxDetails)
                    oldItem == newItem
                else
                    false
            }
        }
    }
}