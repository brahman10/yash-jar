package com.jar.app.feature_vasooli.impl.util

import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.parcelize.Parcelize

@Parcelize
class VasooliEndDateValidator(
    private val endDate: Long
): CalendarConstraints.DateValidator,
    Parcelable {
    override fun isValid(date: Long): Boolean {
        return date <= endDate
    }
}