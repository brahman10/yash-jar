package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase

class FetchCartUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    FetchCartUseCase {
    override suspend fun fetchCart() = deliveryRepository.fetchCart()
}