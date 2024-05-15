package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.UserRepository
import com.jar.app.feature.home.domain.model.UserDeviceDetails
import com.jar.app.feature.home.domain.usecase.UpdateUserDeviceDetailsUseCase
import javax.inject.Inject

internal class UpdateUserDeviceDetailsUseCaseImpl @Inject constructor(private val userRepository: UserRepository) :
    UpdateUserDeviceDetailsUseCase {

    override suspend fun updateUserDeviceDetails(userDeviceDetails: UserDeviceDetails) =
        userRepository.updateUserDeviceDetails(userDeviceDetails)
}