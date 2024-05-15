package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetDeliveryLandingDetailsUseCase

internal class GetDeliveryLandingDetailsUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    GetDeliveryLandingDetailsUseCase {

    override suspend fun getDeliveryLandingScreenDetails() =
        deliveryRepository.getDeliveryLandingScreenDetails()
}