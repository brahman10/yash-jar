package com.jar.app.core_ui.calendarView.util

import androidx.fragment.app.FragmentActivity
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_ui.R
import com.jar.app.core_ui.calendarView.enums.CalendarDayStatus
import com.jar.app.core_ui.calendarView.model.CalendarInfo
import com.jar.app.core_ui.calendarView.model.WeekDayData
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CalendarUtil @Inject constructor(private val dispatcherProvider: DispatcherProvider): BaseResources {

    companion object {
        const val DEFAULT_FORMAT_MONTH = "MMMM"
        const val NUMBER_OF_DAYS_IN_WEEK = 7
        const val FIRST_MONTH_IN_YEAR_INDEX = 0
        const val LAST_MONTH_IN_YEAR_INDEX = 11
    }

    private val calendar = Calendar.getInstance()

    fun getMonthAndYearString(
        format: String = DEFAULT_FORMAT_MONTH,
        monthIndex: Int,
    ): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val year = calendar.get(Calendar.YEAR)
        calendar.apply { set(Calendar.MONTH, monthIndex) }
        val month = sdf.format(calendar.time)
        val monthAndYear = StringBuilder().append(month).append(" ").append(year)
        return monthAndYear.toString()
    }

    fun getMonthString(
        format: String = DEFAULT_FORMAT_MONTH,
        monthIndex: Int,
    ): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        calendar.apply { set(Calendar.MONTH, monthIndex) }
        val month = sdf.format(calendar.time)
        return month.toString()
    }


    suspend fun createMonthCalendar(
        monthIndex: Int,
        calenderList: List<CalendarInfo>
    ): ArrayList<CalendarInfo> =
        withContext(dispatcherProvider.io) {
            calendar.apply { set(Calendar.MONTH, monthIndex) }
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayOfCurrentMonth = calendar.get(Calendar.DAY_OF_WEEK)
            val list = ArrayList<CalendarInfo>()
            var count = calenderList.size
            for (day in 1 until firstDayOfCurrentMonth) {
                count++
                list.add(
                    CalendarInfo(
                        "",
                        -1,
                        CalendarDayStatus.EMPTY.name,
                        null,
                    )
                )
            }
            list.addAll(calenderList)
            while (count < (NUMBER_OF_DAYS_IN_WEEK * calendar.getActualMaximum(Calendar.WEEK_OF_MONTH))) {
                count++
                list.add(
                    CalendarInfo(
                        "",
                        -1,
                        CalendarDayStatus.EMPTY.name,
                        null
                    )
                )
            }
            return@withContext list
        }

    suspend fun getWeekList(
        contextRef: WeakReference<FragmentActivity>
    ): List<WeekDayData> =
        withContext(dispatcherProvider.default) {
            val context = contextRef.get()!!
            val list = ArrayList<WeekDayData>()
            list.add(
                WeekDayData(
                    context.getString(R.string.feature_post_setup_sunday), 1
                )
            )
            list.add(
                WeekDayData(
                    context.getString(R.string.feature_post_setup_monday), 2
                )
            )
            list.add(
                WeekDayData(
                    context.getString(R.string.feature_post_setup_tuesday), 3
                )
            )
            list.add(
                WeekDayData(
                    context.getString(R.string.feature_post_setup_wednesday), 4
                )
            )
            list.add(
                WeekDayData(
                    context.getString(R.string.feature_post_setup_thursday), 5
                )
            )
            list.add(
                WeekDayData(
                    context.getString(R.string.feature_post_setup_friday), 6
                )
            )
            list.add(
                WeekDayData(
                    context.getString(R.string.feature_post_setup_saturday), 7
                )
            )
            return@withContext list
        }

    fun getWeekFromDay(
        contextRef: WeakReference<FragmentActivity>,
        day: Int
    ): String {
        val context = contextRef.get()!!
        return when (day) {
            1 -> context.getString(R.string.feature_post_setup_sunday)
            2 -> context.getString(R.string.feature_post_setup_monday)
            3 -> context.getString(R.string.feature_post_setup_tuesday)
            4 -> context.getString(R.string.feature_post_setup_wednesday)
            5 -> context.getString(R.string.feature_post_setup_thursday)
            6 -> context.getString(R.string.feature_post_setup_friday)
            7 -> context.getString(R.string.feature_post_setup_saturday)
            else -> {
                context.getString(R.string.feature_post_setup_sunday)
            }
        }
    }

    fun getCurrentMonthIndex(): Int {
        calendar.apply { time = Date() }
        return calendar.get(Calendar.MONTH)
    }

    fun getMonthFirstDay(monthIndex: Int): String {
        calendar.set(Calendar.MONTH, monthIndex)
        return "${calendar.getActualMinimum(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(Calendar.YEAR)
        }"
    }

    fun getMonthLastDay(monthIndex: Int): String {
        calendar.set(Calendar.MONTH, monthIndex)
        return "${calendar.getActualMaximum(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(Calendar.YEAR)
        }"
    }

    fun shouldEnablePreviousMonthButton(monthIndex: Int, startDate: Long): Boolean {
        calendar.set(Calendar.MONTH, monthIndex)
        return startDate < calendar.timeInMillis
    }

    fun shouldEnableNextMonthButton(monthIndex: Int, endDate: Long): Boolean {
        calendar.set(Calendar.MONTH, monthIndex)
        return endDate > getLastDayOfMonthInEpoch()
    }

    fun getCurrentCalenderMonthIndex() = calendar.get(Calendar.MONTH)

    fun updateCurrentCalenderYear(shouldIncreaseYear: Boolean) {
        var year = calendar.get(Calendar.YEAR)
        if (shouldIncreaseYear) {
            calendar.set(Calendar.YEAR, ++year)
            calendar.set(Calendar.MONTH, FIRST_MONTH_IN_YEAR_INDEX)
        } else {
            calendar.set(Calendar.YEAR, --year)
            calendar.set(Calendar.MONTH, LAST_MONTH_IN_YEAR_INDEX)
        }
    }

    private fun getLastDayOfMonthInEpoch(): Long {
        val calender = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        return calender.timeInMillis
    }
}