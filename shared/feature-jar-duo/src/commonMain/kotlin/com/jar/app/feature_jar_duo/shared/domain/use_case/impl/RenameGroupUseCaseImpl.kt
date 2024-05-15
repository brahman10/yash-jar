package com.jar.app.feature_jar_duo.shared.domain.use_case.impl

import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepository
import com.jar.app.feature_jar_duo.shared.domain.use_case.RenameGroupUseCase

internal class RenameGroupUseCaseImpl constructor(private val duoRepository: DuoRepository) :
    RenameGroupUseCase {
    override suspend fun renameGroup(
        groupId: String,
        groupName: String
    ) = duoRepository.renameGroup(groupId, groupName)

}