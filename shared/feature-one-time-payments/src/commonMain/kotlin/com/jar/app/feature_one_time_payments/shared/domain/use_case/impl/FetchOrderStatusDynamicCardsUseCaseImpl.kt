package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase

internal class FetchOrderStatusDynamicCardsUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository
) : FetchOrderStatusDynamicCardsUseCase {

    override suspend fun fetchOrderStatusDynamicCards(
        orderType: DynamicCardsOrderType,
        orderId: String?
    ) = paymentRepository.fetchOrderStatusDynamicCards(orderType, orderId)
}