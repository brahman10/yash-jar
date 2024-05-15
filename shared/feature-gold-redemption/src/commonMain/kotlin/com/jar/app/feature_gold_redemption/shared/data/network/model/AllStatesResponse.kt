package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllCitiesResponse(
    @SerialName("cityList")
    val states: List<CityData?>? = null
)