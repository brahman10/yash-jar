package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RealTimeBankDetailSteps(
    @SerialName("footerText")
    val footerText:String,
    @SerialName("steps")
    val steps:List<RealTimeStep>
)

@Serializable
data class RealTimeStep(
    @SerialName("imageUrl")
    val imageUrl:String,
    @SerialName("title")
    val title:String
)