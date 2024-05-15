package com.jar.android.feature_post_setup.impl.ui.setup_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.android.feature_post_setup.databinding.DsBottomsheetSingleItemBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders.DailySavingCancellationBottomSheetViewHolder
import com.jar.app.core_base.domain.model.GenericFaqItem
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDateList
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import kotlinx.coroutines.CoroutineScope

internal class PostSetupDSBottomSheetAdapter(
    private val uiScope: CoroutineScope,
    private val onItemClick: (DailyInvestmentPauseDateList) -> Unit,
) : ListAdapter<DailyInvestmentPauseDateList, DailySavingCancellationBottomSheetViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<DailyInvestmentPauseDateList>() {
            override fun areItemsTheSame(oldItem: DailyInvestmentPauseDateList, newItem: DailyInvestmentPauseDateList): Boolean {
                return oldItem.noOfDay == newItem.noOfDay
            }

            override fun areContentsTheSame(oldItem: DailyInvestmentPauseDateList, newItem: DailyInvestmentPauseDateList): Boolean {
                return oldItem == newItem
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailySavingCancellationBottomSheetViewHolder {
        return DailySavingCancellationBottomSheetViewHolder(
            DsBottomsheetSingleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick,
            uiScope
        )
    }

    override fun onBindViewHolder(holder: DailySavingCancellationBottomSheetViewHolder, position: Int) {
        getItem(position)?.let {
            holder.binding(it)
        }
    }
}