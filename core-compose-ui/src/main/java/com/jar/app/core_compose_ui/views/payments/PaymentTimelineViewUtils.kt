package com.jar.app.core_compose_ui.views.payments

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.jar.app.base.util.orFalse
import com.jar.app.core_ui.R


@Composable
internal fun colorForButtonText(status: TransactionStatus?): Color {
    return when (status) {
        TransactionStatus.SUCCESS -> colorResource(id = R.color.color_58DDC8)
        TransactionStatus.PENDING, null -> colorResource(id = R.color.color_EBB46A)
        TransactionStatus.FAILED -> colorResource(id = R.color.color_EB6A6E)
    }
}

internal fun getTextForStatus(status: TransactionStatus?, getStringFunction: (Int) -> String): String {
    return when (status) {
        TransactionStatus.SUCCESS -> getStringFunction (com.jar.app.core_compose_ui.R.string.core_compose_ui_success)
        TransactionStatus.PENDING -> getStringFunction (com.jar.app.core_compose_ui.R.string.core_compose_ui_in_progress)
        TransactionStatus.FAILED -> getStringFunction (com.jar.app.core_compose_ui.R.string.core_compose_ui_failed)
        null -> ""
    }
}

@DrawableRes
internal fun getIconForStatus(status: TransactionStatus?): Int {
    return when (status) {
        TransactionStatus.SUCCESS -> com.jar.app.core_ui.R.drawable.core_ui_icon_check_filled
        TransactionStatus.PENDING -> com.jar.app.core_compose_ui.R.drawable.core_ui_hourglass
        TransactionStatus.FAILED -> com.jar.app.core_compose_ui.R.drawable.core_ui_failed
        null -> com.jar.app.core_compose_ui.R.drawable.core_ui_purple_circle
    }
}

internal fun colorForTitle(status: TransactionStatus?): Int {
    return when (status) {
        null -> com.jar.app.core_ui.R.color.color_776E94
        else -> com.jar.app.core_ui.R.color.white
    }
}

@Composable
internal fun colorForDivider(
    status: TransactionStatus?,
    nextStatus: TransactionStatus?
): Color {
    if (nextStatus == null) return colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)
    return when (status) {
        TransactionStatus.SUCCESS -> colorResource(id = com.jar.app.core_ui.R.color.color_1EA787)
        TransactionStatus.PENDING, null -> colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)
        TransactionStatus.FAILED -> colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E)
    }
}

internal fun shouldShowRefreshTextDivider(
    refreshText: String?,
    isLastIndex: Boolean,
    shouldShowDividerAtLast: (() -> Boolean)?
) = (if (isLastIndex) shouldShowDividerAtLast?.invoke()
    .orFalse() else true) && !refreshText.isNullOrBlank()

internal fun isStrokeShown(status: TransactionStatus?): Boolean {
    return when (status) {
        TransactionStatus.SUCCESS -> false
        TransactionStatus.PENDING -> true
        TransactionStatus.FAILED -> true
        null -> false
    }
}


internal fun shouldShowRetryRow(status: TransactionStatus?): Boolean {
    return when (status) {
        TransactionStatus.SUCCESS -> false
        TransactionStatus.PENDING -> true
        TransactionStatus.FAILED -> false
        null -> false
    }
}
