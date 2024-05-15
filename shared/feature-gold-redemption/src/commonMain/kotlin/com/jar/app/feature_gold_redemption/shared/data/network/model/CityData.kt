package com.jar.app.feature_gold_redemption.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityData(
    @SerialName("storesCount")
    val storesCount: Int? = 4,
    @SerialName("cityName")
    val cityName: String? = null
)