package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.mandate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_daily_investment.databinding.LayoutDailyInvestmentMandateInfo2Binding
import com.jar.app.feature_daily_investment.databinding.LayoutDailyInvestmentMandateInfoBinding
import com.jar.app.feature_daily_investment.impl.domain.data.DailySavingsMandateInfoData
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.VIEW_TYPE_CURRENT_MANDATE
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.VIEW_TYPE_OTHER_MANDATE


internal class DailySavingsMandateStatusAdapter :
    ListAdapter<DailySavingsMandateInfoData, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DailySavingsMandateInfoData>() {
            override fun areItemsTheSame(oldItem: DailySavingsMandateInfoData, newItem: DailySavingsMandateInfoData): Boolean {
                return oldItem.mandateType == newItem.mandateType
            }

            override fun areContentsTheSame(oldItem: DailySavingsMandateInfoData, newItem: DailySavingsMandateInfoData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CURRENT_MANDATE -> {
                val binding = LayoutDailyInvestmentMandateInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CurrentMandateViewHolder(binding)
            }
            VIEW_TYPE_OTHER_MANDATE -> {
                val binding = LayoutDailyInvestmentMandateInfo2Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OtherMandateViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is CurrentMandateViewHolder -> {
                val currentMandateItem = item as DailySavingsMandateInfoData
                holder.bind(currentMandateItem)
            }
            is OtherMandateViewHolder -> {
                val otherMandateItem = item as DailySavingsMandateInfoData
                holder.bind(otherMandateItem)
            }
            else -> throw IllegalArgumentException("Invalid view holder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is DailySavingsMandateInfoData -> {
                if (item.isCurrentMandate) VIEW_TYPE_CURRENT_MANDATE
                else VIEW_TYPE_OTHER_MANDATE
            }
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    inner class CurrentMandateViewHolder(
        private val binding: LayoutDailyInvestmentMandateInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DailySavingsMandateInfoData) {
            binding.tvSubscriptionName.text = data.mandateType
            binding.tvMandateAmount.text =  "₹${data.value}/day"
        }
    }

    inner class OtherMandateViewHolder(
        private val binding: LayoutDailyInvestmentMandateInfo2Binding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DailySavingsMandateInfoData) {
            binding.tvSubscriptionName.text = data.mandateType
            binding.tvMandateAmount.text = data.value
            binding.tvSubscriptionStaus.text = data.status
        }
    }
}