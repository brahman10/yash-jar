package com.jar.app.feature_gold_delivery.shared.domain.model

import kotlinx.serialization.SerialName
import com.jar.app.core_base.util.orZero
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@kotlinx.serialization.Serializable
data class ProductsV2(
    @SerialName("products")
    val products: List<ProductV2>? = null
)

@Parcelize
@kotlinx.serialization.Serializable
data class ProductV2(
    @SerialName("availableVolumes")
    val availableVolumes: List<AvailableVolumeV2?>? = null,
    @SerialName("label")
    val label: String? = null,
    @SerialName("alertStrip")
    val alertStrip: String? = null,
    @SerialName("wishListed")
    val wishListed: Boolean? = null,
) : Parcelable {
    fun calculateInStockItems(): Int {
        return availableVolumes?.count { it?.inStock == true } ?: availableVolumes?.size.orZero()
    }
}

@Parcelize
@kotlinx.serialization.Serializable
data class ProductSpecifications(
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: String? = null,
    @SerialName("icon")
    val icon: String? = null,
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class AvailableVolumeV2(
    @SerialName("productSpecifications")
    val productSpecifications: List<ProductSpecifications?>? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("alertStrip")
    val alertStrip: String? = null,
    @SerialName("inStock")
    val inStock: Boolean? = null,
    @SerialName("setToNotify")
    var isSetToNotify: Boolean? = null,
    @SerialName("media")
    val media: Media? = null,
    @SerialName("price")
    val goldDeliveryPrice: GoldDeliveryPrice? = null,
    @SerialName("productId")
    val productId: Int? = null,
    @SerialName("volume")
    val volume: Double? = null,
    @SerialName("wishListId")
    var wishListId: String? = null,
    @SerialName("cancellationPolicy")
    var cancellationPolicy: String? = null,
    @SerialName("refundPolicy")
    var refundPolicy: String? = null,
    @SerialName("noOfPeopleOrdered")
    var noOfPeopleOrdered: String? = null,
    @SerialName("peopleImages")
    var peopleImages: List<String>? = null,
) : Parcelable
