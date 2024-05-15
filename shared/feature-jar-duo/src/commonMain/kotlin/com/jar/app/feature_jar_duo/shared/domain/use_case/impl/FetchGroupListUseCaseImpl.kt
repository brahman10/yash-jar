package com.jar.app.feature_jar_duo.shared.domain.use_case.impl

import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepositoryExternal
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupListUseCase

class FetchGroupListUseCaseImpl constructor(private val duoRepository: DuoRepositoryExternal) :
    FetchGroupListUseCase {
    override suspend fun fetchGroupList() = duoRepository.fetchGroupList()
}