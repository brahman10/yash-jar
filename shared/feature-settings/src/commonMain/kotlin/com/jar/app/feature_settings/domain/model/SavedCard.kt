package com.jar.app.feature_settings.domain.model

import com.jar.app.core_base.util.BaseConstants
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class SavedCard(
    @SerialName("cardNo")
    val cardNumber: String,
    @SerialName("cardToken")
    val cardToken: String? = null,
    @SerialName("nameOnCard")
    val nameOnCard: String,
    @SerialName("expYear")
    val cardExpYear: String? = null,
    @SerialName("expMonth")
    val cardExpMonth: String? = null,
    @SerialName("cardType")
    val cardType: String? = null,
    @SerialName("cardIssuer")
    val cardIssuer: String,
    @SerialName("cardBrand")
    val cardBrand: String
) : Parcelable {



    fun getFormattedCardNumber(): String {
        return cardNumber
            .replace("-", " ")
            .replace("XXXXXXXX", "**** ****")
    }

    fun getCardBrandImageUrl(): String {
        return "${BaseConstants.CDN_BASE_URL}/CardTypes/${cardBrand}.png"
    }


}