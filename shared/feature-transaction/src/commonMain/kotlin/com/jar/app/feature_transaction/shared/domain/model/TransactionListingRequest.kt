package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TransactionListingRequest(
    @SerialName("filtersUsed")
    val filtersUsed: Boolean? = null,
    @SerialName("individualFilterObject")
    val individualFilterObject: List<IndividualFilterObject>? = null,
    @SerialName("startDate")
    val startDate: Long? = null,
    @SerialName("endDate")
    val endDate: Long? = null,
    @SerialName("pageNumber")
    val pageNumber: Int? = null,
    @SerialName("pageSize")
    val pageSize: Int? = null
)