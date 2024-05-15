package com.jar.app.feature_gold_sip.impl.ui.sip_day_or_date.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellMonthDayBinding
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellWeekDayBinding
import com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType
import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData

internal class WeekOrMonthAdapter(
    private val sipSubscriptionType: SipSubscriptionType,
    private val onItemClick: (WeekOrMonthData, Int) -> Unit
) :
    ListAdapter<WeekOrMonthData, BaseViewHolder>(DIFF_UTIL) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (sipSubscriptionType) {
        SipSubscriptionType.WEEKLY_SIP -> WeekViewHolder(
            FeatureGoldSipCellWeekDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onItemClick
        )
        SipSubscriptionType.MONTHLY_SIP -> MonthViewHolder(
            FeatureGoldSipCellMonthDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.let {
            when (sipSubscriptionType) {
                SipSubscriptionType.WEEKLY_SIP -> {
                    (holder as WeekViewHolder).setWeekData(it)
                }
                SipSubscriptionType.MONTHLY_SIP -> {
                    (holder as MonthViewHolder).setMonthData(it)
                }
            }
        }
    }
}