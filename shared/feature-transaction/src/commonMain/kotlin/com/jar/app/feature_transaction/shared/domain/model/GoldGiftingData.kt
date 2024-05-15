package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldGiftingData(
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("goldVolume")
    val goldVolume: Float? = null,
    @SerialName("receiverMsg")
    val receiverMsg: String? = null,
    @SerialName("receiverName")
    val receiverName: String? = null,
    @SerialName("receiverPhoneNo")
    val receiverPhoneNo: String? = null,
    @SerialName("senderName")
    val senderName: String? = null,
    @SerialName("senderPhoneNo")
    val senderPhoneNo: String? = null,
    @SerialName("received")
    val isReceived: Boolean? = null,

    override val uniqueKey: String = receiverPhoneNo?.plus(senderPhoneNo)?.plus(amount)?.plus(goldVolume).orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.GOLD_GIFTING
    }

    override fun equals(other: Any?): Boolean {
        return other is GoldGiftingData
    }
}