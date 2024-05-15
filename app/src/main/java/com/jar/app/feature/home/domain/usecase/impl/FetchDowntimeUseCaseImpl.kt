package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.usecase.FetchDowntimeUseCase
import javax.inject.Inject

internal class FetchDowntimeUseCaseImpl @Inject constructor(private val homeRepository: HomeRepository) :
    FetchDowntimeUseCase {
    override suspend fun fetchDownTime() = homeRepository.fetchDownTime()
}