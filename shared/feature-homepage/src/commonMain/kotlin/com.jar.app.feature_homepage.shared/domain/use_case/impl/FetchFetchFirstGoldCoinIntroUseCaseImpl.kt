package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstGoldCoinIntroUseCase

internal class FetchFetchFirstGoldCoinIntroUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchFirstGoldCoinIntroUseCase {
    override suspend fun fetchFirstGoldCoinIntro() = homeRepository.fetchFirstGoldCoinIntro()
}