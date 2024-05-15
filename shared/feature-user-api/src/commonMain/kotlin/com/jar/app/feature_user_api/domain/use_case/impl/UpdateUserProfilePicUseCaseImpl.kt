package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.UpdateUserProfilePicUseCase

internal class UpdateUserProfilePicUseCaseImpl constructor(
    private val userRepository: UserRepository
) : UpdateUserProfilePicUseCase {

    override suspend fun updateUserProfilePhoto(byteArray: ByteArray) =
        userRepository.updateUserProfilePhoto(byteArray)

}