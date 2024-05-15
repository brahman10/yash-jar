package com.jar.app.feature_daily_investment_cancellation.impl.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ProgressScreenData(
    @SerialName("heading")
    val heading: String,

    @SerialName("subHeading")
    val subHeading: String,

    @SerialName("highlightedText")
    val highlightedText: String,

    @SerialName("stopDailySaving")
    val stopDailySaving: Boolean,

    @SerialName("pauseDailySaving")
    val pauseDailySaving: Boolean,

    @SerialName("resumeDailySaving")
    val resumeDailySaving: Boolean,

    @SerialName("continueDailySaving")
    val continueDailySaving: Boolean,

    @SerialName("numberOfDays")
    val numberOfDays: String,

    @SerialName("version")
    val version: String,
) : Parcelable