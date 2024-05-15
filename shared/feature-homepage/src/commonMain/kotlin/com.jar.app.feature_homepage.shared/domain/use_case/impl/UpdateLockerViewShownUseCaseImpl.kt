package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateLockerViewShownUseCase

internal class UpdateLockerViewShownUseCaseImpl constructor(private val homeRepository: HomeRepository) :
    UpdateLockerViewShownUseCase {
    override suspend fun updateLockerViewShown() = homeRepository.updateLockerViewShown()
}