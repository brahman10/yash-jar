package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetProductsFromWishlistUseCase

class GetProductsFromWishlistUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    GetProductsFromWishlistUseCase {
    override suspend fun getProductsFromWishlist(page: Int, size: Int) = deliveryRepository.getProductsFromWishlist(page, size)
}