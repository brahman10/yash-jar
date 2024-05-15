package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LendingFaqDetail(
    @SerialName("question")
    val question: String,
    @SerialName("answer")
    val answer: String
)
