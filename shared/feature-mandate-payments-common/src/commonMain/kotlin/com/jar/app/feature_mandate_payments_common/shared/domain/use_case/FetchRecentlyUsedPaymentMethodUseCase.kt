package com.jar.app.feature_mandate_payments_common.shared.domain.use_case

import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.PaymentMethod
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.RecentlyUsedPaymentMethodData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchRecentlyUsedPaymentMethodUseCase {

    suspend fun fetchRecentlyUsedPaymentMethods(flowType: String?, isPackageInstalled: (packageName: String) -> Boolean): Flow<RestClientResult<List<PaymentMethod>?>>

}