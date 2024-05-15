package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.app.feature_user_api.domain.use_case.DeleteUserVpaUseCase

internal class DeleteUserVpaUseCaseImpl constructor(private val userRepository: UserRepository) :
    DeleteUserVpaUseCase {

    override suspend fun deleteUserSavedVPA(savedVpaId: String) =
        userRepository.deleteUserSavedVPA(savedVpaId)

}