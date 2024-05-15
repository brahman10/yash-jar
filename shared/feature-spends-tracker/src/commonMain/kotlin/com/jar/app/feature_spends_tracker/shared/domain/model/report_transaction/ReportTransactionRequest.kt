package com.jar.app.feature_spends_tracker.shared.domain.model.report_transaction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportTransactionRequest(
    @SerialName("txnId")
    val txnId:String,

    @SerialName("reportType")
    val reportType:String
)

enum class ReportType{
    SELF_TRANSFER,
    WRONG_AMOUNT
}