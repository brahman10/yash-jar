package com.jar.app.feature_transaction.impl.domain.model

import com.jar.app.base.util.epochToDate
import com.jar.app.base.util.getFormattedDate
import com.jar.app.feature_transaction.shared.domain.model.FilterValueData

fun getDateString(it: FilterValueData): String? {
    if (it.startDate == null || it.endDate == null)
        return null
    return "${it.startDate!!.epochToDate().getFormattedDate("MMM dd")} - ${
        it.endDate!!.epochToDate().getFormattedDate("MMM dd")
    }"
}