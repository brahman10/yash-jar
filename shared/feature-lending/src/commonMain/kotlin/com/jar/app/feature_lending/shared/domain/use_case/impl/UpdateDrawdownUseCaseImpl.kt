package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.temp.DrawdownRequest
import com.jar.app.feature_lending.shared.domain.use_case.UpdateDrawdownUseCase

internal class UpdateDrawdownUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : UpdateDrawdownUseCase {

    override suspend fun updateDrawdown(loanId: String, drawdownRequest: DrawdownRequest) =
        lendingRepository.updateDrawdown(loanId, drawdownRequest)

}