package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpdateFirstCoinOrderIdUseCase

internal class FetchUpdateFirstCoinOrderIdUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchUpdateFirstCoinOrderIdUseCase {

    override suspend fun updateFirstCoinDeliveryStatus(orderId:String) =
        homeRepository.updateFirstCoinDeliveryStatus(orderId)
}