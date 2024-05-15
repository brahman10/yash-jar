package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.UserRepository
import com.jar.app.feature.home.domain.usecase.UpdateFcmTokenUseCase
import javax.inject.Inject

internal class UpdateFcmTokenUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : UpdateFcmTokenUseCase {

    override suspend fun updateFcmToken(fcmToken: String, instanceId: String?) =
        userRepository.updateFcmToken(fcmToken, instanceId)

}