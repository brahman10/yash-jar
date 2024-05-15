package com.jar.app.feature_lending.impl.ui.repayments.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingCellTxnHistoryBinding
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.model.repayment.EmiTxnCommonData
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentTxnStatus

internal class RepaymentTxnAdapter(
    private val onClick: (data: EmiTxnCommonData) -> Unit
) : ListAdapter<EmiTxnCommonData, RepaymentTxnAdapter.RepaymentTxnViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<EmiTxnCommonData>() {
            override fun areItemsTheSame(oldItem: EmiTxnCommonData, newItem: EmiTxnCommonData): Boolean {
                return oldItem.emiTitle == newItem.emiTitle
            }

            override fun areContentsTheSame(oldItem: EmiTxnCommonData, newItem: EmiTxnCommonData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RepaymentTxnViewHolder(
        FeatureLendingCellTxnHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick
    )

    override fun onBindViewHolder(holder: RepaymentTxnViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setData(it)
        }
    }

    inner class RepaymentTxnViewHolder(
        private val binding: FeatureLendingCellTxnHistoryBinding,
        private val onClick: (data: EmiTxnCommonData) -> Unit
    ) : BaseViewHolder(binding.root) {
        private var repaymentTxnData: EmiTxnCommonData? = null

        init {
            binding.root.setDebounceClickListener {
                repaymentTxnData?.let {
                    onClick(it)
                }
            }
        }

        fun setData(data: EmiTxnCommonData) {
            this.repaymentTxnData = data

            var statusTextColor: Int? = null

            when (data.getTransactionStatus()) {
                RepaymentTxnStatus.SUCCESS -> {
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_1ea787))
                }
                RepaymentTxnStatus.FAILURE -> {
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EB6A6E))
                }
                RepaymentTxnStatus.CREATED, RepaymentTxnStatus.PENDING, RepaymentTxnStatus.PROCESSING -> {
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EBB46A))
                }
            }

            statusTextColor.let { binding.tvStatus.setTextColor(it) }

            binding.tvTitle.text = data.emiTitle
            binding.tvStatus.text = data.paymentStatusText
            binding.tvAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, data.emiAmount?.toFloatOrNull()?.toInt()?.getFormattedAmount().orEmpty())
            binding.tvDate.text = data.emiDate
        }
    }
}