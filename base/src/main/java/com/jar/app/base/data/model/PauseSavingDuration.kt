package com.jar.app.base.data.model

import androidx.annotation.StringRes
import com.jar.app.base.R

data class PauseSavingDuration(
    val pauseFor: Int,
    val identifier: String,
    val pausedEnumValue: String,
    var isSelected: Boolean
) {
    fun getDurationText() = pauseFor.toString().plus(" ").plus(identifier)

}

enum class PauseSavingOption(val timeValue: Int, val durationType: DurationType) {
    ONE(timeValue = 1, durationType = DurationType.DAY),
    TWO(timeValue = 2, durationType = DurationType.DAYS),
    FIVE(timeValue = 5, durationType = DurationType.DAYS),
    EIGHT(timeValue = 8, durationType = DurationType.DAYS),
    TEN(timeValue = 10, durationType = DurationType.DAYS),
    TWELVE(timeValue = 12, durationType = DurationType.DAYS),
    FIFTEEN(timeValue = 15, durationType = DurationType.DAYS),
    TWENTY(timeValue = 20, durationType = DurationType.DAYS),
    WEEK(timeValue = 1, durationType = DurationType.WEEK),
    TWO_WEEKS(timeValue = 2, durationType = DurationType.WEEKS),
    THREE_WEEKS(timeValue = 3, durationType = DurationType.WEEKS),
    FOUR_WEEKS(timeValue = 4, durationType = DurationType.WEEKS),
    MONTH(timeValue = 1, durationType = DurationType.MONTH);

    companion object {
        fun getTypeForValue(value: String): PauseSavingOption {
            return PauseSavingOption.valueOf(value)
        }
    }
}

enum class DurationType(@StringRes val durationRes: Int) {
    DAY(R.string.base_day),
    DAYS(R.string.base_days),
    WEEK(R.string.base_week),
    WEEKS(R.string.base_weeks),
    MONTH(R.string.base_month),
    MONTHS(R.string.base_months)
}

fun getPauseSavingObject(value: String, isSelected: Boolean = false): PauseSavingDuration {
    return when (value) {
        PauseSavingOption.ONE.name -> PauseSavingDuration(
            1,
            "Day",
            PauseSavingOption.ONE.name,
            isSelected
        )

        PauseSavingOption.FIVE.name -> PauseSavingDuration(
            5,
            "Day",
            PauseSavingOption.FIVE.name,
            isSelected
        )

        PauseSavingOption.TEN.name -> PauseSavingDuration(
            10,
            "Days",
            PauseSavingOption.TEN.name,
            isSelected
        )

        PauseSavingOption.FIFTEEN.name -> PauseSavingDuration(
            15,
            "Days",
            PauseSavingOption.FIFTEEN.name,
            isSelected
        )

        PauseSavingOption.TWENTY.name -> PauseSavingDuration(
            20,
            "Days",
            PauseSavingOption.TWENTY.name,
            isSelected
        )

        PauseSavingOption.WEEK.name -> PauseSavingDuration(
            1,
            "Weeks",
            PauseSavingOption.WEEK.name,
            isSelected
        )

        PauseSavingOption.TWO_WEEKS.name -> PauseSavingDuration(
            2,
            "Weeks",
            PauseSavingOption.TWO_WEEKS.name,
            isSelected
        )

        PauseSavingOption.THREE_WEEKS.name -> PauseSavingDuration(
            3,
            "Weeks",
            PauseSavingOption.THREE_WEEKS.name,
            isSelected
        )

        PauseSavingOption.FOUR_WEEKS.name -> PauseSavingDuration(
            4,
            "Weeks",
            PauseSavingOption.FOUR_WEEKS.name,
            isSelected
        )

        PauseSavingOption.MONTH.name -> PauseSavingDuration(
            1,
            "Month",
            PauseSavingOption.MONTH.name,
            isSelected
        )

        else -> PauseSavingDuration(value.toIntOrNull() ?: 0, "Month", "Zero", isSelected)
    }
}