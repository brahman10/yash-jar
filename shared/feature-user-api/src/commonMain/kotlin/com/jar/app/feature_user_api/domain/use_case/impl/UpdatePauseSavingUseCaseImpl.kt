package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase

internal class UpdatePauseSavingUseCaseImpl constructor(private val userRepository: UserRepository) :
    UpdatePauseSavingUseCase {

    override suspend fun updatePauseSavingValue(shouldPause: Boolean, pauseDuration: String?,pauseType:String) =
        userRepository.updatePauseDuration(shouldPause, pauseDuration,pauseType)

}