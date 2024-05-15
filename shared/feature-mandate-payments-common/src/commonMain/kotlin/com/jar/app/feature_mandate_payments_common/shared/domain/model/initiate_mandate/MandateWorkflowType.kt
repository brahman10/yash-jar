package com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate

@kotlinx.serialization.Serializable
enum class MandateWorkflowType {
    PENNY_DROP, TRANSACTION
}