package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUserGoldBreakdownUseCase

internal class FetchUserGoldBreakdownUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchUserGoldBreakdownUseCase {

    override suspend fun fetchUserGoldBreakdown() = homeRepository.fetchUserGoldBreakdown()

}