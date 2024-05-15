package com.jar.app.feature_post_setup.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DSFailureInfo(
    @SerialName("autopaySubsId")
    val autopaySubsId: String? = null,
    @SerialName("bankName")
    val bankName: String? = null,
    @SerialName("bankLogo")
    val bankLogo: String? = null,
    @SerialName("provider")
    val provider: String? = null,
    @SerialName("isAutopaySetup")
    val isAutoPaySetup: Boolean? = null,
    @SerialName("mandateAmount")
    val mandateAmount: Float? = null,
    @SerialName("upiId")
    val upiId: String? = null,
    @SerialName("recurringFrequency")
    val recurringFrequency: String? = null,
    @SerialName("noOfDays")
    val noOfDays: Int? = null
)
