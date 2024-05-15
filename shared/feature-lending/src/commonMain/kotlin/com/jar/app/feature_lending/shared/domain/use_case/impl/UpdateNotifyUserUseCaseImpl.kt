package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.UpdateNotifyUserUseCase

internal class UpdateNotifyUserUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : UpdateNotifyUserUseCase {
    override suspend fun updateNotifyUser() =
        lendingRepository.updateNotifyUser()
}