package com.jar.app.feature_profile.domain.use_case.impl

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_profile.data.repository.UserRepository
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase

internal class FetchDashboardStaticContentUseCaseImpl constructor(
    private val userRepository: UserRepository,
) : FetchDashboardStaticContentUseCase {

    override suspend fun fetchDashboardStaticContent(staticContentType: BaseConstants.StaticContentType) =
        userRepository.fetchDashboardStaticContent(staticContentType)

}