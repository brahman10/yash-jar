package com.jar.app.weekly_magic_common.impl.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeOnBoardedUseCase
import com.jar.app.feature_weekly_magic_common.shared.ui.OnBoardingWeeklyChallengeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OnBoardingWeeklyChallengeViewModelAndroid @Inject constructor(
    markWeeklyChallengeOnBoardedUseCase: MarkWeeklyChallengeOnBoardedUseCase
) : ViewModel() {

    private val viewModel by lazy {
        OnBoardingWeeklyChallengeViewModel(
            markWeeklyChallengeOnBoardedUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}