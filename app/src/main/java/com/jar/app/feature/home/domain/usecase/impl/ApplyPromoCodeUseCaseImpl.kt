package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.UserRepository
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import com.jar.app.feature.home.domain.usecase.ApplyPromoCodeUseCase
import javax.inject.Inject

internal class ApplyPromoCodeUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : ApplyPromoCodeUseCase {

    override suspend fun applyPromoCode(
        deviceDetails: DeviceDetails,
        promoCode: String,
        id: String?,
        type: String?
    ) = userRepository.applyPromoCode(deviceDetails, promoCode, id, type)
}