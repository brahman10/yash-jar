package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchVasooliOverviewUseCase

internal class FetchVasooliOverviewUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): FetchVasooliOverviewUseCase {

    override suspend fun fetchVasooliOverview() = vasooliRepository.fetchVasooliOverview()

}