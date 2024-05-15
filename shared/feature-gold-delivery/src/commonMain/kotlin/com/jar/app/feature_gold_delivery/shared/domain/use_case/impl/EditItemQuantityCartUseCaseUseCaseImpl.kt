package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditItemQuantityCartUseCase

internal class EditItemQuantityCartUseCaseUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    EditItemQuantityCartUseCase {

    override suspend fun changeQuantityInCart(
        id: String,
        quantity: Int
    ) = deliveryRepository.changeQuantityInCart(id, quantity)
}