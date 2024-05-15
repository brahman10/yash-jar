package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.*
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchRecentlyUsedPaymentMethodsUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class FetchRecentlyUsedPaymentMethodsUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository,
    private val json: Json
) : FetchRecentlyUsedPaymentMethodsUseCase {

    override suspend fun fetchRecentlyUsedPaymentMethods(isPackageInstalled: (packageName: String) -> Boolean, flowContext: String?) =
        flow<RestClientResult<List<PaymentMethod>>> {

            val recentlyUseMethods = mutableListOf<PaymentMethod>()

            paymentRepository.fetchRecentlyUsedPaymentMethods(flowContext).collect(
                onLoading = {
                    emit(RestClientResult.loading())
                },
                onSuccess = {
                    it.paymentsData.forEach {
                        val paymentMethod: String? = it.jsonObject["paymentMethod"]?.jsonPrimitive?.contentOrNull
                        when (OneTimePaymentMethodType.values().find { it.name == paymentMethod }) {
                            OneTimePaymentMethodType.CARD -> {
                                recentlyUseMethods.add(
                                    json.decodeFromJsonElement<PaymentMethodCard>(it)
                                )
                            }
                            OneTimePaymentMethodType.NB -> {
                                recentlyUseMethods.add(
                                    json.decodeFromJsonElement<PaymentMethodNB>(it)
                                )
                            }
                            OneTimePaymentMethodType.UPI_COLLECT -> {
                                recentlyUseMethods.add(
                                    json.decodeFromJsonElement<PaymentMethodUpiCollect>(it)
                                )
                            }
                            OneTimePaymentMethodType.UPI_INTENT -> {
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