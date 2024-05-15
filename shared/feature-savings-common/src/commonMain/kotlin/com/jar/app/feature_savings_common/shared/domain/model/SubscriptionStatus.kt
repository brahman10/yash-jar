package com.jar.app.feature_savings_common.shared.domain.model

enum class SubscriptionStatus {
    SUCCESS, PENDING, FAILURE;

    companion object {
        fun valueFromString(value: String): SubscriptionStatus {
            return valueOf(value)
        }
    }

}