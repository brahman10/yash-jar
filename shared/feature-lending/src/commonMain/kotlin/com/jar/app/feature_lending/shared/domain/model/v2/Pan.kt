package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Pan(
    @SerialName("dob")
    val dob: String? = null,
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("lastName")
    val lastName: String? = null,
    @SerialName("otpAttempts")
    val otpAttempts: Int? = null,
    @SerialName("otpAttemptsDaysLimit")
    val otpAttemptsDaysLimit: Int? = null,
    @SerialName("otpVerificationAttempts")
    val otpVerificationAttempts: Int? = null,
    @SerialName("panDetailsFetchAttempts")
    val panDetailsFetchAttempts: Int? = null,
    @SerialName("panDetailsFetchDaysLimit")
    val panDetailsFetchDaysLimit: Int? = null,
    @SerialName("panNo")
    val panNo: String? = null,
    @SerialName("status")
    val status: String? = null
)