package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrandCatalogoueApiData(
    @SerialName("description")
    val description: String? = null,
    @SerialName("discountHeader")
    val discountHeader: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("myVouchersDeepLink")
    val myVouchersDeepLink: String? = null,
    @SerialName("myOrdersText")
    val myVouchersText: String? = null
)