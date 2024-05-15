package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.UserRepository
import com.jar.app.feature.home.domain.usecase.UpdateUserRatingUseCase
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class UpdateUserRatingUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : UpdateUserRatingUseCase {

    override suspend fun submitUserRating(json: JsonObject) = userRepository.submitUserRating(json)

}