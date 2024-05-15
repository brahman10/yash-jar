package com.jar.app.feature_jar_duo.shared.domain.repository

import com.jar.app.feature_jar_duo.shared.data.network.DuoDataSource
import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepository
import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepositoryExternal

internal class DuoRepositoryImpl constructor(
    private val duoDataSource: DuoDataSource,
) : DuoRepository,
    DuoRepositoryExternal {

    override suspend fun fetchGroupList() = getFlowResult {
        duoDataSource.fetchGroupList()
    }

    override suspend fun fetchGroupInfo(groupId: String?) = getFlowResult {
        duoDataSource.fetchGroupInfo(groupId)
    }

    override suspend fun fetchGroupInfoV2(groupId: String?) = getFlowResult {
        duoDataSource.fetchGroupInfoV2(groupId)
    }

    override suspend fun renameGroup(
        groupId: String,
        groupName: String
    ) = getFlowResult {
        duoDataSource.renameGroup(groupId, groupName)
    }

    override suspend fun deleteGroup(
        groupId: String
    ) = getFlowResult {
        duoDataSource.deleteGroup(groupId)
    }

    override suspend fun fetchDuoIntroStory() = getFlowResult { duoDataSource.fetchDuoIntroStory() }
}