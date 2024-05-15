package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.model.UpdateEntryRequest
import com.jar.app.feature_vasooli.impl.domain.use_case.UpdateVasooliEntryUseCase

internal class UpdateVasooliEntryUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): UpdateVasooliEntryUseCase {

    override suspend fun updateVasooliEntry(updateEntryRequest: UpdateEntryRequest) =
        vasooliRepository.updateVasooliEntry(updateEntryRequest)

}