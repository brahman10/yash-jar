package com.jar.app.feature_jar_duo.shared.domain.use_case.impl

import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepository
import com.jar.app.feature_jar_duo.shared.domain.use_case.DeleteGroupUseCase

internal class DeleteGroupUseCaseImpl constructor(private val duoRepository: DuoRepository) :
    DeleteGroupUseCase {
    override suspend fun deleteGroup(
        groupId: String
    ) = duoRepository.deleteGroup(groupId)
}