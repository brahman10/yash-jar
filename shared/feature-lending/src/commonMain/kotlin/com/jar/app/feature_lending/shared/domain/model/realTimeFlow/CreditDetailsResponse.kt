package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditDetailsResponse(
    @SerialName("phoneNo")
 val phoneNo:String,
    @SerialName("panNumber")
 val panNumber:String? = null,
    @SerialName("experianConsentRequired")
 val experianConsentRequired:Boolean = false,
    @SerialName("panEditable")
 val panEditable:Boolean = false
)
