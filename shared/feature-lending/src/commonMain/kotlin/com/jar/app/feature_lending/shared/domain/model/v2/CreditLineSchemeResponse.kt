package com.jar.app.feature_lending.shared.domain.model.v2

import com.jar.app.feature_lending.shared.domain.model.temp.CreditLineScheme
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreditLineSchemeResponse(
    @SerialName("amount")
    val amount: Float,

    @SerialName("roi")
    val roi: Float,

    @SerialName("emiCards")
    val emiCards: List<CreditLineScheme>,

    @SerialName("consentString")
    val consentString: String? = null
)