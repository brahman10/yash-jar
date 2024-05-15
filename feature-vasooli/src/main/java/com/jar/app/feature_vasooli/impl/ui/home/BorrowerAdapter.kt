package com.jar.app.feature_vasooli.impl.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.epochToDate
import com.jar.app.base.util.getFormattedDate
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.databinding.CellBorrowerBinding
import com.jar.app.feature_vasooli.impl.domain.model.Borrower
import com.jar.app.feature_vasooli.impl.domain.model.VasooliStatus

internal class BorrowerAdapter(
    private val onBorrowerClicked: (borrowerData: Borrower) -> Unit
): ListAdapter<Borrower, BorrowerAdapter.BorrowerViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Borrower>() {
            override fun areItemsTheSame(oldItem: Borrower, newItem: Borrower): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Borrower, newItem: Borrower): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowerViewHolder {
        val binding = CellBorrowerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BorrowerViewHolder(binding, onBorrowerClicked)
    }

    override fun onBindViewHolder(holder: BorrowerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class BorrowerViewHolder(
        private val binding: CellBorrowerBinding,
        private val onBorrowerClicked: (borrowerData: Borrower) -> Unit
    ): BaseViewHolder(binding.root) {

        private var data: Borrower? = null

        init {
            binding.root.setDebounceClickListener {
                data?.let {
                    onBorrowerClicked.invoke(it)
                }
            }
        }

        fun bind(data: Borrower) {
            this.data = data

            binding.tvName.text = data.borrowerName.orEmpty()
            binding.tvAmountDue.text = context.resources.getString(
                R.string.feature_vasooli_due_amount_x,
                data.dueAmount.toString(),
            )
            binding.tvAmountTotal.text = context.resources.getString(
                R.string.feature_vasooli_total_amount_y,
                data.borrowedAmount.toString(),
            )
            binding.ivLender.setDrawableFromName(
                name = data.borrowerName.orEmpty(),
                textColor = com.jar.app.core_ui.R.color.color_375B6F,
                backgroundColor = com.jar.app.core_ui.R.color.color_7DB2CF
            )

            binding.tvDateLastRepayment.text = data.lastRepaymentDate?.epochToDate()?.getFormattedDate("dd LLLL ''yy").orEmpty()
            binding.tvDateLastRepayment.isVisible = data.lastRepaymentDate != null && data.lastRepaymentDate != 0L

            if (data.lastRepaymentAmount != null) {
                binding.tvAmountLastRepayment.text = context.getString(
                    R.string.feature_vasooli_last_repayment_x,
                    data.lastRepaymentAmount.orZero().toString()
                )
            } else {
                binding.tvAmountLastRepayment.text = context.getString(R.string.feature_vasooli_last_repayment_default)
            }
            binding.tvAmountLastRepayment.isVisible = true

            when (data.status) {
                VasooliStatus.ACTIVE.name -> {
                    binding.tvAmountLastRepayment.text = context.getString(R.string.feature_vasooli_last_repayment_default)
                    binding.groupDue.isVisible = true
                    binding.tvRepaid.isVisible = false
                }
                VasooliStatus.PARTIALLY_RECOVERED.name -> {
                    binding.groupDue.isVisible = true
                    binding.tvRepaid.isVisible = false
                }
                VasooliStatus.RECOVERED.name -> {
                    binding.tvRepaid.background = ContextCompat.getDrawable(
                        context, R.drawable.feature_vasooli_rounded_58ddc8_5dp
                    )
                    binding.tvRepaid.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.feature_vasooli_ic_green_tick, 0, 0, 0
                    )
                    binding.tvRepaid.text = context.getString(R.string.feature_vasooli_fully_repaid)
                    binding.tvRepaid.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.color_58DDC8
                        )
                    )
                    binding.tvRepaid.isVisible = true
                    binding.groupDue.isVisible = false
                }
                VasooliStatus.DEFAULT.name -> {
                    binding.tvRepaid.background = ContextCompat.getDrawable(
                        context, R.drawable.feature_vasooli_rounded_red_opacity10_5dp
                    )
                    binding.tvRepaid.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.feature_vasooli_ic_close, 0, 0, 0
                    )
                    binding.tvRepaid.text = context.getString(R.string.feature_vasooli_default)
                    binding.tvRepaid.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.redAlertText
                        )
                    )
                    binding.tvRepaid.isVisible = true
                    binding.groupDue.isVisible = false
                }
            }
        }
    }
}