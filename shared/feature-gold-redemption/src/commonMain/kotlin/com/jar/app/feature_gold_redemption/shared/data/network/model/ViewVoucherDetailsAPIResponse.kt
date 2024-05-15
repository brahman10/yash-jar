package com.jar.app.feature_gold_redemption.shared.data.network.model

import kotlinx.serialization.SerialName
import com.jar.app.feature_one_time_payments_common.shared.PaymentOrderDetails
import com.jar.app.feature_gold_redemption.shared.domain.model.CardStatus
import com.jar.app.feature_gold_redemption.shared.domain.model.CardType
import kotlinx.serialization.Serializable

@Serializable
data class ViewVoucherDetailsAPIResponse(
    @SerialName("voucherId")
    val voucherId: String? = null,
    @SerialName("amount")
    val amount: Int? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
    @SerialName("activationPin")
    val pin: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("voucherName")
    val voucherName: String? = null,
    @SerialName("brandTitle")
    val brandTitle: String? = null,
    @SerialName("validTillText")
    val validTillText: String? = null,
    @SerialName("bottomDrawerObjectType")
    val bottomDrawerObjectType: String? = null,
    @SerialName("expiredAtText")
    val expiredAtText: String? = null,
    @SerialName("voucherProcessingText")
    val voucherProcessingText: String? = null,
    @SerialName("goldBonusText")
    val goldBonusText: String? = null,
    @SerialName("productType")
    val productType: String? = null,
    @SerialName("myVouchersType")
    val myVouchersType: String? = null,
    @SerialName("aboutJewellerText")
    val aboutJewellerText: String? = null,
    @SerialName("onlineRedemptionText")
    val onlineRedemptionText: String? = null,
    @SerialName("onlineRedemptionTextAns")
    val onlineRedemptionTextAns: String? = null,
    @SerialName("offlineStoreListText")
    val offlineStoreListText: String? = null,
    @SerialName("offlineStoreListTextAns")
    val offlineStoreListTextAns: String? = null,
    @SerialName("paymentOrderDetails")
    val paymentOrderDetails: PaymentOrderDetails? = null,
    @SerialName("howToRedeemList")
    val howToRedeemList: List<String>? = null,
    @SerialName("tncList")
    val tncList: List<String>? = null,
    @SerialName("refundDetails")
    val refundDetails: RefundDetails? = null,
) {
    fun getVoucherStatusEnum(): CardStatus {
        return CardStatus.valueOf(myVouchersType ?: "EXPIRED")
    }
    fun getCardType(): CardType {
        return CardType.valueOf(productType?.uppercase() ?: "GOLD")
    }
    fun convertToUserVoucher(): UserVoucher {
        return UserVoucher(
            amount = amount,
//            calendarUrl =  calendarUrl,
            code = code,
            imageUrl = imageUrl,
            validTillText = validTillText,
            voucherId = voucherId,
            voucherName = voucherName,
            voucherProcessingText = voucherProcessingText,
            calendarUrl = null,
            noOfDaysToRedeem = null,
            viewDetails = null,
            voucherExpiredText = null,
            myVouchersType = CardStatus.EXPIRED.name,
            currentState = getSafeTODO(productType),
            creationDate = "",
            quantity = quantity
        )
    }

    private fun getSafeTODO(productType: String?): String? {
        return when (productType) {
            "DIAMOND" -> productType
            "GOLD" -> productType
            else -> {
                "GOLD"
            }
        }
    }
}