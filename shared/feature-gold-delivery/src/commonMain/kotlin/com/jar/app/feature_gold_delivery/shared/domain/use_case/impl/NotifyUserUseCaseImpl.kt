package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.NotifyUserUseCase

class NotifyUserUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    NotifyUserUseCase {
    override suspend fun notifyUser(addCartItemRequest: AddCartItemRequest) = deliveryRepository.notifyUser(addCartItemRequest)
}