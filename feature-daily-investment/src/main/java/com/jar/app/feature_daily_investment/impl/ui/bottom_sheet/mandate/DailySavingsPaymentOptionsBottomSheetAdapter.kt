package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.mandate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.util.toSentenceCase
import com.jar.app.feature_daily_investment.databinding.LayoutDailyInvestmentPaymentOptionCellBinding
import com.jar.app.feature_daily_investment.impl.domain.data.DailySavingsMandatePaymentOption
import com.jar.app.feature_mandate_payment.BuildConfig.*


class DailySavingsPaymentOptionsBottomSheetAdapter(
    private val context: Context,
    private val onItemClick: (String) -> Unit,
) : ListAdapter<DailySavingsMandatePaymentOption, DailySavingsPaymentOptionsViewHolder>(
    DIFF_CALLBACK
) {

    private var selectedPosition = 0

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<DailySavingsMandatePaymentOption>() {
                override fun areItemsTheSame(
                    oldItem: DailySavingsMandatePaymentOption,
                    newItem: DailySavingsMandatePaymentOption
                ): Boolean {
                    return oldItem.packageName == newItem.packageName
                }

                override fun areContentsTheSame(
                    oldItem: DailySavingsMandatePaymentOption,
                    newItem: DailySavingsMandatePaymentOption
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailySavingsPaymentOptionsViewHolder {
        val binding = LayoutDailyInvestmentPaymentOptionCellBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailySavingsPaymentOptionsViewHolder(binding, context, onItemClick)
    }

    override fun onBindViewHolder(holder: DailySavingsPaymentOptionsViewHolder, position: Int) {
        val paymentOption = getItem(position)
        holder.bind(paymentOption, position == selectedPosition)
    }

    fun updateSelection(position: Int) {
        if (selectedPosition != position) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }
}

class DailySavingsPaymentOptionsViewHolder(
    private val binding: LayoutDailyInvestmentPaymentOptionCellBinding,
    private val context: Context,
    private val onItemClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentOption: DailySavingsMandatePaymentOption, isSelected: Boolean) {
        binding.apply {
            when (paymentOption.packageName) {
                PAYTM_PACKAGE -> {
                    binding.ivPaymentOptionLogo.setImageResource(com.jar.app.core_ui.R.drawable.ic_paytm)
                }

                PHONEPE_PACKAGE -> {
                    binding.ivPaymentOptionLogo.setImageResource(com.jar.app.core_ui.R.drawable.ic_phonepe)
                }

                GPAY_PACKAGE -> {
                    binding.ivPaymentOptionLogo.setImageResource(com.jar.app.core_ui.R.drawable.ic_gpay)
                }
            }
            tvPaymentOption.text = paymentOption.optionName.toSentenceCase()
            cvPaymentOptionLogo.background =
                (if (isSelected) com.jar.app.feature_daily_investment.R.drawable.feature_daily_investment_rounded_11dp_transparent_bg else null)?.let {
                    ContextCompat.getDrawable(
                        context,
                        it
                    )
                }
            ivTick.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
            root.setOnClickListener {
                onItemClick(paymentOption.packageName)
                (itemView.parent as RecyclerView).adapter?.let {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        (it as DailySavingsPaymentOptionsBottomSheetAdapter).updateSelection(
                            adapterPosition
                        )
                    }
                }
            }
        }
    }
}