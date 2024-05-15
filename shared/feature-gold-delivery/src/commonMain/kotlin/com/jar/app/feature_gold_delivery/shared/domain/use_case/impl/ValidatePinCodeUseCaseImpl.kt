package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase

internal class ValidatePinCodeUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    ValidatePinCodeUseCase {

    override suspend fun validatePinCode(pinCode: String) =
        deliveryRepository.validatePinCode(pinCode)
}