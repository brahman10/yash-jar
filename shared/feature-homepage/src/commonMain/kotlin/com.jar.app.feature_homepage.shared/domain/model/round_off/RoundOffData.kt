package com.jar.app.feature_homepage.shared.domain.model.round_off

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RoundOffData(
    @SerialName("noOfDays")
    val noOfDays: Int,

    @SerialName("startTime")
    val startTime: Long,

    @SerialName("endTime")
    val endTime: Long,

    @SerialName("txnAmt")
    val txnAmt: Float,

    @SerialName("orderId")
    val orderId: String

)