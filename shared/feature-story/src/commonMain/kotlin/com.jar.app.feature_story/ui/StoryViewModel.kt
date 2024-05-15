package com.jar.app.feature_story.ui

import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.app.feature_story.domain.use_cases.FetchInAppStoriesUseCase
import com.jar.app.feature_story.domain.use_cases.FetchPageByPageIdUseCase
import com.jar.app.feature_story.domain.use_cases.UpdateUserActionUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel constructor(
    private val fetchInAppStoriesUseCase: FetchInAppStoriesUseCase,
    private val updateUserActionUseCase: UpdateUserActionUseCase,
    private val fetchStoryByPageId: FetchPageByPageIdUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var savedCurrentPosition = 0


    private val _inAppStoryFlow: MutableStateFlow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>> =
        MutableStateFlow(
            RestClientResult.loading()
        )
    val inAppStoryFlow: StateFlow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>> =
        _inAppStoryFlow
    var inAppStoryData: InAppStoryModel? = null

    fun fetchStories(storyId: String? = null) {
        if (storyId != null) {
            viewModelScope.launch {
                fetchStoryByPageId.fetchStoryById(storyId).collect {
                    _inAppStoryFlow.value = it
                }
            }
        } else {
            viewModelScope.launch {
                fetchInAppStoriesUseCase.fetchInAppStories().collect {
                    _inAppStoryFlow.value = it
                }
            }
        }

    }

    fun updateUserAction(
        userAction: String,
        actionValue: Boolean,
        pageId: String,
        timeSpent: Long?
    ) {
        viewModelScope.launch {
            updateUserActionUseCase.updateUserActionUseCase(
                userActionType = userAction,
                action = actionValue,
                pageId = pageId,
                timeSpent
            ).collect(
                onLoading = {

                }, onSuccess = {

                }, onError = { error, errorCode ->

                }
            )
        }
    }
}

enum class UserAction(val value: String) {
    LIKE("Like"),
    VIEW("View"),
    DOWNLOAD("Download"),
    SHARE("Share"),
    CTA("CTA"),
    TIMESPENT("TimeSpent")
}