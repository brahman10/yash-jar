package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoalAmountResponse(
    @SerialName("amountInputText")
    val amountInputText: String? = null,
    @SerialName("amountQuestion")
    val amountQuestion: String? = null,
    @SerialName("answeredDetails")
    val answeredDetails: List<AnsweredDetail?>? = null,
    @SerialName("footerButtonText")
    val footerButtonText: String? = null,
    @SerialName("generalTip")
    val generalTip: GeneralTip? = null,
    @SerialName("higherAmountTip")
    val higherAmountTip: HigherAmountTip? = null,
    @SerialName("lowerAmountTip")
    val lowerAmountTip: LowerAmountTip? = null,
    @SerialName("maxAmount")
    val maxAmount: Int? = null,
    @SerialName("minAmount")
    val minAmount: Int? = null,
    @SerialName("title")
    val title: String? = null
)

@kotlinx.serialization.Serializable
data class DurationModel(
    val duration:String,
    var isSelected: Boolean = false
)

@kotlinx.serialization.Serializable
data class GeneralTip(
     @SerialName("icon")
    val icon: String? = null,
     @SerialName("message")
    val message: String? = null
)

@kotlinx.serialization.Serializable
data class HigherAmountTip(
     @SerialName("icon")
    val icon: String? = null,
     @SerialName("message")
    val message: String? = null
)

@kotlinx.serialization.Serializable
data class LowerAmountTip(
     @SerialName("icon")
    val icon: String? = null,
     @SerialName("message")
    val message: String? = null
)
