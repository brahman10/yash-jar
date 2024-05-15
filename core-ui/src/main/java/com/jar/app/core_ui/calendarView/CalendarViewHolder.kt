package com.jar.app.core_ui.calendarView

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.R
import com.jar.app.core_ui.calendarView.adapter.SetupCalenderDayAdapter
import com.jar.app.core_ui.calendarView.model.CalendarInfo
import com.jar.app.core_ui.calendarView.model.SavingOperations
import com.jar.app.core_ui.calendarView.util.CalendarUtil
import com.jar.app.core_ui.calendarView.viewholder.CalenderViewPageItem
import com.jar.app.core_ui.databinding.CellCalenderBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.core_ui.widget.button.ButtonType
import kotlinx.coroutines.CoroutineScope

internal class CalendarViewHolder(
    private val binding: CellCalenderBinding,
    private val uiScope: CoroutineScope,
    private val onDayClick: (CalendarInfo) -> Unit,
    private val onNextMonthClicked: () -> Unit,
    private val onPrevMonthClicked: () -> Unit,
    private val onDSOperationCtaClicked: (SavingOperations) -> Unit,
) : BaseViewHolder(binding.root), BaseResources {

    private var adapter: SetupCalenderDayAdapter? = null
    private var calenderViewPageItem: CalenderViewPageItem? = null
    private val spaceItemDecoration = SpaceItemDecoration(1.dp, 1.dp, escapeEdges = true)

    init {
        adapter = SetupCalenderDayAdapter {
            onDayClick.invoke(it)
        }

        binding.calenderView.rvCalender.layoutManager =
            GridLayoutManager(binding.root.context, CalendarUtil.NUMBER_OF_DAYS_IN_WEEK)
        binding.calenderView.rvCalender.adapter = adapter
        binding.calenderView.rvCalender.addItemDecorationIfNoneAdded(spaceItemDecoration)

        binding.calenderView.ivNext.setDebounceClickListener {
            calenderViewPageItem?.let { onNextMonthClicked.invoke() }
        }
        binding.calenderView.ivPrevious.setDebounceClickListener {
            calenderViewPageItem?.let { onPrevMonthClicked.invoke() }
        }
    }

    fun setupCalendar(calenderViewPageItem: CalenderViewPageItem) {
        this.calenderViewPageItem = calenderViewPageItem
        setupSavingActionsUI()
        setupCalendarUI()
    }

    private fun setupCalendarUI() {
        adapter?.submitList(calenderViewPageItem?.calendarInfo)

        binding.calenderView.ivPrevious.alpha =
            if (calenderViewPageItem?.isPreviousClickEnabled.orFalse()) 1f else 0.5f
        binding.calenderView.ivPrevious.isClickable =
            calenderViewPageItem?.isPreviousClickEnabled.orFalse()

        binding.calenderView.ivNext.alpha =
            if (calenderViewPageItem?.isNextClickEnabled.orFalse()) 1f else 0.5f
        binding.calenderView.ivNext.isClickable =
            calenderViewPageItem?.isNextClickEnabled.orFalse()

        binding.calenderView.tvMonthYear.text = calenderViewPageItem?.yearAndMonthText.orEmpty()

    }

    private fun setupSavingActionsUI() {
        binding.clSavingsActions.isVisible =
            calenderViewPageItem?.calendarSavingOperations?.savingOperations?.isNotEmpty().orFalse()
        binding.clBtn1.isVisible =
            calenderViewPageItem?.calendarSavingOperations?.savingOperations?.isNotEmpty().orFalse()
        binding.clBtn2.isVisible =
            calenderViewPageItem?.calendarSavingOperations?.savingOperations?.size.orZero() > 1
        binding.clBtn1.setText(
            calenderViewPageItem?.calendarSavingOperations?.savingOperations?.getOrNull(
                0
            )?.title.orEmpty()
        )
        Glide.with(binding.root.context)
            .load(calenderViewPageItem?.calendarSavingOperations?.savingOperations?.getOrNull(0)?.icon)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    binding.clBtn1.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        resource,
                        null,
                        null,
                        null
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        binding.clBtn2.setText(
            calenderViewPageItem?.calendarSavingOperations?.savingOperations?.getOrNull(
                1
            )?.title.orEmpty()
        )
        Glide.with(binding.root.context)
            .load(calenderViewPageItem?.calendarSavingOperations?.savingOperations?.getOrNull(1)?.icon)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    binding.clBtn2.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        resource,
                        null,
                        null,
                        null
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        binding.clBtn1.setDebounceClickListener {
            calenderViewPageItem?.calendarSavingOperations?.savingOperations?.getOrNull(0)?.let {
                onDSOperationCtaClicked.invoke(it)
            }
        }

        binding.clBtn2.setDebounceClickListener {
            calenderViewPageItem?.calendarSavingOperations?.savingOperations?.get(1)?.let {
                onDSOperationCtaClicked.invoke(it)
            }
        }

        setOperationButtonsBackground()
    }

    private fun autoSlideToNext(view1: View, view2: View) {
        view1.slideToRevealNew(view2)
    }

    private fun setOperationButtonsBackground() {
        binding.clBtn1.setCustomButtonStyle(
            if (calenderViewPageItem?.calendarSavingOperations?.savingOperations?.getOrNull(0)?.isPrimary.orFalse())
                ButtonType.primaryButton
            else
                ButtonType.secondaryHollowButton
        )
        binding.clBtn2.setCustomButtonStyle(
            if (calenderViewPageItem?.calendarSavingOperations?.savingOperations?.getOrNull(1)?.isPrimary.orFalse())
                ButtonType.primaryButton
            else
                ButtonType.secondaryHollowButton
        )
    }

}