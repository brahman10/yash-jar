package com.jar.app.feature_health_insurance.shared.data.models.transaction_details


import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.NeedHelp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceTransactionDetails(
    @SerialName("toolbarTitle")
    val toolbarTitle: String,
    @SerialName("paymentCard")
    val paymentDescriptionCard: PaymentPlanDescriptionCard? = null,
    @SerialName("contactUs")
    val contactUs: NeedHelp? = null,
    @SerialName("orderDetails")
    val orderDetails: OrderDetails? = null,
    @SerialName("paymentStatus")
    val paymentStatusesData: PaymentStatusesData? = null
)

@Serializable
data class OrderDetails(
    @SerialName("title")
    val title: String,
    @SerialName("sections")
    val orderDetailsList: List<OrderDetail>? = null
)

@Serializable
data class PaymentStatusesData(
    @SerialName("title")
    val title: String,
    @SerialName("sections")
    val paymentStatusDataList: List<PaymentStatusData>? = null
)