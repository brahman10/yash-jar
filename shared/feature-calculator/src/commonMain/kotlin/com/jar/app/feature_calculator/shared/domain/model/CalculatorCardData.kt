package com.jar.app.feature_calculator.shared.domain.model


import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CalculatorCardData(
    @SerialName("backgroundColor")
    val backgroundColor: String,
    @SerialName("buttonBackgroundColor")
    val buttonBackgroundColor: String,
    @SerialName("buttonText")
    val buttonText: String,
    @SerialName("buttonTextColor")
    val buttonTextColor: String,
    @SerialName("deepLink")
    val deepLink: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("primaryTextColor")
    val primaryTextColor: String,
    @SerialName("secondaryTextColor")
    val secondaryTextColor: String,
    @SerialName("title1")
    val title1: String,
    @SerialName("title2")
    val title2: String,
    @SerialName("title3")
    val title3: String,
    @SerialName("title4")
    val title4: String,
)