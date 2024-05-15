package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddDeliveryAddressUseCase

internal class AddDeliveryAddressUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    AddDeliveryAddressUseCase {

    override suspend fun addDeliveryAddress(address: Address) =
        deliveryRepository.addDeliveryAddress(address)
}