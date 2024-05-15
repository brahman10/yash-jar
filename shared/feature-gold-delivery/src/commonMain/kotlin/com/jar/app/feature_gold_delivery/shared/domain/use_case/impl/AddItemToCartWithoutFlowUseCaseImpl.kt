package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithoutFlowUseCase

class AddItemToCartWithoutFlowUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    AddItemToCartWithoutFlowUseCase {
    override suspend fun addItemToCartWithoutFlow(addCartItemRequest: AddCartItemRequest) =
        deliveryRepository.addItemToCartWithoutFlow(addCartItemRequest)
}