package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteAddressUseCase

internal class DeleteAddressUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    DeleteAddressUseCase {

    override suspend fun deleteAddress(id: String) = deliveryRepository.deleteAddress(id)
}