package com.jar.app.feature_homepage.shared.domain.use_case.impl
import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinHomeScreenDataUseCase

internal class FetchFirstCoinHomeScreenDataUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchFirstCoinHomeScreenDataUseCase {
    override suspend fun fetchFirstCoinHomeScreenData() =
        homeRepository.fetchFirstCoinHomeScreenData()
}