package com.jar.android.feature_post_setup.impl.ui.failed_transactions

import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellCalenderFailedDayBinding
import com.jar.android.feature_post_setup.impl.model.CalendarDayStatus
import com.jar.android.feature_post_setup.impl.model.getDayStatus
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.formatNumber
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo

internal class FailedDayTransactionViewHolder(
    val binding: FeaturePostSetupCellCalenderFailedDayBinding,
    private val onItemClick: (FeaturePostSetUpCalendarInfo, Int) -> Unit
) : BaseViewHolder(binding.root), BaseResources {

    private var calendarInfo: FeaturePostSetUpCalendarInfo? = null

    init {
        binding.root.setDebounceClickListener {
            calendarInfo?.let {
                if (it.getDayStatus() == CalendarDayStatus.FAILED)
                    onItemClick.invoke(it, bindingAdapterPosition)
            }
        }
    }

    fun bind(calendarInfo: FeaturePostSetUpCalendarInfo) {
        this.calendarInfo = calendarInfo
        val calendarDayStatus = calendarInfo.getDayStatus()
        binding.tvSelectionCircle.isVisible = calendarDayStatus == CalendarDayStatus.FAILED
        binding.tvSelectionCircle.isSelected = calendarInfo.isSelected.orFalse()
        binding.tvDay.text = calendarInfo.day.toString()
        when (calendarDayStatus) {
            CalendarDayStatus.SUCCESS,
            CalendarDayStatus.PENDING,
            CalendarDayStatus.SCHEDULED,
            CalendarDayStatus.DISABLED,
            CalendarDayStatus.PAUSED,
            CalendarDayStatus.IGNORED,
            CalendarDayStatus.DETECTED,
            CalendarDayStatus.DS_PENNY_DROP,
            CalendarDayStatus.NOT_CREATED -> {
                handleOtherStatesUI()
                binding.root.backgroundTintList = ContextCompat.getColorStateList(
                    binding.root.context,
                    com.jar.app.core_ui.R.color.color_473D67
                )
            }
            CalendarDayStatus.FAILED -> {
                binding.root.backgroundTintList = ContextCompat.getColorStateList(
                    binding.root.context,
                    calendarDayStatus.backgroundColorRes
                )
                calendarInfo.amount?.let {
                    binding.tvAmount.text = if (it > 999)
                        it.toInt().formatNumber()
                    else
                        binding.root.context.getString(
                            com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                            it.toInt()
                        )
                }
            }
            CalendarDayStatus.EMPTY -> {
                handleEmptyStateUI()
                binding.root.backgroundTintList = ContextCompat.getColorStateList(
                    binding.root.context,
                    calendarDayStatus.backgroundColorRes
                )
            }
        }

    }

    private fun handleOtherStatesUI(){
        binding.tvSelectionCircle.isInvisible = true
        binding.tvAmount.isInvisible = true
    }

    private fun handleEmptyStateUI(){
        binding.tvSelectionCircle.isInvisible = true
        binding.tvAmount.isInvisible = true
        binding.tvDay.isInvisible = true
    }

}