package com.jar.app.feature.transaction.ui.transaction_breakup

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.R
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellTransactionBreakupBinding
import com.jar.app.feature_transaction.impl.domain.model.getIconForCategory

class PaymentTransactionBreakupViewHolder(private val binding: CellTransactionBreakupBinding) :
    BaseViewHolder(binding.root) {

    fun setTransactionBreakup(transaction: com.jar.app.feature_transaction.shared.domain.model.Transaction) {
        Glide.with(itemView).load(transaction.getIconForCategory()).into(binding.ivCoin)
        binding.tvTransactionTitle.text = transaction.title
        binding.tvTransactionAmount.text =
            context.getString(R.string.rupee_x_in_string, transaction.getAmountToShow())
        binding.tvHeaderSpareChange.isVisible = transaction.shouldShowRoundOff()
        binding.tvSpareChange.isVisible = transaction.shouldShowRoundOff()
        binding.tvSpareChange.text = context.getString(
            R.string.rupee_x_in_string,
            transaction.roundOffAmount.orZero().toString()
        )
        binding.tvStatus.text = transaction.getCustomStatus()
        binding.tvStatus.setTextColor(transaction.getColorForStatus().getColor(context))
        binding.tvTimeStamp.text = transaction.timestamp
    }
}