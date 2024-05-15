package com.jar.app.feature_sell_gold.shared.domain.models

import com.jar.app.core_base.data.dto.KycProgressResponseDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KycDetailsResponse(
    @SerialName("kycRequired")
    val kycRequired: Boolean?,
    @SerialName("kycProgressResponse")
    val kycProgressResponse: KycProgressResponseDTO?,
    @SerialName("kycStatusResponse")
    val kycStatusResponse: KycStatusResponse?,
    @SerialName("docType")
    val docType: String?,
    @SerialName("verificationState")
    val verificationState: VerificationState?,
    @SerialName("kycStatusCards")
    val kycStatusCards: KycStatusCards?,
    @SerialName("bottomSheet")
    val bottomSheet: BottomSheet?
)

@Serializable
data class KycStatusResponse(
    @SerialName("kycStatus")
    val kycStatus: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("kycScreenData")
    val kycScreenData: KycScreenData?,
    @SerialName("giftDetails")
    val giftDetails: GiftDetails?
)

@Serializable
data class KycScreenData(
    @SerialName("title")
    val title: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("allRetryExhausted")
    val allRetryExhausted: Boolean?,
    @SerialName("shouldTryAgain")
    val shouldTryAgain: Boolean?
)

@Serializable
data class GiftDetails(
    @SerialName("receiverName")
    val receiverName: String?,
    @SerialName("receiverNumber")
    val receiverNumber: String?,
    @SerialName("senderName")
    val senderName: String?,
    @SerialName("message")
    val message: String?,
    @SerialName("volume")
    val volume: Double?,
    @SerialName("giftingId")
    val giftingId: String?,
    @SerialName("status")
    val status: String?,
    @SerialName("isKYCRequired")
    val isKYCRequired: Boolean?,
    @SerialName("amount")
    val amount: Double?,
    @SerialName("giftingDate")
    val giftingDate: Long?,
    @SerialName("receiverJarUser")
    val receiverJarUser: Boolean?
)

@Serializable
data class VerificationState(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String?,
    @SerialName("iconLink")
    val iconLink: String,
    @SerialName("backgroundColor")
    val backgroundColor: String?
)

@Serializable
data class KycStatusCards(
    @SerialName("title")
    val title: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("buttonText")
    val buttonText: String?,
    @SerialName("buttonIcon")
    val buttonIcon: String?,
    @SerialName("backgroundColor")
    val backgroundColor: String?,
    @SerialName("verificationState")
    val verificationState: VerificationState?
)

@Serializable
data class BottomSheet(
    @SerialName("iconLink")
    val iconLink: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("verifyCta")
    val verifyCta: String?,
    @SerialName("contactCta")
    val contactCta: String?,
    @SerialName("contactLink")
    val contactLink: String?,
    @SerialName("buttonIcon")
    val buttonIcon: String?
)