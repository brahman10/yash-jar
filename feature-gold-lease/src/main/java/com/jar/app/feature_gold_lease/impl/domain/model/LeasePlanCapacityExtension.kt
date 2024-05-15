package com.jar.app.feature_gold_lease.impl.domain.model

import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanCapacity

fun LeasePlanCapacity.getStatusColor(): Int {
    return when (this) {
        LeasePlanCapacity.NEW_LAUNCH -> com.jar.app.core_ui.R.color.color_789BDE
        LeasePlanCapacity.FILLING_FAST -> com.jar.app.core_ui.R.color.color_58DDC8
        LeasePlanCapacity.ALMOST_FULL -> com.jar.app.core_ui.R.color.color_EBB46A
        LeasePlanCapacity.CLOSED -> com.jar.app.core_ui.R.color.color_D5CDF2
        LeasePlanCapacity.NO_TAG -> com.jar.app.core_ui.R.color.color_D5CDF2
    }
}