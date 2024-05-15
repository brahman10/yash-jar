package com.jar.app.feature_lending.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class PlacesPrediction(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String,
    val fullText: String,
    //For UI
    var showPlaceNotFound: Boolean = false,
    var showGoogleAttribution: Boolean = false
): Parcelable