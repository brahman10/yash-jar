package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceTransactionsData(
    @SerialName("nextPage")
    val nextPage: Int? = null,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("title")
    val title: String,
    @SerialName("transactions")
    val insuranceTransactionDataList: List<InsuranceTransactionData>
)