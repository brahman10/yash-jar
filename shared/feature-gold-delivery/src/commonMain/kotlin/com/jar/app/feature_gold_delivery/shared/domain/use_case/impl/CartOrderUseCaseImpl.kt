package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.CartOrderUseCase

internal class CartOrderUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    CartOrderUseCase {

    override suspend fun submitFeedback(
        orderId: String,
        feedback: Int
    ) = deliveryRepository.submitFeedback(orderId, feedback)
}