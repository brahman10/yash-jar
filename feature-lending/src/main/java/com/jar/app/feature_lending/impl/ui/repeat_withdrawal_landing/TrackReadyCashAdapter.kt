package com.jar.app.feature_lending.impl.ui.repeat_withdrawal_landing

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.shouldDisableClick
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.CellTrackYourReadyCashBinding
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationItemV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationStatusV2

internal class TrackReadyCashAdapter :
    ListAdapter<LoanApplicationItemV2, TrackReadyCashAdapter.TrackReadyCashHolder>(DIFF_UTIL) {

    private var listener: ((LoanApplicationItemV2) -> Unit)? = null

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LoanApplicationItemV2>() {
            override fun areItemsTheSame(
                oldItem: LoanApplicationItemV2,
                newItem: LoanApplicationItemV2
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: LoanApplicationItemV2,
                newItem: LoanApplicationItemV2
            ): Boolean {
                return oldItem.applicationId == newItem.applicationId
            }

        }
    }

    fun setCardClickListener(listener: (LoanApplicationItemV2) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackReadyCashHolder {
        val binding = CellTrackYourReadyCashBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackReadyCashHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackReadyCashHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrackReadyCashHolder(
        private val binding: CellTrackYourReadyCashBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoanApplicationItemV2) {
            binding.tvLoanName.text = item.readyCashName
            binding.progressBar.progress = item.repaymentPercentage.orZero().toInt()
            if (item.status == LoanApplicationStatusV2.DISBURSAL_PENDING.name) {
                binding.groupAmountPaid.isVisible = false
                binding.tvYetToStart.isVisible = false
                binding.tvMoneyTransferInProgress.isVisible = true
                binding.root.shouldDisableClick(true)
                binding.tvDetails.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        com.jar.app.core_ui.R.color.color_EEEAFF
                    )
                )
                binding.tvDetails.alpha = 0.3f
            } else {
                binding.tvDetails.alpha = 1f
                binding.tvDetails.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        com.jar.app.core_ui.R.color.color_EEEAFF
                    )
                )
                binding.root.shouldDisableClick(false)
                binding.tvMoneyTransferInProgress.isVisible = false
                if (item.paidEMI.orZero() > 0) {
                    binding.tvAmountPaid.text = binding.root.context.getString(
                        com.jar.app.feature_lending.shared.MR.strings.feature_lending_rupee_prefix_float.resourceId, item.paidLoanAmount.orZero()
                    )
                    binding.groupAmountPaid.isVisible = true
                    binding.tvYetToStart.isVisible = false
                } else {
                    binding.groupAmountPaid.isVisible = false
                    binding.tvYetToStart.isVisible = true
                }
            }
            if (item.status == LoanApplicationStatusV2.CLOSED.name || item.status == LoanApplicationStatusV2.FORECLOSED.name) {
                binding.tvEmiPaid.isVisible = false
                binding.tvClosed.isVisible = true
                binding.progressBar.progressTintList = null
                binding.progressBar.progressTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        com.jar.app.core_ui.R.color.color_1EA787
                    )
                )
            } else {
                binding.progressBar.progressTintList = null
                binding.progressBar.progressDrawable = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.feature_lending_progress_track_your_ready_cash
                )
                binding.tvEmiPaid.isVisible = true
                binding.tvClosed.isVisible = false
                binding.tvEmiPaid.text = binding.root.context.getString(
                    com.jar.app.feature_lending.shared.MR.strings.feature_lending_d_emi_paid.resourceId,
                    item.paidEMI.orZero(),
                    item.totalEMI.orZero()
                )
            }
            binding.root.setDebounceClickListener {
                listener?.invoke(item)
            }
        }
    }
}