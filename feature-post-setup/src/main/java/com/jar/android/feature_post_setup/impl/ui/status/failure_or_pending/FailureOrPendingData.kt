package com.jar.android.feature_post_setup.impl.ui.status.failure_or_pending

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FailureOrPendingData(
    val title: String?,
    val description: String?,
    val isPendingFlow: Boolean,
    val transactionId: String?,
    val amount: Float,
    val roundOffIds: List<String>
) : Parcelable