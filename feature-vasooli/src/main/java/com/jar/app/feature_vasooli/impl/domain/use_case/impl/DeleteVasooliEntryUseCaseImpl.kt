package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.use_case.DeleteVasooliEntryUseCase

internal class DeleteVasooliEntryUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): DeleteVasooliEntryUseCase {

    override suspend fun deleteVasooliEntry(loanId: String) =
        vasooliRepository.deleteVasooliEntry(loanId)
}