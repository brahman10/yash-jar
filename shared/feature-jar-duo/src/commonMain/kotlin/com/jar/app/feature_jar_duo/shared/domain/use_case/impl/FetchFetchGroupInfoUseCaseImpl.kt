package com.jar.app.feature_jar_duo.shared.domain.use_case.impl

import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepository
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupInfoUseCase

internal class FetchFetchGroupInfoUseCaseImpl constructor(private val duoRepository: DuoRepository) :
    FetchGroupInfoUseCase {
    override suspend fun fetchGroupInfo(groupId: String?) = duoRepository.fetchGroupInfo(groupId)

    override suspend fun fetchGroupInfoV2(groupId: String?)  = duoRepository.fetchGroupInfoV2(groupId)

}