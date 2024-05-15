package com.jar.app.feature.home.domain.model

import kotlinx.serialization.SerialName

data class IsKycRequiredData(

    @SerialName("kycRequired")
    val kycRequired: Boolean
)
