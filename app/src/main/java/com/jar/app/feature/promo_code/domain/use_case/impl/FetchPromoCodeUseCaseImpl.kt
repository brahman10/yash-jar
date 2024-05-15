package com.jar.app.feature.promo_code.domain.use_case.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.promo_code.domain.use_case.FetchPromoCodeUseCase
import javax.inject.Inject

internal class FetchPromoCodeUseCaseImpl @Inject constructor(private val homeRepository: HomeRepository) :
    FetchPromoCodeUseCase {
    override suspend fun fetchPromoCode(page: Int, size: Int) =
        homeRepository.fetchPromoCode(page, size)
}