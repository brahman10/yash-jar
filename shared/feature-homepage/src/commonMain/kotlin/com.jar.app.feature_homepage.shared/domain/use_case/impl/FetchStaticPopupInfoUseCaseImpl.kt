package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchStaticPopupInfoUseCase

internal class FetchStaticPopupInfoUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchStaticPopupInfoUseCase {

    override suspend fun fetchStaticPopupInfo(contentType: String) =
        homeRepository.fetchStaticPopupInfo(contentType)
}