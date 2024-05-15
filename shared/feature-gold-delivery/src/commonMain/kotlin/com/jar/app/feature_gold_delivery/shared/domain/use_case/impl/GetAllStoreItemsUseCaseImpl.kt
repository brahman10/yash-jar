package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase

class GetAllStoreItemsUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    GetAllStoreItemsUseCase {
    override suspend fun getAllStoreItems(category: String?) = deliveryRepository.getAllStoreItems(category)
}