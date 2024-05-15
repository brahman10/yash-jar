package com.jar.app.feature_lending.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LoanReasonChips(
    @SerialName("loanNameChips")
    var loanNameChips: List<ReasonData>
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class ReasonData(
    @SerialName("id")
    val id: Int,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("value")
    val title: String? = null,
    //For UI purpose
    @SerialName("isSelected")
    val isSelected: Boolean = false
) : Parcelable