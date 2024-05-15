package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.ValidateIfscCodeUseCase

internal class ValidateIfscCodeUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
): ValidateIfscCodeUseCase {

    override suspend fun validateIfscCode(code: String) =
        lendingRepository.validateIfscCode(code)

}