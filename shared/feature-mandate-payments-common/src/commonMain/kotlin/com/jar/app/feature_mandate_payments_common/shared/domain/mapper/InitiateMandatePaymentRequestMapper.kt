package com.jar.app.feature_mandate_payments_common.shared.domain.mapper

import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentGateway

fun InitiateMandatePaymentRequest.toInitiateMandatePaymentApiRequest(
    mandatePaymentGateway: MandatePaymentGateway,
    packageName: String,
    phonePeVersionCode: String?
): InitiateMandatePaymentApiRequest {
    return InitiateMandatePaymentApiRequest(
        provider = mandatePaymentGateway.name,
        mandateAmount = mandateAmount,
        authWorkflowType = authWorkflowType.name,
        packageName = packageName,
        phonePeVersionCode = phonePeVersionCode,
        insuranceId = insuranceId,
        subscriptionType = subscriptionType,
        goalId = goalId
    )
}