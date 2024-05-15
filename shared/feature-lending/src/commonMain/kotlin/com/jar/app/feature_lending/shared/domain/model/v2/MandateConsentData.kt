package com.jar.app.feature_lending.shared.domain.model.v2


import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class MandateConsentData(
    @SerialName("consent")
    val consent: List<String>? = null,//html
    @SerialName("instructions")
    val instructions: List<String>? = null//html
)

@kotlinx.serialization.Serializable
data class MandateSetupUpdatedContent(
    @SerialName("consent")
    val consent: String? = null,//html
    @SerialName("instruction")
    val instruction: String? = null,//html
    @SerialName("pollingTimeInMillis")
    val pollingTimeInMillis:Long? = null
)

fun MandateConsentData.getConsentData(): List<ConsentDto>? {
    return consent?.map { ConsentDto(it) }
}