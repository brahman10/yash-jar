package com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders

import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jar.android.feature_post_setup.impl.model.CalendarDayStatus
import com.jar.android.feature_post_setup.impl.model.getDayStatus
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.formatNumber
import com.jar.app.base.util.orFalse
import com.jar.app.core_ui.databinding.FeaturePostSetupCellCalenderDayBinding
import com.jar.app.core_ui.extension.setDebounceClickListener

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo

internal class SetupCalenderDayViewHolder(
    val binding: FeaturePostSetupCellCalenderDayBinding,
    private val onItemClick: (FeaturePostSetUpCalendarInfo) -> Unit
) :
    BaseViewHolder(binding.root), BaseResources {

    private var calendarInfo: FeaturePostSetUpCalendarInfo? = null

    init {
        binding.root.setDebounceClickListener {
            calendarInfo?.let {
                if (isItemClickable())
                    it.id?.let { id -> onItemClick.invoke(it) }
            }
        }
    }

    fun bind(calendarInfo: FeaturePostSetUpCalendarInfo) {
        this.calendarInfo = calendarInfo
        val calendarDayStatus = calendarInfo.getDayStatus()
        binding.tvDay.text = calendarInfo.day.toString()
        setViewsAccToStatus(calendarDayStatus)
        //Added isInvisible here so that UI doesn't get distorted in different cases
        calendarInfo.amount?.let {
            binding.tvAmount.isInvisible = false
            binding.tvAmount.text =
                if (it > 999)
                    it.toInt().formatNumber()
                else
                    binding.root.context.getString(
                        com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                        it.toInt()
                    )
        } ?: run {
            binding.tvAmount.isInvisible = true
        }
        binding.bankServerDot.isVisible = calendarInfo.isLadderingPresent.orFalse()
        binding.root.backgroundTintList = ContextCompat.getColorStateList(
            binding.root.context,
            calendarDayStatus.backgroundColorRes
        )
        calendarDayStatus.textColorRes?.let {
            binding.tvAmount.setTextColor(ContextCompat.getColor(binding.root.context, it))
        }

        calendarDayStatus.dayTextColorRes?.let {
            binding.tvDay.isInvisible = false
            binding.tvDay.setTextColor(ContextCompat.getColor(binding.root.context, it))
        } ?: run {
            binding.tvDay.isInvisible = true
        }
    }

    private fun setViewsAccToStatus(calendarDayStatus: CalendarDayStatus) {
        when (calendarDayStatus) {
            CalendarDayStatus.SUCCESS,
            CalendarDayStatus.FAILED,
            CalendarDayStatus.PENDING,
            CalendarDayStatus.SCHEDULED -> {
                binding.disabledStateOverlayBackground.isVisible = false
                binding.ivPauseState.isVisible = false
                binding.ivStateIcon.isVisible = false
            }

            CalendarDayStatus.DS_PENNY_DROP -> {
                binding.ivPauseState.isVisible = false
                binding.ivStateIcon.isVisible = true
            }

            CalendarDayStatus.IGNORED, CalendarDayStatus.DETECTED, CalendarDayStatus.DISABLED -> {
                binding.disabledStateOverlayBackground.isVisible = true
                binding.ivPauseState.isVisible = false
                binding.ivStateIcon.isVisible = false
            }

            CalendarDayStatus.NOT_CREATED -> {
                binding.ivPauseState.isVisible = false
                binding.ivStateIcon.isVisible = false
            }

            CalendarDayStatus.PAUSED -> {
                binding.ivPauseState.isVisible = true
                binding.ivStateIcon.isVisible = false
                binding.disabledStateOverlayBackground.isVisible = false
                calendarDayStatus.drawableRes?.let { binding.ivPauseState.setImageResource(it) }
            }

            CalendarDayStatus.EMPTY -> {
                binding.ivPauseState.isVisible = false
                binding.ivStateIcon.isVisible = false
                binding.disabledStateOverlayBackground.isVisible = false
            }
        }
    }

    private fun isItemClickable() =
        (calendarInfo?.getDayStatus() == CalendarDayStatus.SUCCESS)
                || (calendarInfo?.getDayStatus() == CalendarDayStatus.FAILED)
                || (calendarInfo?.getDayStatus() == CalendarDayStatus.PENDING)

}