package com.jar.app.feature_lending.shared.api.impl.use_case

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase

internal class FetchLendingV2PreApprovedDataUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchLendingV2PreApprovedDataUseCase {

    override suspend fun fetchPreApprovedData() = lendingRepository.fetchPreApprovedData()

}