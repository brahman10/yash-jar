package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Aadhaar(
    @SerialName("aadhaarNo")
    val aadhaarNo: String? = null,
    @SerialName("address")
    val address: String? = null,
    @SerialName("dob")
    val dob: String? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("status")
    val status: String? = null
)