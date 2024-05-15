package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GoalStatusResponse(
    @SerialName("goalId")
    val goalId: String? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("statusIcon")
    val statusIcon: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("txnStatusHeader")
    val txnStatusHeader: String? = null,
    @SerialName("txnRoutine")
    val txnRoutine: List<TxnRoutineItem>? = null,
    @SerialName("statusDetails")
    val statusDetails: StatusDetails? = null,
    @SerialName("setupSavingsGoalDetails")
    val setupSavingsGoalDetails: SetupSavingsGoalDetails? = null,
    @SerialName("orderDetails")
    val orderDetails: OrderDetails? = null,
    @SerialName("buttonCta")
    val buttonCta: ButtonCta? = null
)
enum class ManualPaymentStatus {
    SUCCESS,
    PENDING,
    FAILED;
    companion object {
        fun fromString(value: String?): ManualPaymentStatus {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: SUCCESS
        }
    }
}

@Serializable
data class TxnRoutineItem(
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("isKYCRequired")
    val isKYCRequired: Boolean? = null
)

@Serializable
data class SetupSavingsGoalDetails(
    @SerialName("header")
    val header: String? = null,
    @SerialName("showChevron")
    val showChevron: Boolean? = null,
    @SerialName("goalDetailList")
    val goalDetailList: List<GoalDetailItem>? = null
)

@Serializable
data class OrderDetails(
    @SerialName("header")
    val header: String? = null,
    @SerialName("showChevron")
    val showChevron: Boolean? = null,
    @SerialName("priceSubHeader")
    val priceSubHeader: String? = null,
    @SerialName("priceDetailList")
    val priceDetailList: List<PriceDetailItem>? = null,
    @SerialName("txnSubHeader")
    val txnSubHeader: String? = null,
    @SerialName("txnDetailList")
    val txnDetailList: List<TxnDetailItem>? = null
)

@Serializable
data class PriceDetailItem(
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: String? = null,
    @SerialName("copy")
    val copy: Boolean? = null
)

@Serializable
data class TxnDetailItem(
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: String? = null,
    @SerialName("copy")
    val copy: Boolean? = null
)

@Serializable
data class ButtonCta(
    @SerialName("text")
    val text: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("deeplink")
    val deeplink: String? = null
)
