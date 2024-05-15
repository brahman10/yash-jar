package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManageScreenData(
    @SerialName("sections")
    var sections: List<InsuranceDataSection>? = null,
    @SerialName("contactUs")
    val contactUs: NeedHelp? = null ,
    @SerialName("insuranceExpiredScreen")
    val insuranceExpiredScreen: InsuranceExpiredScreen?,
    @SerialName("insuranceStatusDetails")
    val insuranceStatusDetails: InsuranceStatusDetails? = null,
    @SerialName("isInsuranceExpired")
    val isInsuranceExpired: Boolean,
    @SerialName("needHelp")
    val needHelp: NeedHelp? = null,
    @SerialName("policyCards")
    val policyCards: List<PolicyCard>? = null,
    @SerialName("metaData")
    val metaData: MetaData
)