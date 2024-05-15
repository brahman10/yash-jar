package com.jar.app.feature_lending.impl.ui.repayments.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingCellEmiScheduleBinding
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.model.repayment.EmiTxnCommonData
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentStatus

internal class EmiScheduledAdapter : ListAdapter<EmiTxnCommonData, EmiScheduledAdapter.EmiScheduleViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<EmiTxnCommonData>() {
            override fun areItemsTheSame(oldItem: EmiTxnCommonData, newItem: EmiTxnCommonData): Boolean {
                return oldItem.emiDate == newItem.emiDate
            }

            override fun areContentsTheSame(oldItem: EmiTxnCommonData, newItem: EmiTxnCommonData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EmiScheduleViewHolder(
        FeatureLendingCellEmiScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: EmiScheduleViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setData(it)
        }
    }

    inner class EmiScheduleViewHolder(
        private val binding: FeatureLendingCellEmiScheduleBinding
    ) : BaseViewHolder(binding.root) {

        fun setData(data: EmiTxnCommonData) {
            val statusBgRes: Int
            val statusTextColor: Int

            when (data.getPaymentStatus()) {
                RepaymentStatus.PAID_ON_TIME, RepaymentStatus.LATE_PAYMENT -> {
                    statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1a1ea787_bg_4dp)
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_58DDC8))
                }
                RepaymentStatus.PAYMENT_FAILED, RepaymentStatus.PAYMENT_OVERDUE -> {
                    statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1aeb6a6e_bg_4dp)
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EB6A6E))
                }
                RepaymentStatus.PAYMENT_PENDING, RepaymentStatus.UPCOMING -> {
                    statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1aebb46a_bg_4dp)
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EBB46A))
                }
            }

            if (data.paymentStatusText.isNullOrBlank()) {
                binding.tvStatus.isVisible = false
            } else {
                statusBgRes.let { binding.tvStatus.setBackgroundResource(it) }
                statusTextColor.let { binding.tvStatus.setTextColor(it) }
                binding.tvStatus.isVisible = true
                binding.tvStatus.text = data.paymentStatusText
            }
            binding.tvTitle.text = data.emiTitle
            binding.tvAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, data.emiAmount?.toFloatOrNull()?.toInt()?.getFormattedAmount().orEmpty())
            binding.tvDate.text = getCustomStringFormatted(MR.strings.feature_lending_due_date_x, data.emiDate.orEmpty())
        }
    }
}