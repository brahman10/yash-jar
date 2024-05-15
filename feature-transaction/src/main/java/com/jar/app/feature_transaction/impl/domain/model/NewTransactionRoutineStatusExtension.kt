package com.jar.app.feature_transaction.impl.domain.model

import com.jar.app.feature_transactions_common.shared.NewTransactionRoutineStatus

fun NewTransactionRoutineStatus.getStatusColor(): Int {
    return when(this) {
        NewTransactionRoutineStatus.COMPLETED -> com.jar.app.core_ui.R.color.color_58DDC8
        NewTransactionRoutineStatus.PROCESSING -> com.jar.app.core_ui.R.color.color_EBB46A
        NewTransactionRoutineStatus.FAILED -> com.jar.app.core_ui.R.color.color_EB6A6E
        NewTransactionRoutineStatus.INACTIVE -> com.jar.app.core_ui.R.color.color_776E94
    }
}

fun NewTransactionRoutineStatus.getStatusBackground(): Int {
    return when(this) {
        NewTransactionRoutineStatus.COMPLETED -> com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_green_273442_6dp
        NewTransactionRoutineStatus.PROCESSING -> com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_yellow_43353b_6dp
        NewTransactionRoutineStatus.FAILED -> com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_red_413046_6dp
        NewTransactionRoutineStatus.INACTIVE -> com.jar.app.core_ui.R.drawable.core_ui_bg_circle_776e94
    }
}

fun NewTransactionRoutineStatus.getStatusIcon(): Int {
    return when(this) {
        NewTransactionRoutineStatus.COMPLETED -> com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick
        NewTransactionRoutineStatus.PROCESSING -> com.jar.app.core_ui.R.drawable.core_ui_ic_hour_glass_bg_yellow
        NewTransactionRoutineStatus.FAILED -> com.jar.app.core_ui.R.drawable.core_ui_ic_red_cross
        NewTransactionRoutineStatus.INACTIVE -> com.jar.app.core_ui.R.drawable.core_ui_bg_circle_776e94
    }
}