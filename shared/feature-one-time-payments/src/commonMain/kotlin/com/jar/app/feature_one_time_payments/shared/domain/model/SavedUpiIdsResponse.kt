package com.jar.app.feature_one_time_payments.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SavedUpiIdsResponse(
    @SerialName("vpaAddresses")
    val vpaAddresses: List<String?>?
)