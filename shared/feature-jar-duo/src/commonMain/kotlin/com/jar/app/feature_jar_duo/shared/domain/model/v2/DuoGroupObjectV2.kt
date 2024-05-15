package com.jar.app.feature_jar_duo.shared.domain.model.v2

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoGroupObjectV2(

    @SerialName("userName")
    val userName: String,

    @SerialName("owner")
    val isOwner:Boolean,

    @SerialName("duoTopObjects")
    val top: List<DuoGroupTopObjectV2>,

    @SerialName("userProfilePhoto")
    val userProfile: String? = null,

    @SerialName("overallScore")
    val overallScore: Int


    ) : Parcelable