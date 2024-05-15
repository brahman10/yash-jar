package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetStoreItemFaqUseCase

class GetStoreItemFaqUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    GetStoreItemFaqUseCase {
    override suspend fun getStoreItemFaq(type: String) = deliveryRepository.getStoreItemFaq(type)
}