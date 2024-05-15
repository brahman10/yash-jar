package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.usecase.FetchPublicStaticContentUseCase
import javax.inject.Inject

internal class FetchPublicStaticContentUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository,
) : FetchPublicStaticContentUseCase {

    override suspend fun fetchPublicStaticContent(
        staticContentType: BaseConstants.StaticContentType,
        phoneNumber: String,
        context: String?
    ) =
        homeRepository.fetchPublicStaticContent(staticContentType, phoneNumber,context)

}