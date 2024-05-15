package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.core_base.domain.model.User
import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase

internal class UpdateUserUseCaseImpl constructor(private val userRepository: UserRepository) :
    UpdateUserUseCase {

    override suspend fun updateUser(user: User) = userRepository.updateUser(user)
}