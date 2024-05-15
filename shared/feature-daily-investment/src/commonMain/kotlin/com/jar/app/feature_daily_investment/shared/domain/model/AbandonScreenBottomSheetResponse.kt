package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AbandonScreenBottomSheetResponse(
    @SerialName("param")
    val param : Int,

    @SerialName("contentType")
    val contentType : String,

    @SerialName("dailySavingAbandonScreen")
    val dailySavingAbandonScreen: DailySavingAbandonScreen

)

@kotlinx.serialization.Serializable
data class DailySavingAbandonScreen (

    @SerialName("header")
    val header : String? = null,

    @SerialName("stepsList")
    val stepsList : ArrayList<Steps>? = null,

    @SerialName("footerButton1")
    val footerButton1 : String? = null,

    @SerialName("footerButton2")
    val footerButton2 : String? = null,

    @SerialName("profilePics")
    val profilePics : ArrayList<String>? = null,

    @SerialName("footerText")
    val footerText : String? = null,

    )

@kotlinx.serialization.Serializable
data class Steps (

    @SerialName("title")
    val title : String,

    @SerialName("imageUrl")
    val imageUrl : String? = null,
)