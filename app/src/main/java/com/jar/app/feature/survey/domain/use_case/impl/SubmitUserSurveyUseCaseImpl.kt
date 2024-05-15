package com.jar.app.feature.survey.domain.use_case.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.survey.domain.use_case.SubmitUserSurveyUseCase
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject

internal class SubmitUserSurveyUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository
) : SubmitUserSurveyUseCase {

    override suspend fun submitSurvey(jsonElement: JsonElement) =
        homeRepository.submitSurvey(jsonElement)
}