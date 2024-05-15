package com.jar.app.feature_user_api.domain.model

import dev.icerock.moko.resources.StringResource
import com.jar.app.feature_user_api.shared.MR


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

enum class DurationType(val durationRes: StringResource) {
    DAY(MR.strings.feature_user_api_day),
    DAYS(MR.strings.feature_user_api_days),
    WEEK(MR.strings.feature_user_api_week),
    WEEKS(MR.strings.feature_user_api_weeks),
    MONTH(MR.strings.feature_user_api_month),
    MONTHS(MR.strings.feature_user_api_months)
}
