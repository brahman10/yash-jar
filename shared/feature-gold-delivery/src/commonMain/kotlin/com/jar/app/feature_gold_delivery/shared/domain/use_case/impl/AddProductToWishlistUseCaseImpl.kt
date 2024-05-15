package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddProductToWishlistUseCase

class AddProductToWishlistUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    AddProductToWishlistUseCase {
    override suspend fun addProductToWishlist(addCartItemRequest: AddCartItemRequest) = deliveryRepository.addProductToWishlist(addCartItemRequest)
}