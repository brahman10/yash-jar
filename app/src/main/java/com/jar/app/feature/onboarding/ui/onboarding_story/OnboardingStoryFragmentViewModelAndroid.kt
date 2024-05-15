package com.jar.app.feature.onboarding.ui.onboarding_story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOnboardingStoriesUseCase
import com.jar.app.feature_onboarding.shared.ui.onboarding_story.OnboardingStoryFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OnboardingStoryFragmentViewModelAndroid @Inject constructor(
    private val fetchOnboardingStoriesUseCase: FetchOnboardingStoriesUseCase
) : ViewModel() {

    private val viewModel by lazy {
        OnboardingStoryFragmentViewModel(
            fetchOnboardingStoriesUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}