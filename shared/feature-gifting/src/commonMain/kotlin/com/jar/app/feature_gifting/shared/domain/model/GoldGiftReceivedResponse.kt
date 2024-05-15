package com.jar.app.feature_gifting.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoldGiftReceivedResponse(
    @SerialName("receiverName")
    val receiverName: String?,

    @SerialName("senderName")
    val senderName: String,

    @SerialName("senderNumber")
    val senderPhoneNo: String? = null,

    @SerialName("receiverNumber")
    val receiverPhoneNo: String,

    @SerialName("message")
    val message: String?,

    @SerialName("volume")
    val volume: Float,

    @SerialName("giftingId")
    val giftingId: String,

    @SerialName("status")
    val status: String,

    @SerialName("amount")
    val amount: Float,

    @SerialName("isViewed")
    val isViewed: Boolean? = null,

    @SerialName("giftingDate")
    val giftingDate: Long,
) : Parcelable