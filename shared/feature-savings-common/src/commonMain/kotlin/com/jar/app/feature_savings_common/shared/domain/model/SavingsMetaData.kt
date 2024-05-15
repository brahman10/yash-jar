package com.jar.app.feature_savings_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class SavingsMetaData(
    @SerialName("showPostSetupData")
    val showPostSetupData: Boolean? = null,
    @SerialName("mandateText")
    val mandateText: String? = null,
    @SerialName("dailySavingsType")
    val dailySavingsType: String? = null,
) : Parcelable