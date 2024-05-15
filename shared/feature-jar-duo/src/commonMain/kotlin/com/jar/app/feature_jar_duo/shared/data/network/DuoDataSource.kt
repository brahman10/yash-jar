package com.jar.app.feature_jar_duo.shared.data.network

import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData
import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupInfo
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroData
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupInfoV2
import com.jar.app.feature_jar_duo.shared.util.DuoConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

class DuoDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {


    suspend fun fetchGroupList() = getResult<ApiResponseWrapper<List<DuoGroupData>>> {
        client.get {
            url(Endpoints.FETCH_GROUP_LIST)
        }
    }


    suspend fun fetchGroupInfo(groupId: String?) = getResult<ApiResponseWrapper<DuoGroupInfo>> {
        client.get {
            url(Endpoints.FETCH_GROUP_INFO)
            if (groupId.isNullOrBlank().not()) parameter("groupId", groupId)
        }
    }

    suspend fun fetchGroupInfoV2(
        groupId: String?
    ) = getResult<ApiResponseWrapper<DuoGroupInfoV2>> {
        client.get {
            url(Endpoints.FETCH_GROUP_INFO_V2)
            parameter("groupId", groupId)
        }
    }

    suspend fun renameGroup(
        groupId: String, groupName: String
    ) = getResult<ApiResponseWrapper<Unit?>> {
        client.put {
            url(Endpoints.RENAME_GROUP)
            parameter("groupId", groupId)
            parameter("groupName", groupName)
        }
    }

    suspend fun deleteGroup(groupId: String) = getResult<ApiResponseWrapper<Unit?>> {
        client.put {
            url(Endpoints.DELETE_GROUP)
            parameter("groupId", groupId)
        }
    }


    suspend fun fetchDuoIntroStory() =
        getResult<ApiResponseWrapper<DuoIntroData>> {
            client.get {
                url(Endpoints.FETCH_DUO_STORY_INTRO)
            }
        }
}