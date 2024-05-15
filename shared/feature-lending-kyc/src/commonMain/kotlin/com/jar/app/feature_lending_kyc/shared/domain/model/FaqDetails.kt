package com.jar.app.feature_lending_kyc.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FaqDetails(
    @SerialName("kycFAQList")
    val faqType: List<FaqTitleAndType>
)

@kotlinx.serialization.Serializable
data class FaqTitleAndType(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String
)

@kotlinx.serialization.Serializable
data class FaqTypeDetails(
    @SerialName("kycLendingFAQs")
    val kYCFaqs: KYCFaqs
)


// TODO : RAHUL -> NEED TO REMOVE BELOW DATA CLASSES WHEN KYC REFACTOR TO SHARED.
@kotlinx.serialization.Serializable
data class KycFaqResponse(
    @SerialName("kycDetails")
    val kycDetails: KycFaqDetails
)

@kotlinx.serialization.Serializable
data class KycFaqDetails(
    @SerialName("faq")
    val faq: KYCFaqs,

    @SerialName("kycLimit")
    val kycLimit: String? = null
)

@kotlinx.serialization.Serializable
data class KYCFaqs(
    @SerialName("faqsList")
    val faqDataList: List<FaqData>
)

@kotlinx.serialization.Serializable
data class FaqData(
    @SerialName("faqs")
    val faqs: List<Faq>,

    @SerialName("type")
    val type: String
)

@Parcelize
@kotlinx.serialization.Serializable
data class Faq(
    @SerialName("answer")
    val answer: String,

    @SerialName("question")
    val question: String,

    @SerialName("type")
    var type: String? = null,

    @SerialName("disclaimer")
    val disclaimer: String? = null
) : Parcelable
