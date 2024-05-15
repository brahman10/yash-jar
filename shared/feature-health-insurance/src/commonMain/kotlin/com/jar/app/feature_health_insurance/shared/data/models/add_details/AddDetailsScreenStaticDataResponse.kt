package com.jar.app.feature_health_insurance.shared.data.models.add_details

import com.jar.app.feature_health_insurance.shared.data.models.NeedHelp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddDetailsScreenStaticDataResponse(
    @SerialName("ctaDeeplink")
    val ctaDeeplink: String? = null,
    @SerialName("ctaText")
    val ctaText: String? = null,
    @SerialName("currentPageNumber")
    val currentPageNumber: Int? = null,
    @SerialName("defaultInfoIcon")
    val defaultInfoIcon: String? = null,
    @SerialName("defaultInfoText")
    val defaultInfoText: String? = null,
    @SerialName("defaultMembers")
    val defaultMembers: List<Int>? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("hintForMyself")
    val hintForMyself: String? = null,
    @SerialName("hintForMyselfAndSpouse")
    val hintForMyselfAndSpouse: String? = null,
    @SerialName("kidsInfoIcon")
    val kidsInfoIcon: String? = null,
    @SerialName("kidsInfoText")
    val kidsInfoText: String? = null,
    @SerialName("kidsPolicyInfoText")
    val kidsPolicyInfoText: String? = null,
    @SerialName("labelTextForMyself")
    val labelTextForMyself: String? = null,
    @SerialName("labelTextForMyselfAndSpouse")
    val labelTextForMyselfAndSpouse: String? = null,
    @SerialName("mandatoryContextText")
    val mandatoryContextText: String? = null,
    @SerialName("mandatoryHeader")
    val mandatoryHeader: String? = null,
    @SerialName("premiumValuesMemberDetailsList")
    val premiumValuesMemberDetailsList: List<String>? = null,
    @SerialName("toolBarText")
    val toolBarText: String? = null,
    @SerialName("totalPageNumber")
    val totalPageNumber: Int? = null,
    @SerialName("needHelp")
    val needHelp: NeedHelp? = null,
    @SerialName("errorInfoIcon")
    val errorInfoIcon: String,
    @SerialName("insuranceMaxAge")
    val insuranceMaxAge: Int? = null,
    @SerialName("insuranceMinAge")
    val insuranceMinAge: Int? = null,
    @SerialName("errors")
    val errors: Map<String, String>? = null,
    @SerialName("kidsPolicyInfoIcon")
    val kidsPolicyInfoIcon: String? = null,
    @SerialName("footer")
    val footer: FooterData? = null
)