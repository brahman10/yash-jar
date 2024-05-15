package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserGoldSipDetailsDTO(
    @SerialName("bankLogo")
    val bankLogo: String? = null,
    @SerialName("bankName")
    val bankName: String? = null,
    @SerialName("enabled")
    val enabled: Boolean,
    @SerialName("nextDeductionDate")
    val nextDeductionDate: Long? = null,
    @SerialName("pauseStatus")
    val pauseStatus: PauseStatusDTO? = null,
    @SerialName("provider")
    val provider: String? = null,
    @SerialName("subsState")
    val subsState: String? = null,
    @SerialName("subscriptionStatus")
    val subscriptionStatus: String? = null,
    @SerialName("subscriptionAmount")
    val subscriptionAmount: Float,
    @SerialName("subscriptionDay")
    val subscriptionDay: Int,
    @SerialName("subscriptionId")
    val subscriptionId: String? = null,
    @SerialName("subscriptionType")
    val subscriptionType: String? = null,
    @SerialName("updateDate")
    val updateDate: Long? = null,
    @SerialName("upiId")
    val upiId: String? = null,
    @SerialName("manualPayment")
    val manualPaymentDetails: FullPaymentInfoDTO? = null,
    @SerialName("mandateAmount")
    val mandateAmount: Float? = null,
    @SerialName("order")
    val order: Int? = null
)
