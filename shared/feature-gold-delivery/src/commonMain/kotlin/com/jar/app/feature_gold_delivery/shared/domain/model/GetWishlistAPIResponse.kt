package com.jar.app.feature_gold_delivery.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GetWishlistAPIResponse(
    @SerialName("wishList")
    val wishList: List<WishlistAPIData>? = null,
) : Parcelable