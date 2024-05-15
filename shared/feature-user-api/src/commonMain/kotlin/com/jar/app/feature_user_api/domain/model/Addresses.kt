package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Addresses(
    @SerialName("addresses")
    val addresses: List<Address>
)