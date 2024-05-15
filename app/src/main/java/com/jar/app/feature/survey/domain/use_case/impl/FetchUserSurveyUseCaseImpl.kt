package com.jar.app.feature.survey.domain.use_case.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.survey.domain.use_case.FetchUserSurveyUseCase
import javax.inject.Inject

internal class FetchUserSurveyUseCaseImpl @Inject constructor(private val homeRepository: HomeRepository) : FetchUserSurveyUseCase {
    override suspend fun fetchUserSurvey() = homeRepository.fetchUserSurvey()
}