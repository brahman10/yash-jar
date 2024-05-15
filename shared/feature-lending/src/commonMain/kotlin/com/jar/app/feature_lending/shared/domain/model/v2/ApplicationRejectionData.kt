package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ApplicationRejectionData(
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,//html
    @SerialName("faqs")
    val faqs: List<QuestionAnswer>? = null,
    //TODO : Ankur get it added from BE
    @SerialName("daysLeft")
    val daysLeft: Int? = null,
)