package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartBreakdownUseCase

class FetchCartBreakdownUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    FetchCartBreakdownUseCase {
    override suspend fun fetchCartBreakdown() = deliveryRepository.fetchCartBreakdown()
}