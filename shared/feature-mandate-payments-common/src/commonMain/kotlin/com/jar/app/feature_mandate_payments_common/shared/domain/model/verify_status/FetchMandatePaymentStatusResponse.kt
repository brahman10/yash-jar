package com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status

import com.jar.app.core_base.data.dto.GoldBalanceDTO
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class FetchMandatePaymentStatusResponse(
    @SerialName("recurringAmount")
    val recurringAmount: Float? = null,

    @SerialName("recurringFrequency")
    val recurringFrequency: String? = null,

    @SerialName("startDate")
    val startDate: String? = null,

    @SerialName("endDate")
    val endDate: String? = null,

    @SerialName("status")
    private val status: String? = null,

    @SerialName("success")
    val success: Boolean? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("subTitle")
    val subTitle: String? = null,

    @SerialName("bankLogo")
    val bankLogo: String? = null,

    @SerialName("bankName")
    val bankName: String? = null,

    @SerialName("provider")
    val provider: String? = null,

    @SerialName("upiId")
    val upiId: String? = null,

    @SerialName("subscriptionId")
    val subscriptionId: String? = null,

    @SerialName("invoiceLink")
    val invoiceLink: String? = null,

    @SerialName("autopaySuccessData")
    val autoPaySuccessData: AutoPaySuccessData? = null

) : Parcelable {

    fun getAutoInvestStatus(): MandatePaymentProgressStatus {
        return if (status.isNullOrBlank())
            MandatePaymentProgressStatus.FAILURE
        else MandatePaymentProgressStatus.valueOf(status)
    }

    fun getRecurringFrequency(): StringResource {
        return if (recurringFrequency.isNullOrEmpty()) FrequencyStatus.weekly.frequencyRes
        else FrequencyStatus.valueOf(recurringFrequency).frequencyRes
    }
}

@Parcelize
@kotlinx.serialization.Serializable
data class AutoPaySuccessData(
    @SerialName("lottie")
    val lottie: String,
    @SerialName("goldBalanceResponse")
    val goldBalanceResponse: GoldBalanceDTO? = null,
    @SerialName("couponDescription")
    val couponDescription: String? = null
) : Parcelable