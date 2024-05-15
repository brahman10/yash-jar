package com.jar.app.feature_jar_duo.shared.domain.use_case.impl

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepository
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroData
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchDuoIntroStoryUseCase
import kotlinx.coroutines.flow.Flow

internal class FetchDuoIntroStoryUseCaseImpl constructor(
    private val duoRepository: DuoRepository
) : FetchDuoIntroStoryUseCase {
    override suspend fun fetchDuoIntroStory(): Flow<RestClientResult<ApiResponseWrapper<DuoIntroData>>> =
        duoRepository.fetchDuoIntroStory()

}