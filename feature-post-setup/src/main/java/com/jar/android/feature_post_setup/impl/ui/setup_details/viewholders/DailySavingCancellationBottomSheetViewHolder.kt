package com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders

import com.jar.android.feature_post_setup.databinding.DsBottomsheetSingleItemBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDateList
import com.jar.app.feature_post_setup.shared.PostSetupMR
import kotlinx.coroutines.CoroutineScope

internal class DailySavingCancellationBottomSheetViewHolder(
    private val binding: DsBottomsheetSingleItemBinding,
    private val onItemClicked: (DailyInvestmentPauseDateList) -> Unit,
    private val uiScope: CoroutineScope,
    ) : BaseViewHolder(binding.root) {

    fun binding(dailyInvestmentPauseDateList : DailyInvestmentPauseDateList) {
        binding.tvLeftHeader.text = getTotalTime(dailyInvestmentPauseDateList.noOfDay)
        binding.tvRightHeader.text = dailyInvestmentPauseDateList.tillDate

        binding.clRootContainer.setDebounceClickListener {
            onItemClicked.invoke(dailyInvestmentPauseDateList)
        }

        if (dailyInvestmentPauseDateList.isSelected) {
            binding.clRootContainer.background = context.resources.getDrawable(com.jar.android.feature_post_setup.R.drawable.feature_post_setup_bg_rounded_3c3357_10dp)
            binding.ivSelected.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selected_v2)
        } else {
            binding.clRootContainer.background = context.resources.getDrawable(com.jar.app.core_ui.R.drawable.bg_rounded_776e94_10dp)
            binding.ivSelected.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_bg_radio_unselected_v2)
        }
    }

    private fun getTotalTime(days: Int): String {
        return when (days) {
            1 -> {
                getCustomString(PostSetupMR.strings.till_tomorrow)
            }

            7 -> {
                getCustomString(PostSetupMR.strings.for_1_week)
            }

            14 -> {
                getCustomString(PostSetupMR.strings.for_2_week)
            }

            else -> {
                getCustomString(PostSetupMR.strings.permanently)
            }
        }
    }
}