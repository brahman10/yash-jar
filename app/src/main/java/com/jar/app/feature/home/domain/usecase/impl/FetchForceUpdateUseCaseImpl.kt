package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.usecase.FetchForceUpdateUseCase
import javax.inject.Inject

class FetchForceUpdateUseCaseImpl @Inject constructor(private val homeRepository: HomeRepository):FetchForceUpdateUseCase {
    override suspend fun fetchForceUpdateData() = homeRepository.fetchForceUpdateData()
}