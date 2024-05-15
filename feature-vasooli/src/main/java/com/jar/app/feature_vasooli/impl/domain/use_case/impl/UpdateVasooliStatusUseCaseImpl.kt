package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.model.UpdateStatusRequest
import com.jar.app.feature_vasooli.impl.domain.use_case.UpdateVasooliStatusUseCase

internal class UpdateVasooliStatusUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): UpdateVasooliStatusUseCase{

    override suspend fun updateVasooliStatus(updateStatusRequest: UpdateStatusRequest) =
        vasooliRepository.updateVasooliStatus(updateStatusRequest)

}