package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntroScreenAPIDataPart2(
    @SerialName("faqsTitle")
    val faqsTitle: String? = null,
    @SerialName("footer")
    val footer: String? = null,
    @SerialName("footerButtonDeepLink")
    val footerButtonDeepLink: String? = null,
    @SerialName("footerButtonText")
    val footerButtonText: String? = null,
    @SerialName("footerImage")
    val footerImage: String? = null,
    @SerialName("voucherEducationHeader")
    val voucherEducationHeader: String? = null,
    @SerialName("vouchersEducationList")
    val vouchersEducationList: List<VouchersEducation?>? = null
)