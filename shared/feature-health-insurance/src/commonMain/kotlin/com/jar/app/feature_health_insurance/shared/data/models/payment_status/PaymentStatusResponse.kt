package com.jar.app.feature_health_insurance.shared.data.models.payment_status


import com.jar.app.feature_health_insurance.shared.data.models.NeedHelp
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceCTA
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusResponse(

    @SerialName("status")
    val status: String? = null,
    @SerialName("amountPaid")
    val amountPaid: String? = null,
    @SerialName("amountPaidText")
    val amountPaidText: String? = null,
    @SerialName("ctaText")
    val ctaText: String? = null,
    @SerialName("ctaLink")
    val ctaLink: String? = null,
    @SerialName("insuranceImgUrl")
    val insuranceImgUrl: String? = null,
    @SerialName("insurancePolicyDetails")
    val insurancePolicyDetails: InsurancePolicyDetails? = null,
    @SerialName("premiumType")
    val premiumType: String? = null,
    @SerialName("premiumTypeText")
    val premiumTypeText: String? = null,
    @SerialName("statusIconUrl")
    val statusIconUrl: String? = null,
    @SerialName("statusImgUrl")
    val statusImgUrl: String? = null,
    @SerialName("statusLottieUrl")
    val statusLottieUrl: String? = null,
    @SerialName("statusMessage")
    val statusMessage: String? = null,
    @SerialName("homePageCtaText")
    val homePageCtaText: String? = null,
    @SerialName("needHelpText")
    val needHelpText: String? = null,
    @SerialName("contactSupportText")
    val contactSupportText: String? = null,
    @SerialName("transactionDetails")
    val transactionDetails: TransactionDetails? = null,
    @SerialName("needHelp")
    val needHelp: NeedHelp? = null,
    @SerialName("cta")
    val cta: InsuranceCTA? = null,
)

enum class PaymentStatus {
    SUCCESS,
    FAILURE,
    PENDING,
    INITIATED,
}