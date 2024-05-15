package com.jar.app.feature_round_off.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class Transaction(
    @SerialName("amount")
    val amount: Float? = null,

    @SerialName("initialInvestment")
    val initialInvestment: Boolean? = null,

    @SerialName("investmentMode")
    val investmentMode: String? = null,

    @SerialName("investmentStatus")
    private val investmentStatus: String? = null,

    @SerialName("status")
    private val status: String? = null,

    @SerialName("roundOffAmount")
    val roundOffAmount: Double? = null,

    @SerialName("timestamp")
    val timestamp: String,

    @SerialName("title")
    val title: String,

    @SerialName("txnCategory")
    val txnCategory: String? = null,

    @SerialName("txnDate")
    val txnDate: String? = null,

    @SerialName("txnId")
    val txnId: String? = null,

    @SerialName("merchant")
    val merchant: String? = null,

    @SerialName("roundedOffTo")
    val roundedOffTo: Double? = null,

    @SerialName("categoryInfo")
    val categoryInfo: CategoryInfo? = null
) : Parcelable {

    fun getCustomStatus() = status ?: investmentStatus


}
