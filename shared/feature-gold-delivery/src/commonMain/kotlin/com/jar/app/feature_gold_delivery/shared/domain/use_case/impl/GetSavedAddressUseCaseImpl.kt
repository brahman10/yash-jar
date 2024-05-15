package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetSavedAddressUseCase

internal class GetSavedAddressUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    GetSavedAddressUseCase {

    override suspend fun getSavedAddress() = deliveryRepository.getAllAddress()
}