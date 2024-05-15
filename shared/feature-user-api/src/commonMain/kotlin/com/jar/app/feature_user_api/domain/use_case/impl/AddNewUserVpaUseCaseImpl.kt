package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.AddNewUserVpaUseCase

internal class AddNewUserVpaUseCaseImpl constructor(private val userRepository: UserRepository) :
    AddNewUserVpaUseCase {

    override suspend fun addNewVPA(vpaName: String) = userRepository.addNewVPA(vpaName)

}