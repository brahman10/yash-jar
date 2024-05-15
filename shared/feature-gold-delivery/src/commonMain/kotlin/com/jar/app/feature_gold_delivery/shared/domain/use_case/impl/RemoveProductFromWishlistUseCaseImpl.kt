package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.RemoveProductFromWishlistUseCase

class RemoveProductFromWishlistUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    RemoveProductFromWishlistUseCase {
    override suspend fun removeProductFromWishlist(id: String) = deliveryRepository.removeProductFromWishlist(id)
}