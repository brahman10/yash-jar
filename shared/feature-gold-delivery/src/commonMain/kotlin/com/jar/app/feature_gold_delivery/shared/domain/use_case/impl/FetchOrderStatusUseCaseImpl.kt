package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchOrderStatusUseCase

class FetchOrderStatusUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    FetchOrderStatusUseCase {
    override suspend fun fetchOrderStatus(orderId: String) = deliveryRepository.fetchOrderStatus(orderId)
}