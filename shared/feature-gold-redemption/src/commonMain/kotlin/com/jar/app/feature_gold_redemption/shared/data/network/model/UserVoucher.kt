package com.jar.app.feature_gold_redemption.shared.data.network.model

import com.jar.app.feature_gold_redemption.shared.domain.model.CardStatus
import com.jar.app.feature_gold_redemption.shared.domain.model.CardType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserVoucher(
    @SerialName("amount")
    val amount: Int? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("calendarUrl")
    val calendarUrl: String? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("noOfDaysLeft")
    val noOfDaysToRedeem: Int? = null,
    @SerialName("validTillText")
    val validTillText: String? = null,
    @SerialName("creationDateString")
    val creationDate: String?=null,
    @SerialName("viewDetails")
    val viewDetails: String? = null,
    @SerialName("refundInitText")
    val refundInitText: String? = null,
    @SerialName("myVouchersType")
    val myVouchersType: String? = null,
    @SerialName("type")
    val currentState: String? = null,
    @SerialName("voucherExpiredText")
    val voucherExpiredText: String? = null,
    @SerialName("voucherId")
    val voucherId: String? = null,
    @SerialName("voucherName")
    val voucherName: String? = "Hello testing",
    @SerialName("voucherProcessingText")
    val voucherProcessingText: String? = null
) {
    fun getStatusEnum(): CardStatus {
        return CardStatus.valueOf(myVouchersType ?: "EXPIRED")
    }
    fun getCardTypeEnum(): CardType {
        if (currentState == "DIAMOND voucher") {
            return CardType.DIAMOND
        }
        if (currentState == "GOLD voucher") {
            return CardType.GOLD
        }
        return CardType.valueOf(currentState ?: "NONE")
    }
}