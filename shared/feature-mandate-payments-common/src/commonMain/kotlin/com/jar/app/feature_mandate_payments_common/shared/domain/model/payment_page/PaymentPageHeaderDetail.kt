package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PaymentPageHeaderDetail(
    @SerialName("toolbarHeader")
    val toolbarHeader: String,

    @SerialName("toolbarIcon")
    val toolbarIcon: Int?,

    @SerialName("title")
    val title: String,

    @SerialName("featureFlow")
    val featureFlow: String,

    @SerialName("savingFrequency")
    val savingFrequency: String,

    @SerialName("userLifecycle")
    val userLifecycle: String? = null,

    @SerialName("mandateSavingsType")
    val mandateSavingsType: MandatePaymentCommonConstants.MandateStaticContentType?,

    @SerialName("bestAmount")
    val bestAmount: Int? = null
)

@kotlinx.serialization.Serializable
data class Description(
    @SerialName("icon")
    val icon: Int,

    @SerialName("description")
    val description: String
)