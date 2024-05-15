package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchNewImageUseCase

internal class FetchNewImageUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): FetchNewImageUseCase {

    override suspend fun fetchNewImage(ignoreIndex: String) =
        vasooliRepository.fetchNewImage(ignoreIndex)

}