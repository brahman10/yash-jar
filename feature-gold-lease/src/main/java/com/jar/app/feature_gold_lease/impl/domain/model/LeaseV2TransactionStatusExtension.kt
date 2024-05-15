package com.jar.app.feature_gold_lease.impl.domain.model

import com.jar.app.feature_gold_lease.shared.domain.model.LeaseV2TransactionStatus

fun LeaseV2TransactionStatus.getStatusIcon(): Int {
    return when (this) {
        LeaseV2TransactionStatus.SUCCESS -> com.jar.app.core_ui.R.drawable.core_ui_ic_tick
        LeaseV2TransactionStatus.FAILURE -> com.jar.app.core_ui.R.drawable.core_ui_ic_red_cross
        LeaseV2TransactionStatus.PENDING -> com.jar.app.core_ui.R.drawable.core_ui_ic_hour_glass_bg_yellow
    }
}