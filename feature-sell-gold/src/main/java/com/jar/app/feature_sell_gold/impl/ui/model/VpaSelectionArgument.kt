package com.jar.app.feature_sell_gold.impl.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class VpaSelectionArgument(
    val isRetryFlow: Boolean = false,
    val withdrawalPrice: String,
    val orderId: String? = null
)