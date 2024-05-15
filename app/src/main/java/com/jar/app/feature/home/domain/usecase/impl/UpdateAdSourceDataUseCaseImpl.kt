package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.model.AdSourceData
import com.jar.app.feature.home.domain.usecase.UpdateAdSourceDataUseCase
import javax.inject.Inject

internal class UpdateAdSourceDataUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository
) : UpdateAdSourceDataUseCase {

    override suspend fun updateAdSourceData(adSourceData: AdSourceData) =
        homeRepository.updateAdSourceData(adSourceData)
}