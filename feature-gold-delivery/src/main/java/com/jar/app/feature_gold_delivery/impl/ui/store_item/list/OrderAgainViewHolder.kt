package com.jar.app.feature_gold_delivery.impl.ui.store_item.list

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.OrderAgainCardBinding
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transactions_common.shared.CommonTransactionStatus

class OrderAgainViewHolder(
    private val binding: OrderAgainCardBinding,
    private val onOrderAgainFlow: (transactionData: TransactionData) -> Unit,
    private val onTrackHistory: ((transactionData: TransactionData) -> Unit)?
) :
    BaseViewHolder(binding.root) {

    init {
    }

    private var transaction: TransactionData? = null

    fun setStoreItem(transactionData: TransactionData) {
        this.transaction = transactionData

        binding.tvCartName.text = transactionData.title
        binding.tvCartQuantity2.text = "1 Item | ${transactionData.volume} gm"
        Glide.with(binding.cartImageView).load(transactionData.iconLink)
            .placeholder(R.drawable.placeholder_item).into(binding.cartImageView)

        val transactionStatus = transactionData.statusEnum?.uppercase()
        val status: CommonTransactionStatus =
            CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: CommonTransactionStatus.DEFAULT
        if (status == CommonTransactionStatus.SUCCESS) {
            binding.firstContainerSecondBtn.isVisible = true
            binding.deliveryTv.isVisible = false
            binding.trackOrder.isVisible = false
            binding.firstContainerSecondBtn.setDebounceClickListener {
                onOrderAgainFlow(transactionData)
            }
        } else {
            binding.firstContainerSecondBtn.isVisible = false
            binding.deliveryTv.isVisible = true
            binding.trackOrder.isVisible = true

            binding.deliveryTv.setText("Ordered on " + transactionData.date)
            binding.trackOrder.setDebounceClickListener {
                onTrackHistory?.invoke(transactionData)
            }
        }
    }
}