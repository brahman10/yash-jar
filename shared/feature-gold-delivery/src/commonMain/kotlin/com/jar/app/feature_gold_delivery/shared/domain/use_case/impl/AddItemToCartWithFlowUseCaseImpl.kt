package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase

class AddItemToCartWithFlowUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    AddItemToCartWithFlowUseCase {
    override suspend fun addItemToCart(addCartItemRequest: AddCartItemRequest) =
        deliveryRepository.addItemToCart(addCartItemRequest)
}