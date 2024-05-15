package com.jar.app.feature_promo_code.shared.data.models


import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PromoCodeTransactionResponse(
    @SerialName("amount")
    val amount: Double? = null ,
    @SerialName("invoiceLink")
    val invoiceLink: String? = null,
    @SerialName("transactionStatus")
    val transactionStatus: String? = null ,
    @SerialName("volume")
    val volume: Double? = null
):Parcelable{
    fun getTransactionStatus(): PromoCodeTransactionStatus {
        return transactionStatus?.let {
            PromoCodeTransactionStatus.valueOf(it)
        } ?: run {
            PromoCodeTransactionStatus.PENDING
        }
    }
}