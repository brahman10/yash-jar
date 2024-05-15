package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchFaqStaticDataUseCase

class FetchFaqStaticDataUseCaseImpl constructor(
    private val loginRepository: LoginRepository
): FetchFaqStaticDataUseCase {

    override suspend fun fetchFaqStaticData(staticContentType: BaseConstants.StaticContentType) = loginRepository.fetchDashboardStaticContent(staticContentType)
}