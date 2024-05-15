package com.jar.app.feature_post_setup.domain.use_case.impl

import com.jar.app.feature_post_setup.data.repository.PostSetupRepository
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupCalenderDataUseCase

internal class FetchPostSetupCalenderDataUseCaseImpl constructor(
    private val postSetupRepository: PostSetupRepository
) : FetchPostSetupCalenderDataUseCase {
    override suspend fun fetchPostSetupCalendarData(
        startDate: String,
        endDate: String
    ) = postSetupRepository.fetchPostSetupCalendarData(startDate, endDate)
}