package com.jar.app.feature_in_app_stories.impl.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_story.domain.use_cases.FetchInAppStoriesUseCase
import com.jar.app.feature_story.domain.use_cases.FetchPageByPageIdUseCase
import com.jar.app.feature_story.domain.use_cases.UpdateUserActionUseCase
import com.jar.app.feature_story.ui.StoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class StoryViewModelAndroid @Inject constructor(
    private val fetchInAppStoriesUseCase: FetchInAppStoriesUseCase,
    private val updateUserActionUseCase: UpdateUserActionUseCase,
    private val fetchStoryByPageId: FetchPageByPageIdUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        StoryViewModel(
            fetchInAppStoriesUseCase,
            updateUserActionUseCase,
            fetchStoryByPageId,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}
