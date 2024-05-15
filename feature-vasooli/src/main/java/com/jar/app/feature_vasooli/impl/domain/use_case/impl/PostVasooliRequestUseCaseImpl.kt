package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.model.VasooliEntryRequest
import com.jar.app.feature_vasooli.impl.domain.use_case.PostVasooliRequestUseCase

internal class PostVasooliRequestUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): PostVasooliRequestUseCase {

    override suspend fun postVasooliRequest(vasooliEntryRequest: VasooliEntryRequest) =
        vasooliRepository.postVasooliRequest(vasooliEntryRequest)

}