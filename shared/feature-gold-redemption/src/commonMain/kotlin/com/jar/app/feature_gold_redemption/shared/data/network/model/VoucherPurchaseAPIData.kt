package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import com.jar.app.feature_one_time_payments_common.shared.PaymentOrderDetails
import com.jar.app.feature_gold_redemption.shared.domain.model.CardType
import kotlinx.serialization.Serializable

@Serializable
data class VoucherPurchaseAPIData(
    @SerialName("amountList")
    val amountList: List<Float?>? = null,
    @SerialName("discountText")
    val discountText: String? = null,
    @SerialName("voucherHeaderString")
    val voucherHeaderString: String? = null,
    @SerialName("howToRedeem")
    val howToRedeem: List<String?>? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("paymentOrderDetails")
    val paymentOrderDetails: PaymentOrderDetails? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("inStoreRedemptionText")
    val inStoreRedemptionText: String? = null,
    @SerialName("onlineRedemptionText")
    val onlineRedemptionText: String? = null,
    @SerialName("peakQuantity")
    val peakQuantity: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("brandTitle")
    val brandTitle: String? = null,
    @SerialName("discountPercentage")
    val discountPercentage: Float? = null,
    @SerialName("tnc")
    val tnc: List<String?>? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("voucherStaticContentList")
    val voucherStaticContentList: List<VoucherStaticContent?>? = null
) {
    fun getCardType(): CardType {
        return CardType.valueOf(type ?: "GOLD")
    }
}