package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAddressByIdUseCase

internal class GetAddressByIdUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    GetAddressByIdUseCase {

    override suspend fun getAddressById(id: String) = deliveryRepository.getAddressById(id)
}