package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CkycDetail(
    @SerialName("name")
    val name:String? = null,
    @SerialName("dob")
    val dob:String? = null,
    @SerialName("address")
    val address:String? = null,
)
