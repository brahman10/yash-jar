package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchVibaCardUseCase

internal class FetchVibaCardUseCaseImpl constructor(private val homeRepository: HomeRepository) :
        FetchVibaCardUseCase {
    override suspend fun fetchVibaCardDetails() = homeRepository.fetchVibaCardDetails()

}