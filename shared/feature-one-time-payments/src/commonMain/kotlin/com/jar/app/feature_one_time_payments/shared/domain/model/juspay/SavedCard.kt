package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import com.jar.app.core_base.util.BaseConstants
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class SavedCard(

    @SerialName("nameOnCard")
    val nameOnCard: String,

    @SerialName("expired")
    val expired: Boolean,

    @SerialName("cardType")
    val cardType: String,

    @SerialName("cardNumber")
    val cardNumber: String,

    @SerialName("cardIssuer")
    val cardIssuer: String,

    @SerialName("cardFingerprint")
    val cardFingerprint: String,

    @SerialName("cardToken")
    val cardToken: String,

    @SerialName("cardExpYear")
    val cardExpYear: String,

    @SerialName("cardExpMonth")
    val cardExpMonth: String,

    @SerialName("cardBrand")
    val cardBrand: String

) : Parcelable {

    fun getFormattedExpiryDate() = "$cardExpMonth/${cardExpYear.toInt() % 100}"

    fun getFormattedCardNumber(): String {
        return cardNumber
            .replace("-", " ")
            .replace("XXXXXXXX", "**** ****")
    }

    fun getCardBrandImageUrl(): String {
        return "${BaseConstants.CDN_BASE_URL}/CardTypes/${cardBrand}.png"
    }


}