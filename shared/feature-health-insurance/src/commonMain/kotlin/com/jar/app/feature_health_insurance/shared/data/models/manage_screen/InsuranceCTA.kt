package com.jar.app.feature_health_insurance.shared.data.models.manage_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InsuranceCTA(
    @SerialName("text") val text: String,
    @SerialName("link") val link: String,
    @SerialName("ctaType") val ctaType: String,
    @SerialName("action") val action: String? = null
)

enum class InsuranceCTAAction {
    DEEP_LINK, PAY_MANUALLY, CALL, EMAIL, GO_HOME, ADD_MEMBER_DETAILS, BACK, VERIFY, CONTACT_US, VIEW_BENEFITS;
    companion object {
        fun getInsuranceCTAAction(stringValue: String?): InsuranceCTAAction {
            return values().find {
                it.name.equals(stringValue, ignoreCase = true)
            } ?: throw (Throwable("Incorrect cta action"))
        }
    }
}