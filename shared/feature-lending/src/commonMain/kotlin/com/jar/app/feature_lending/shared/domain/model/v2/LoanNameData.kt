package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoanNameData(
    @SerialName("readyCashName")
    val readyCashName: String? = null,
    @SerialName("readyCashReason")
    val readyCashReason: String? = null,
    @SerialName("status")
    val status: String? = null,

)