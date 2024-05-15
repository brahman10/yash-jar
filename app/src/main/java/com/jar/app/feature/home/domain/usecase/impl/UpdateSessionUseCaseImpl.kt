package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.usecase.UpdateSessionUseCase
import javax.inject.Inject

internal class UpdateSessionUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository
) : UpdateSessionUseCase {

    override suspend fun updateSession(appVersion: Int) = homeRepository.updateSession(appVersion)
}