package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class SendGiftGoldResponse(
    @SerialName("amountToBePaid")
    val amountToBePaid: Float? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: List<String>? = null,

    @SerialName("cta")
    val cta: String? = null,

    @SerialName("giftingId")
    val giftingId: String? = null,

    @SerialName("balanceAvailable")
    val balanceAvailable: Boolean,

    @SerialName("receiverDetails")
    val receiverDetails: GoldReceiverDetails? = null,

    @SerialName("giftingStatus")
    private val giftingStatus: String? = null

) : Parcelable {
    fun getGiftingStatus(): GiftingStatus {
        return GiftingStatus.values().find { giftingStatus == it.name } ?: GiftingStatus.PENDING
    }
}

enum class GiftingStatus {
    SENT, PENDING, FAILURE
}

@Parcelize
@kotlinx.serialization.Serializable
data class GoldReceiverDetails(
    @SerialName("receiverName")
    val receiverName: String? = null,

    @SerialName("receiverNumber")
    val receiverNumber: String,

    @SerialName("receiverJarUser")
    val receiverJarUser: Boolean,

    @SerialName("volume")
    val volume: Float? = null,

    @SerialName("amount")
    val amount: Float? = null
) : Parcelable