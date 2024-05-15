package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.usecase.CaptureAppOpensUseCase

internal class CaptureAppOpensUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : CaptureAppOpensUseCase {
    override suspend fun captureAppOpens() = homeRepository.captureAppOpens()
}