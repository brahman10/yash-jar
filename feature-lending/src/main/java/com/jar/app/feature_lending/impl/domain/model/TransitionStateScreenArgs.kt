package com.jar.app.feature_lending.impl.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
@Parcelize
data class TransitionStateScreenArgs(
    @SerialName("transitionType")
    val transitionType: String,
    @SerialName("flowType")
    val flowType: String,
    @SerialName("loanId")
    val loanId: String,
    @SerialName("destinationDeeplink")
    val destinationDeeplink: String? = null,
    @SerialName("lender")
    val lender: String? = null,
    @SerialName("isFromRepeatWithdrawal")
    val isFromRepeatWithdrawal: Boolean = false
) : Parcelable
