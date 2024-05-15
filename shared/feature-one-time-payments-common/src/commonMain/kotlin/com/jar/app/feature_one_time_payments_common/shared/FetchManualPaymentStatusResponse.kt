package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class FetchManualPaymentStatusResponse(

    @SerialName("transactionId")
    val transactionId: String? = null,

    @SerialName("paymentProvider")
    val paymentProvider: String? = null,

    @SerialName("amount")
    val amount: Float? = null,

    @SerialName("header")
    val header: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("info")
    val info: String? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("txnStatus")
    private val txnStatus: String? = null,

    @SerialName("goldTxnStatus")
    private val goldTxnStatus: String? = null,

    @SerialName("oneTimeInvestOrderDetails")
    val oneTimeInvestOrderDetails: OneTimeInvestOrderDetails? = null,

    @SerialName("sendGiftResponse")
    val sendGiftResponse: SendGiftGoldResponse? = null,

    @SerialName("goldDeliveryResponse")
    val goldDeliveryResponse: DeliverProductResponse? = null,

    @SerialName("shareImageUrl")
    val shareImageUrl: String? = null,

    @SerialName("createVoucherOrderResponse")
    val createVoucherOrderResponse: CreateVoucherOrderResponse? = null,

    @SerialName("shareText")
    val shareText: String? = null,

    @SerialName("oneTimeInvestment")
    val oneTimeInvestment: Boolean? = null,

    @SerialName("transactionDate")
    val transactionDate: Long? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("weeklyChallengeResponse")
    val weeklyChallengeResponse: WeeklyChallengeResponse?,

    @SerialName("postPaymentRewardCardList")
    val postPaymentRewardCardList: List<PostPaymentRewardCard>? = null,

    @SerialName("paymentMethod")
    val paymentMethod: String? = null,

    @SerialName("paymentDate")
    val paymentDate: String? = null,

    @SerialName("payerVpa")
    val payerVpa: String? = null,

    @SerialName("leaseId")
    val leaseId: String? = null,

    @SerialName("showInAppRating")
    val showInAppRating: Boolean? = null,

    @SerialName("postOrderCrossSellCard")
    val postOrderCrossSellCard: PostOrderCrossSellCardData? = null
) : Parcelable {
    fun getManualPaymentStatus(): ManualPaymentStatus {
        return txnStatus?.let {
            ManualPaymentStatus.valueOf(it)
        } ?: run {
            ManualPaymentStatus.FAILURE
        }
    }

    fun getManualPaymentGoldStatus(): ManualPaymentStatus? {
        return goldTxnStatus?.let { ManualPaymentStatus.valueOf(it) }
    }
}