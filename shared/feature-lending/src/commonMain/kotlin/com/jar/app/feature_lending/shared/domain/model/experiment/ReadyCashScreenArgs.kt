package com.jar.app.feature_lending.shared.domain.model.experiment

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ReadyCashScreenArgs(
    @SerialName("source")
    val source:String,
    @SerialName("type")
    val type:String,
    @SerialName("screenName")
    val screenName:String,
    @SerialName("loanId")
    val loanId: String? = null,
    @SerialName("lender")
    val lender: String? = null,
    @SerialName("screenData")
    val screenData: ScreenData? = null,
    @SerialName("isRepeatWithdrawal")
    val isRepeatWithdrawal: Boolean = false,
    @SerialName("isRepayment")
    val isRepayment: Boolean = false,
) : Parcelable
