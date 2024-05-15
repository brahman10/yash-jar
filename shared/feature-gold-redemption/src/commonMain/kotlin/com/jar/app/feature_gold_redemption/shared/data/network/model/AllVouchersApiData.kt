package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllVouchersApiData(
    @SerialName("header")
    val header: String? = null,
    @SerialName("myOrdersTxt")
    val myOrdersTxt: String? = null,
    @SerialName("productFilter")
    val productFilter: List<ProductFilter?>? = null,
    @SerialName("productsList")
    val voucherProductsList: List<VoucherProducts?>? = null
)