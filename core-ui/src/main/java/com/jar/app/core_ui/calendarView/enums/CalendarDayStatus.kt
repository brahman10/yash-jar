package com.jar.app.core_ui.calendarView.enums

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.jar.app.base.ui.BaseResources
import com.jar.app.core_ui.R
import com.jar.app.core_ui.calendarView.model.CalendarInfo
import dev.icerock.moko.resources.StringResource

enum class CalendarDayStatus(
    val stringRes: StringResource?,
    @DrawableRes val drawableRes: Int?,
    @ColorRes val backgroundColorRes: Int,
    @ColorRes val textColorRes: Int?,
    @ColorRes val dayTextColorRes: Int?,
) : BaseResources {
    SUCCESS(
        stringRes = StringResource(R.string.feature_post_setup_savings_successful_for),
        textColorRes = com.jar.app.core_ui.R.color.white_30,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_135360,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    DS_PENNY_DROP(
        stringRes = StringResource(R.string.feature_post_setup_savings_successful_for),
        textColorRes = com.jar.app.core_ui.R.color.white_30,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_135360,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    FAILED(
        stringRes = StringResource(R.string.feature_post_setup_savings_failed_for),
        textColorRes = com.jar.app.core_ui.R.color.white,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_953D52,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    PENDING(
        stringRes = StringResource(R.string.feature_post_setup_savings_pending_for),
        textColorRes = com.jar.app.core_ui.R.color.white,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_BA8844,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    SCHEDULED(
        stringRes = null,
        textColorRes = null,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_473D67,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    NOT_CREATED(
        stringRes = null,
        textColorRes = null,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_473D67,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    PAUSED(
        stringRes = StringResource(R.string.feature_post_setup_paused),
        textColorRes = null,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_473D67,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    DISABLED(
        stringRes = null,
        textColorRes = null,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_322854,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    EMPTY(
        stringRes = null,
        textColorRes = null,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_392D61,
        dayTextColorRes = null
    ),
    IGNORED(
        stringRes = null,
        textColorRes = null,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_392D61,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    ),
    DETECTED(
        stringRes = null,
        textColorRes = null,
        drawableRes = null,
        backgroundColorRes = com.jar.app.core_ui.R.color.color_392D61,
        dayTextColorRes = com.jar.app.core_ui.R.color.white
    )
}

fun CalendarInfo.getDayStatus(): CalendarDayStatus {
    return CalendarDayStatus.valueOf(this.status)
}
