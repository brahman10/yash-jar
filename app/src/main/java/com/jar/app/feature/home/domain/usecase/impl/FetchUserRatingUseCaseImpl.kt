package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.UserRepository
import com.jar.app.feature.home.domain.usecase.FetchUserRatingUseCase
import javax.inject.Inject

class FetchUserRatingUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : FetchUserRatingUseCase {
    override suspend fun getUserRating() = userRepository.getUserRating()

}