package com.jar.app.base.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FeatureFlowData(
    val fromScreen: String,
    val fromSection: String? = null,
    val fromCard: String? = null
) : Parcelable