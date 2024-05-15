package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHamburgerMenuItemsUseCase

internal class FetchHamburgerMenuItemsUseCaseImpl constructor(
    private val homeRepository: HomeRepository,
) : FetchHamburgerMenuItemsUseCase {
    override suspend fun fetchHamburgerData() = homeRepository.fetchHamburgerData()
}