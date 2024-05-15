package com.jar.app.feature_lending.impl.ui.repayments.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingCellPaymentBreakdownBinding
import com.jar.app.feature_lending.shared.domain.model.repayment.PaymentBreakDownDetails
import com.jar.app.feature_lending.impl.ui.common.KeyValueAdapter
import com.jar.app.feature_lending.shared.MR

internal class RepaymentTransactionCardAdapter : ListAdapter<PaymentBreakDownDetails, RepaymentTransactionCardAdapter.TransactionBreakDownViewHolder>(DIFF_UTIL) {

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<PaymentBreakDownDetails>() {
            override fun areItemsTheSame(oldItem: PaymentBreakDownDetails, newItem: PaymentBreakDownDetails): Boolean {
                return oldItem.cardHeader == newItem.cardHeader
            }

            override fun areContentsTheSame(oldItem: PaymentBreakDownDetails, newItem: PaymentBreakDownDetails): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TransactionBreakDownViewHolder(
        FeatureLendingCellPaymentBreakdownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TransactionBreakDownViewHolder, position: Int) {
        getItem(position)?.let { holder.setData(it) }
    }

    inner class TransactionBreakDownViewHolder(
        private val binding: FeatureLendingCellPaymentBreakdownBinding
    ) : BaseViewHolder(binding.root) {

        private var adapter: KeyValueAdapter? = null

        init {
            adapter = KeyValueAdapter(areValuesAmount = true, skipPadding = true)
            adapter?.setFontSize(12f)
            adapter?.setColor(com.jar.app.core_ui.R.color.white, com.jar.app.core_ui.R.color.white)
            binding.rvKeyValue.adapter = adapter
        }

        fun setData(data: PaymentBreakDownDetails) {
            binding.tvTitle.text = data.cardHeader
            val totalAmount = data.cardBreakdown?.lastOrNull()?.value
            binding.tvTotalAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, totalAmount?.toFloatOrNull()?.toInt()?.getFormattedAmount().orEmpty())
            adapter?.submitList(data.cardBreakdown?.subList(0,
                data.cardBreakdown?.size?.minus(1) ?: 0
            ))
        }
    }
}