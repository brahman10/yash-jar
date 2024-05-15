package com.jar.feature_gold_price_alerts.shared.domain.model


import com.jar.app.core_base.util.orZero
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Timeframe(
    @SerialName("period")
    val period: Int? = null,
    @SerialName("unit")
    val unit: String? = null,
    @SerialName("selected")
    val isSelected: Boolean? = null
) {
    fun getTitle(): String {
        return "${period.orZero()}${unit?.getOrNull(0)?.toUpperCase()}"
    }
}