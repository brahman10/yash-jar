package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.domain.model.DeliverProductRequest
import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeliverProductUseCase

internal class DeliverProductUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    DeliverProductUseCase {

    override suspend fun deliverProduct(deliverProductRequest: DeliverProductRequest) =
        deliveryRepository.deliverProduct(deliverProductRequest)

}