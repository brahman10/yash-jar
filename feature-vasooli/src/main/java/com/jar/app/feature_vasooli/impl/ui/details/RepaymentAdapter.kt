package com.jar.app.feature_vasooli.impl.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.epochToDate
import com.jar.app.base.util.getFormattedDate
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.databinding.CellRepaymentBinding
import com.jar.app.feature_vasooli.impl.domain.model.Repayment

internal class RepaymentAdapter : ListAdapter<Repayment, RepaymentAdapter.RepaymentViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Repayment>() {
            override fun areItemsTheSame(oldItem: Repayment, newItem: Repayment): Boolean {
                return  newItem == oldItem
            }

            override fun areContentsTheSame(oldItem: Repayment, newItem: Repayment): Boolean {
                return newItem == oldItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepaymentViewHolder {
        val binding = CellRepaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepaymentViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class RepaymentViewHolder(
        private val binding: CellRepaymentBinding
    ) : BaseViewHolder(binding.root) {

        fun bind(repayment: Repayment) {
            binding.tvRepaymentAmount.text = context.getString(R.string.feature_vasooli_currency_sign_x_int, repayment.amount)
            binding.tvRepaymentDate.text = repayment.repaidOn.epochToDate().getFormattedDate("dd LLLL ''yy")
            binding.tvRepaymentMode.text = context.getString(R.string.feature_vasooli_paid_with_x, (repayment.paymentMode ?: "--"))
        }

    }
}