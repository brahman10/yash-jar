package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class HomeStaticCardOrdering(
    @SerialName("couponCodeDiscoveryOrder")
    val couponCodeDiscoveryOrder:Int?,
    @SerialName("sipOrder")
    val sipOrder:Int?,
    @SerialName("updateDailySavingOrder")
    val updateDailySavingOrder:Int?,
    @SerialName("jarDuoOrder")
    val jarDuoOrder:Int?,
    @SerialName("vasooliOrder")
    val vasooliOrder:Int?,
    @SerialName("helpVideoOrder")
    val helpVideoOrder:Int?,
    @SerialName("quickActionsOrder")
    val quickActionOrder:Int?,
    @SerialName("goldLeaseOrder")
    val goldLeaseOrder:Int?,
    @SerialName("dailySavingSetupOrder")
    val dailySavingSetupOrder:Int?,
    @SerialName("preNotificationCardOrder")
    val preNotificationCardOrder:Int?
)
