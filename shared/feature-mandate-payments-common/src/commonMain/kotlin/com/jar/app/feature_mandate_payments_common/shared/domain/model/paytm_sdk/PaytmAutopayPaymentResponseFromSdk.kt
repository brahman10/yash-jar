package com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_sdk

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class PaytmAutopayPaymentResponseFromSdk(
    @SerialName("BANKNAME")
    val BANKNAME: String? = null,

    @SerialName("BANKTXNID")
    val BANKTXNID: String? = null,

    @SerialName("CHECKSUMHASH")
    val CHECKSUMHASH: String? = null,

    @SerialName("CURRENCY")
    val CURRENCY: String? = null,

    @SerialName("GATEWAYNAME")
    val GATEWAYNAME: String? = null,

    @SerialName("MID")
    val MID: String? = null,

    @SerialName("ORDERID")
    val ORDERID: String? = null,

    @SerialName("PAYMENTMODE")
    val PAYMENTMODE: String? = null,

    @SerialName("RESPCODE")
    val RESPCODE: String? = null,

    @SerialName("RESPMSG")
    val RESPMSG: String? = null,

    @SerialName("STATUS")
    val STATUS: String? = null,

    @SerialName("CHARGEAMOUNT")
    val CHARGEAMOUNT: String? = null,

    @SerialName("TXNAMOUNT")
    val TXNAMOUNT: String? = null,

    @SerialName("TXNDATE")
    val TXNDATE: String? = null,

    @SerialName("TXNID")
    val TXNID: String? = null,

    @SerialName("SUBS_ID")
    val SUBS_ID: String? = null,
): Parcelable {
    fun toPaytmPaymentResultData(): PaytmAutoPayPaymentResultData {
        return PaytmAutoPayPaymentResultData(
            bankName = BANKNAME,
            bankTxnId = BANKTXNID,
            checksumHash = CHECKSUMHASH,
            currency = CURRENCY,
            gatewayName = GATEWAYNAME,
            mid = MID,
            orderId = ORDERID,
            paymentMode = PAYMENTMODE,
            respCode = RESPCODE,
            respMsg = RESPMSG,
            status = STATUS,
            chargeAmount = CHARGEAMOUNT,
            txnAmount = TXNAMOUNT,
            txnDate = TXNDATE,
            txnId = TXNID,
            subsId = SUBS_ID
        )
    }
}