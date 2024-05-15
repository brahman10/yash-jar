package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchBottomNavStickyCardDataUseCase

internal class FetchBottomNavStickyCardDataUseCaseImpl constructor(
    private val homeRepository: HomeRepository,
) : FetchBottomNavStickyCardDataUseCase {
    override suspend fun fetchBottomNavStickyCardData() = homeRepository.fetchBottomNavStickyCardData()
}