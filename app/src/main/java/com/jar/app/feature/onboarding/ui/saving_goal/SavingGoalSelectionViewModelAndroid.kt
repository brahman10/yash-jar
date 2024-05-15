package com.jar.app.feature.onboarding.ui.saving_goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSavingGoalsUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSavingGoalsV2UseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchUserSavingPreferencesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.PostSavingGoalsUseCase
import com.jar.app.feature_onboarding.shared.ui.saving_goal.SavingGoalSelectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SavingGoalSelectionViewModelAndroid @Inject constructor(
    postSavingGoalsUseCase: PostSavingGoalsUseCase,
    fetchSavingGoalsUseCase: FetchSavingGoalsUseCase,
    fetchSavingGoalsV2UseCase: FetchSavingGoalsV2UseCase,
    fetchUserSavingPreferencesUseCase: FetchUserSavingPreferencesUseCase
) : ViewModel() {

    private val viewModel by lazy {
        SavingGoalSelectionViewModel(
            postSavingGoalsUseCase,
            fetchSavingGoalsUseCase,
            fetchSavingGoalsV2UseCase,
            fetchUserSavingPreferencesUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}