package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RealTimeLeadStatus(
    @SerialName("success")
    val success:Boolean?=null,
    @SerialName("userId")
    val userId:String? = null,
    @SerialName("status")
    val status:String? =null
)
