package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StateData(
    @SerialName("storesCount")
    val count: Int? = null,
    @SerialName("stateName")
    val name: String? = null
)