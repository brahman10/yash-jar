package com.jar.app.feature_gifting.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class SendGiftGoldRequest(
    @SerialName("receiverName")
    var receiverName: String? = null,

    @SerialName("receiverPhoneNo")
    var receiverPhoneNo: String? = null,

    @SerialName("volume")
    var volume: Float? = null,

    @SerialName("amount")
    var amount: Float? = null,

    @SerialName("messageForReceiver")
    var messageForReceiver: String? = null,

    @SerialName("buyGoldRequestType")
    var buyGoldRequestType: String? = null
) : Parcelable