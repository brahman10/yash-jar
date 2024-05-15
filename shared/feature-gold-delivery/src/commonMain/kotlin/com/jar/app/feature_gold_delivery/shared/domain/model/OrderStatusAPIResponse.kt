package com.jar.app.feature_gold_delivery.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class OrderStatusAPIResponse(
    @SerialName("paymentState")
    val paymentState: GoldDeliveryTransactionState? = null,
    @SerialName("orderState")
    val orderState: GoldDeliveryTransactionState? = null,
) : Parcelable