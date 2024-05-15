package com.jar.app.feature_lending.shared.domain.model.experiment

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.*

@kotlinx.serialization.Serializable
data class LendingJourneyResponse(
    @SerialName("data")
    val data: ReadyCashJourney
)
@kotlinx.serialization.Serializable
data class ReadyCashJourney(
    @SerialName("applicationId")
    val applicationId: String? = null,
    @SerialName("repeatLoanEnabled")
    val repeatLoanEnabled: Boolean = false,
    @SerialName("type")
    val type: String? = null,
    @SerialName("appUnderMaintenance")
    val appUnderMaintenance: Boolean = false,
    @SerialName("currentScreen")
    val currentScreen: String? = null,
    @SerialName("minSalary")
    val minSalary: Int? = null,
    @SerialName("lender")
    val lender: String? = null,
    @SerialName("screens")
    val screens: List<String>? = null,
    @SerialName("screenData")
    val screenData: Map<String, ScreenData>? = null,
    @SerialName("progressBar")
    val progressBar: List<ReadyCashProgressBar>? = null
)

@Parcelize
@kotlinx.serialization.Serializable
data class ScreenData(
    @SerialName("shouldShowProgress")
    val shouldShowProgress: Boolean = false,
    @SerialName("backScreen")
    val backScreen: String,
    @SerialName("nextScreen")
    val nextScreen: String,
    @SerialName("status")
    val status: String,
    @SerialName("skipForward")
    val skipForward: Boolean = false
) : Parcelable

@kotlinx.serialization.Serializable
data class ReadyCashProgressBar(
    @SerialName("step")
    val step: String,
    @SerialName("stepName")
    val stepName: String,
    @SerialName("status")
    val status: String,
    @SerialName("screens")
    val screens: List<String>
)