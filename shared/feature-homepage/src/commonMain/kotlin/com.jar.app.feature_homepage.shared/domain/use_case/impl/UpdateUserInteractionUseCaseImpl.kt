package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateUserInteractionUseCase

internal class UpdateUserInteractionUseCaseImpl constructor(private val homeRepository: HomeRepository) :
    UpdateUserInteractionUseCase {
    override suspend fun updateUserInteraction(order: Int,featureType: String) =
        homeRepository.updateUserInteraction(order,featureType)
}