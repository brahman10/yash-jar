package com.jar.app.feature_gold_sip.impl.ui.update_sip.adapter.week_or_month

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellMonthDayBinding
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellUpdateWeekDayBinding
import com.jar.app.feature_gold_sip.impl.ui.sip_day_or_date.adapter.MonthViewHolder
import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData

internal class UpdateWeekOrMonthAdapter(
    private val sipSubscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType,
    private val onItemClick: (WeekOrMonthData, Int) -> Unit
) : ListAdapter<WeekOrMonthData, BaseViewHolder>(DIFF_UTIL) {

    private var subscriptionType = sipSubscriptionType

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<WeekOrMonthData>() {
            override fun areItemsTheSame(
                oldItem: WeekOrMonthData,
                newItem: WeekOrMonthData
            ): Boolean {
                return oldItem.value == newItem.value
            }

            override fun areContentsTheSame(
                oldItem: WeekOrMonthData,
                newItem: WeekOrMonthData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (subscriptionType) {
        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> UpdateWeekViewHolder(
            FeatureGoldSipCellUpdateWeekDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onItemClick
        )
        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> MonthViewHolder(
            FeatureGoldSipCellMonthDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.let {
            when (sipSubscriptionType) {
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> {
                    (holder as UpdateWeekViewHolder).setWeekData(it)
                }
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> {
                    (holder as MonthViewHolder).setMonthData(it)
                }
            }
        }
    }
}