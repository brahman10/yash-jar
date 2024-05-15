package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchExperianReportRequest(
    @SerialName("name")
    val name:String,
    @SerialName("phoneNumber")
    val phoneNumber:String,
    @SerialName("panNo")
    val panNo:String?=null
)
