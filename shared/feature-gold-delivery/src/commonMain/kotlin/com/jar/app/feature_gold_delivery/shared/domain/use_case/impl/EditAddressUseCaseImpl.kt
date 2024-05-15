package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditAddressUseCase

internal class EditAddressUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    EditAddressUseCase {

    override suspend fun editAddress(
        id: String,
        address: Address
    ) = deliveryRepository.editAddress(id, address)
}