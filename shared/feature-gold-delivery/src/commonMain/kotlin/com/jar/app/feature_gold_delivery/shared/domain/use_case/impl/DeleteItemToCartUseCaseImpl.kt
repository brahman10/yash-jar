package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase

internal class DeleteItemToCartUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    DeleteItemToCartUseCase {

    override suspend fun deleteItemFromCart(productID: String) =
        deliveryRepository.deleteItemFromCart(productID)
}



