package com.jar.app.feature_calculator.shared.domain.model


import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SliderData(
    @SerialName("max")
    val max: Float,
    @SerialName("min")
    val min: Float,
    @SerialName("order")
    val order: Int,
    @SerialName("stepCount")
    val stepCount: Float,
    @SerialName("subType")
    val subType: String,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: String
) {
    fun getSliderType(): SliderType {
        return try {
            SliderType.valueOf(type)
        } catch (e: Exception) {
            SliderType.NONE
        }
    }

    fun getSliderSubType(): SliderSubType {
        return try {
            SliderSubType.valueOf(subType)
        } catch (e: Exception) {
            SliderSubType.NONE
        }
    }
}

enum class SliderType {
    AMOUNT,
    TENURE,
    PERCENTAGE,
    NONE
}

enum class SliderSubType(val affix: String) {
    AMOUNT("₹"),
    MONTH("Mo"),
    YEAR("Yr"),
    PERCENTAGE("%"),
    NONE("")
}