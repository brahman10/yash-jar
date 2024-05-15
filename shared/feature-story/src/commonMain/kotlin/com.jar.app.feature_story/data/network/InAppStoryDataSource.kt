package com.jar.app.feature_story.data.network

import com.jar.app.core_network.CoreNetworkBuildKonfig.BASE_STORY_URL_KTOR
import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.host
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.url

class InAppStoryDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    private val hostName = BASE_STORY_URL_KTOR
    private val inAppStoryUrl = "/feed/api/v1/user/user_story"
    private val fetchPageIdUrl = "/feed/api/v1/user/shared_user_story"
    private val updateUserAction = "/feed/api/v1/user/user_action"

    suspend fun getInAppStories() = getResult<ApiResponseWrapper<InAppStoryModel>> {
        client.get {
            host = hostName
            url(inAppStoryUrl)
        }
    }

    suspend fun updateUserAction(userActionType: String,
                                 action: Boolean,
                                 pageId: String,
                                 timeSpent:Long?
    ) = getResult<ApiResponseWrapper<Unit?>> {

        client.put {
            host = hostName
            url(updateUserAction)
            parameter("user_action_type", userActionType)
            parameter("action", action)
            parameter("page_id", pageId)
            parameter("time_spent",timeSpent)
        }
    }
    suspend fun fetchPageByStoryId(pageId: String) = getResult<ApiResponseWrapper<InAppStoryModel>> {
        client.get {
            host = hostName
            url(fetchPageIdUrl)
            parameter("page_id", pageId)
        }
    }

}