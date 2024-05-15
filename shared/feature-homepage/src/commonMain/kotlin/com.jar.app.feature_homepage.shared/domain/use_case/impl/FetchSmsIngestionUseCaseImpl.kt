package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchSmsIngestionUseCase

internal class FetchSmsIngestionUseCaseImpl constructor(private val homeRepository: HomeRepository) :
    FetchSmsIngestionUseCase {
    override suspend fun shouldSendSmsOnDemand() = homeRepository.shouldSendSmsOnDemand()
}