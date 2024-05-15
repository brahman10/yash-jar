package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

import com.jar.app.core_base.domain.model.Faq
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RealTimeLanding(
    @SerialName("toolbarTitle")
    val toolbarTitle: String,
    @SerialName("headerCard")
    val headerCard: HeaderCard,
    @SerialName("stepsCard")
    val stepsCard: StepsCard,
    @SerialName("benefitsCard")
    val benefitsCard: BenefitsCard,
    @SerialName("faqs")
    val faqs: List<Faq>,
    @SerialName("type")
    val type: String
)


@kotlinx.serialization.Serializable
data class HeaderCard(
    @SerialName("creditScoreCard")
    val creditScoreCard: CreditScoreCard? = null,
    @SerialName("genericCard")
    val genericCard: RealTimeGenericCard? = null ,
    @SerialName("refreshCreditScore")
    val refreshCreditScore: Boolean
)

@kotlinx.serialization.Serializable
data class CreditScoreCard(
    @SerialName("backgroundColor")
    val backgroundColor: String,
    @SerialName("footerText")
    val footerText: String,
    @SerialName("creditScore")
    val creditScore: Int,
    @SerialName("creditScoreResult")
    val creditScoreResult: String
)

@kotlinx.serialization.Serializable
data class RealTimeGenericCard(
    @SerialName("backgroundColor")
    val backgroundColor: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("footerText")
    val footerText: String,
    @SerialName("imageUrl")
    val imageUrl: String
)

@kotlinx.serialization.Serializable
data class StepsCard(
    @SerialName("title")
    val title: String,
    @SerialName("realTimeSteps")
    val realTimeSteps: List<CardItemData>
)
@kotlinx.serialization.Serializable
data class BenefitsCard(
    @SerialName("title")
    val title: String,
    @SerialName("realTimeBenefits")
    val realTimeBenefits: List<CardItemData>
)

@kotlinx.serialization.Serializable
data class CardItemData(
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("description")
    val description: String,
    @SerialName("order")
    val order: Int = 0
)