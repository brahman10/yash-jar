package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.error_screen

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class PanErrorStatesArguments(
    val heading: String,
    val title: String,
    val subTitle: String,
    @DrawableRes val imageId: Int,
    val isInvalidPan: Boolean = false,
    val haveTechnicalError: Boolean = false
) : Parcelable