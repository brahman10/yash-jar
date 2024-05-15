package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FirstCoinProgressData (

    @SerialName("header")
    val header : String,

    @SerialName("percentageCompleted")
    val percentageCompleted : Double,

    @SerialName("currentBalance")
    val currentBalance : Double,

    @SerialName("target")
    val target : Double,

    @SerialName("autopayText")
    val autopayText : String?,

    @SerialName("amount")
    val amount : Double?,

    @SerialName("ctaText")
    val ctaText : String,

    @SerialName("ctaDeepLink")
    val ctaDeepLink : String,

    @SerialName("bottomText")
    val bottomText : String,

    )