package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import com.jar.app.feature_health_insurance.shared.data.models.NeedHelp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SelectPremiumResponse(
    @SerialName("currentPageNo")
    val currentPageNo: Int?,
    @SerialName("header")
    val header: Header?,
    @SerialName("insuranceType")
    val insuranceType: String?,
    @SerialName("main")
    val main: Main?,
    @SerialName("navigationTitle")
    val navigationTitle: String?,
    @SerialName("totalPageNo")
    val totalPageNo: Int?,
    @SerialName("needHelp")
    val needHelp: NeedHelp,
    @SerialName("testimonials")
    val testimonials: List<Testimonial>? = null,
    @SerialName("returnScreen")
    val returnScreen: SelectPlanReturnScreen? = null
)