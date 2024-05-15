package com.jar.app.core_base.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class InfoDialogResponse(
    @SerialName("helpWorkFlow")
    val helpWorkFlow: InfoDialogData,
    @SerialName("goldPurchaseCount")
    val goldPurchaseCount: Int? = null
)