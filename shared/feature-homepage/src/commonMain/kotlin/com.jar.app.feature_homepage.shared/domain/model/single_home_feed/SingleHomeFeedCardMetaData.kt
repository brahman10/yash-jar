package com.jar.app.feature_homepage.shared.domain.model.single_home_feed

import com.jar.app.core_base.domain.model.card_library.TextList
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SingleHomeFeedCardMetaData(
    @SerialName("bgImage")
    val bgImage: String,

    @SerialName("buttonList")
    val buttonList: List<Button>,

    @SerialName("cardType")
    var cardType: String,

    @SerialName("description")
    val description: TextList,

    @SerialName("featureType")
    var featureType: String,

    @SerialName("header")
    val header: TextList,

    @SerialName("offerDetails")
    val offerDetails: OfferDetails? = null,

    @SerialName("order")
    var order: Int,

    @SerialName("shouldShowAsSingleCard")
    var shouldShowAsSingleCard: Boolean,

    @SerialName("title")
    val title: TextList? = null,

    @SerialName("trustedByImage")
    val trustedByImage: String? = null,

    @SerialName("variantType")
    var variantType: String,

    @SerialName("showMoreButton")
    var showMoreButton: Boolean,

    @SerialName("showCard")
    var showCard: Boolean,

    @SerialName("requiredContactPermission")
    val requiredContactPermission: Boolean? = null,

    var isExpanded: Boolean? = null
)