package com.jar.app.feature_spends_tracker.shared.domain.model.spends_transaction_data


import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class SpendsTransactionData(
    @SerialName("amount")
    val amount: String,
    @SerialName("beneDetails")
    val beneDetails: String? = null,
    @SerialName("header")
    val header: String,
    @SerialName("paidToText")
    val paidToText: String? = null,
    @SerialName("reportFlagIcon")
    val reportFlagIcon: String,
    @SerialName("spendsIcon")
    val spendsIcon: String,
    @SerialName("txnDate")
    val txnDate: String,
    @SerialName("txnId")
    val txnId: String,
    @SerialName("txnTime")
    val txnTime: String
) : Parcelable