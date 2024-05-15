package com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status

@kotlinx.serialization.Serializable
enum class MandatePaymentProgressStatus {
    SUCCESS, PENDING, FAILURE
}

object MandatePaymentProgressStatusConverter{

    fun getFromString(status:String): MandatePaymentProgressStatus = when(status){
        "SUCCESS"-> MandatePaymentProgressStatus.SUCCESS
        "PENDING"-> MandatePaymentProgressStatus.PENDING
        "FAILURE"-> MandatePaymentProgressStatus.FAILURE
        else -> MandatePaymentProgressStatus.PENDING
    }
}