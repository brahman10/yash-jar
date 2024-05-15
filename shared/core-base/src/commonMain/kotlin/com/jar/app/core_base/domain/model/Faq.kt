package com.jar.app.core_base.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class Faq(
    @SerialName("answer")
    val answer: String,

    @SerialName("question")
    val question: String,

    @SerialName("type")
    var type: String? = null
) : Parcelable