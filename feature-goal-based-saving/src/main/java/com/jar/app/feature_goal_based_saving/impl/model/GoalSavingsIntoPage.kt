package com.jar.app.feature_goal_based_saving.impl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
data class GoalSavingsIntoPage(
    @SerialName("param")
    val param: Int,
    @SerialName("contentType")
    val contentType: String,
    @SerialName("goalSavingsIntroPageResponse")
    val goalSavingsIntroPageResponse: GoalSavingsIntroPageResponse
): Parcelable

@Parcelize
data class GoalSavingsIntroPageResponse(
    @SerialName("header1")
    val header1: String,
    @SerialName("header2")
    val header2: String,
    @SerialName("displayUrl")
    val displayUrl: String,
    @SerialName("footerButtonText")
    val footerButtonText: String
): Parcelable
