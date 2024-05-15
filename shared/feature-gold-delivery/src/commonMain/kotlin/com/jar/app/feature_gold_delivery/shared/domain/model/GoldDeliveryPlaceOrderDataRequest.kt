package com.jar.app.feature_gold_delivery.shared.domain.model

import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoldDeliveryPlaceOrderDataRequest(
    @SerialName("addressId")
    val addressId: String,
    @SerialName("useJarSavings")
    val useJarSavings: Boolean,
    @SerialName("paymentProvider")
    val paymentProvider: String,
    @SerialName("priceResponse")
    val priceResponse: FetchCurrentGoldPriceResponse? = null,
) : Parcelable