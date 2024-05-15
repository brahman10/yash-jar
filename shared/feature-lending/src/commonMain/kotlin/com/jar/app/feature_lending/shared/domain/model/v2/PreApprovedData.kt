package com.jar.app.feature_lending.shared.domain.model.v2

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PreApprovedData(
    @SerialName("availableLimit")
    val availableLimit: Int? = null,
    @SerialName("creditScore")
    val creditScore: Int? = null,
    @SerialName("emailId")
    val emailId: String? = null,
    @SerialName("preApproved")
    val isPreApproved: Boolean? = null,
    @SerialName("lender")
    val lender: String? = null,
    @SerialName("lenderName")
    val lenderName: String? = null,
    @SerialName("lenderLogoUrl")
    val lenderLogoUrl: String? = null,
    @SerialName("creditProviderLogoUrl")
    val creditProviderLogoUrl: String? = null,
    @SerialName("maxDrawdown")
    val maxDrawDown: Int? = null,
    @SerialName("minDrawdown")
    val minDrawDown: Int? = null,
    @SerialName("npciLogoUrl")
    val npciLogoUrl: String? = null,
    @SerialName("offerAmount")
    val offerAmount: Int? = null,
    @SerialName("recommendedDrawDown")
    val recommendedDrawDown: Int? = null,
    @SerialName("trustCount")
    val trustCount: String? = null,
    @SerialName("blockLoanButton")
    val blockLoanButton: Boolean? = null,
    @SerialName("repeatLoanEnabled")
    val repeatLoanEnabled: Boolean? = null,
    @SerialName("limitMessage")
    val limitMessage: String? = null
): Parcelable