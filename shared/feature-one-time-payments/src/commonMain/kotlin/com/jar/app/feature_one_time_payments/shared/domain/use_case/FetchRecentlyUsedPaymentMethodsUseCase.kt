package com.jar.app.feature_one_time_payments.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethod
import kotlinx.coroutines.flow.Flow

interface FetchRecentlyUsedPaymentMethodsUseCase {

    suspend fun fetchRecentlyUsedPaymentMethods(isPackageInstalled: (packageName: String) -> Boolean, flowContext: String?): Flow<RestClientResult<List<PaymentMethod>>>

}