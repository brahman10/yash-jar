package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.temp.LendingAddress
import com.jar.app.feature_lending.shared.domain.use_case.UpdateAddressDetailsUseCase

internal class UpdateAddressDetailsUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
): UpdateAddressDetailsUseCase {

    override suspend fun updateAddressDetails(lendingAddress: LendingAddress) = lendingRepository.updateAddressDetails(lendingAddress)

}