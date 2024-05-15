package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase

internal class UpdateLoanDetailsV2CaseImpl constructor(
    private val lendingRepository: LendingRepository
) : UpdateLoanDetailsV2UseCase {

    override suspend fun updateLoanDetails(updateLoanDetailsBody: UpdateLoanDetailsBodyV2, checkPoint: String) =
        lendingRepository.updateLoanDetails(updateLoanDetailsBody, checkPoint)

}