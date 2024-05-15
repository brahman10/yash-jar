package com.jar.app.feature_vasooli.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VasooliOverview(
    @SerialName("totalLent")
    val totalLent: Int? = null,

    @SerialName("totalDue")
    val totalDue: Int? = null
)