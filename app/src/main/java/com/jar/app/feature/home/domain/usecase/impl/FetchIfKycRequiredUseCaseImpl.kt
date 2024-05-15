package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.usecase.FetchIfKycRequiredUseCase
import javax.inject.Inject


class FetchIfKycRequiredUseCaseImpl @Inject constructor(private val homeRepository: HomeRepository) :
    FetchIfKycRequiredUseCase {
    override suspend fun fetchIfKycRequired() = homeRepository.fetchIfKycIsRequired()

}