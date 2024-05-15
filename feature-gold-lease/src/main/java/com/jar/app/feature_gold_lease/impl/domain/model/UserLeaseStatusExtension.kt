package com.jar.app.feature_gold_lease.impl.domain.model

import com.jar.app.feature_gold_lease.shared.domain.model.UserLeaseStatus
import com.jar.app.feature_gold_lease.R

fun UserLeaseStatus.getStatusTextColor(): Int {
    return when (this) {
        UserLeaseStatus.ACTIVE -> com.jar.app.core_ui.R.color.color_58DDC8
        UserLeaseStatus.IN_PROGRESS -> com.jar.app.core_ui.R.color.color_EBB46A
        UserLeaseStatus.CANCELLED -> com.jar.app.core_ui.R.color.color_EB6A6E
        UserLeaseStatus.CLOSED -> com.jar.app.core_ui.R.color.color_789BDE
        UserLeaseStatus.COMPLETED -> com.jar.app.core_ui.R.color.color_58DDC8
        UserLeaseStatus.FAILED -> com.jar.app.core_ui.R.color.color_EB6A6E
    }
}

fun UserLeaseStatus.getStatusBg(): Int {
    return when (this) {
        UserLeaseStatus.ACTIVE -> R.drawable.feature_gold_lease_bg_rounded_green_273442_6dp
        UserLeaseStatus.IN_PROGRESS -> R.drawable.feature_gold_lease_bg_rounded_yellow_43353b_6dp
        UserLeaseStatus.CANCELLED -> R.drawable.feature_gold_lease_bg_rounded_red_413046_6dp
        UserLeaseStatus.CLOSED -> R.drawable.feature_gold_lease_bg_rounded_blue_7898de_6dp
        UserLeaseStatus.COMPLETED -> R.drawable.feature_gold_lease_bg_rounded_green_273442_6dp
        UserLeaseStatus.FAILED -> R.drawable.feature_gold_lease_bg_rounded_red_413046_6dp
    }
}

fun UserLeaseStatus.getStatusText(): Int {
    return when (this) {
        UserLeaseStatus.ACTIVE -> R.string.feature_gold_lease_active
        UserLeaseStatus.IN_PROGRESS -> R.string.feature_gold_lease_in_progress
        UserLeaseStatus.CANCELLED -> R.string.feature_gold_lease_cancelled
        UserLeaseStatus.CLOSED -> R.string.feature_gold_lease_closed
        UserLeaseStatus.COMPLETED -> R.string.feature_gold_lease_completed
        UserLeaseStatus.FAILED -> R.string.feature_gold_lease_failed
    }
}