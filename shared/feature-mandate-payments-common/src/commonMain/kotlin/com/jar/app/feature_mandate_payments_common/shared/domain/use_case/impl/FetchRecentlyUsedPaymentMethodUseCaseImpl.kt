package com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl

import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.PaymentMethod
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.MandatePaymentMethodType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.PaymentMethodUpiIntent
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchRecentlyUsedPaymentMethodUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class FetchRecentlyUsedPaymentMethodUseCaseImpl constructor(
    private val mandatePaymentRepository: MandatePaymentRepository,
    private val json: Json
) : FetchRecentlyUsedPaymentMethodUseCase {

    override suspend fun fetchRecentlyUsedPaymentMethods(flowType: String?, isPackageInstalled: (packageName: String) -> Boolean)  =
        flow<RestClientResult<List<PaymentMethod>?>> {

            val recentlyUseMethods = mutableListOf<PaymentMethod>()

            mandatePaymentRepository.fetchRecentlyUsedPaymentMethods(flowType).collect(
                onLoading = {
                    emit(RestClientResult.loading())
                },
                onSuccess = {
                    it?.paymentsData?.forEach {
                        val paymentMethod: String? = it.jsonObject["paymentMethod"]?.jsonPrimitive?.contentOrNull
                        when (MandatePaymentMethodType.values().find { it.name == paymentMethod }) {
                            MandatePaymentMethodType.CARD -> {
                                // Not Supported In Mandate
                            }
                            MandatePaymentMethodType.NB -> {
                                // Not Supported In Mandate
                            }
                            MandatePaymentMethodType.UPI_COLLECT -> {
                                // Not Supported In Mandate
                            }
                            MandatePaymentMethodType.UPI_INTENT -> {
                                val upiIntentResponse =
                                    json.decodeFromJsonElement<PaymentMethodUpiIntent>(it)

                                if (isPackageInstalled(upiIntentResponse.payerApp)) {
                                    recentlyUseMethods.add(upiIntentResponse)
                                }
                            }
                            else -> {
                                //Do nothing in this case
                            }
                        }
                    }
                    emit(RestClientResult.success(recentlyUseMethods))
                },
                onSuccessWithNullData = {

                },
                onError = { errorMessage, errorCode ->
                    emit(RestClientResult.error(message = errorMessage, errorCode = errorCode))
                }
            )
        }

}