package com.jar.app.feature_lending.shared.domain.model.temp

import com.jar.app.feature_user_api.domain.model.Address
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LendingAddress(
    @SerialName("applicationId")
    val applicationId: String? = null,

    @SerialName("address")
    val address: Address
)