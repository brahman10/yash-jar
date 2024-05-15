package com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.success

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PostSetupSipData(
    val sipSubscriptionType: String,
    val isSetupFlow: Boolean,
    val subscriptionDay: String,
    val sipDayValue: Int,
    val nextDeductionDate: String?,
    val sipAmount: Float,
    val flowType: String? = null,
) : Parcelable