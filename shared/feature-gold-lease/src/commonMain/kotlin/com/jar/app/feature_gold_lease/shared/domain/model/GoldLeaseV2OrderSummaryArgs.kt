package com.jar.app.feature_gold_lease.shared.domain.model

@kotlinx.serialization.Serializable
data class GoldLeaseV2OrderSummaryArgs(
    val flowType: String,
    val goldLeaseV2OrderSummaryScreenData: GoldLeaseV2OrderSummaryScreenData?,
    val leaseId: String?,
    val isNewLeaseUser: Boolean
)