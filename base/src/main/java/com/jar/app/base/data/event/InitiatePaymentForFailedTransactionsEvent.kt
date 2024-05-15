package com.jar.app.base.data.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InitiatePaymentForFailedTransactionsEvent(
    val amount: Float,
    val roundOffIds: List<String>
):Parcelable
