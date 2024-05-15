package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AmountSelectionResp(
    @SerialName("param")
    val param : Int? = null,

    @SerialName("contentType")
    val contentType : String? = null,

    @SerialName("savingsBenefits")
    val savingsBenefits: SavingsBenefits? = null

)

@kotlinx.serialization.Serializable
data class SavingsBenefits(

    @SerialName("goldHeader")
    val goldHeader: String? = null,

    @SerialName("header")
    val header : String? = null,

    @SerialName("rateOfAppreciation")
    val rateOfAppreciation : Float? = null,

    @SerialName("savingsBenefitsObjectList")
    val savingsBenefitsObjectList : ArrayList<SavingsBenefitsObjectList>? = null,

    )

@kotlinx.serialization.Serializable
data class SavingsBenefitsObjectList(

    @SerialName("title")
    val title : String? = null,

    @SerialName("description")
    val description : String? = null,

    @SerialName("imageUrl")
    val imageUrl : String? = null,

    )