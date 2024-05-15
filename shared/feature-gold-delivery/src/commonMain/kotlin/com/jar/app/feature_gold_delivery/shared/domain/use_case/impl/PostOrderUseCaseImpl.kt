package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryPlaceOrderDataRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.PostOrderUseCase

class PostOrderUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    PostOrderUseCase {
    override suspend fun postOrder(request: GoldDeliveryPlaceOrderDataRequest) = deliveryRepository.postOrder(request)
}