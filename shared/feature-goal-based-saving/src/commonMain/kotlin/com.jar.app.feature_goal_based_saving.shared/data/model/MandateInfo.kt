package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MandateInfo(
    @SerialName("resetRequired")
    val resetRequired: Boolean? = null,
    @SerialName("currentMandateAmount")
    val currentMandateAmount: Float? = null,
    @SerialName("newMandateAmount")
    val newMandateAmount: Float? = null,
    @SerialName("currentSubsType")
    val currentSubsType: String? = null,
    @SerialName("authWorkflowType")
    val authWorkflowType: String? = null
){
    fun getFinalMandateAmount() = newMandateAmount ?: currentMandateAmount ?: 0f

}