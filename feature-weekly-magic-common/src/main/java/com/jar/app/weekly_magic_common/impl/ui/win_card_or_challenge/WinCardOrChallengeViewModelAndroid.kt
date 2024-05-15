package com.jar.app.weekly_magic_common.impl.ui.win_card_or_challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeDetailUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCase
import com.jar.app.feature_weekly_magic_common.shared.ui.WinCardOrChallengeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WinCardOrChallengeViewModelAndroid @Inject constructor(
    private val fetchWeeklyChallengeDetailUseCase: FetchWeeklyChallengeDetailUseCase,
    private val markWeeklyChallengeViewedUseCase: MarkWeeklyChallengeViewedUseCase,
): ViewModel(){

    private val viewModel by lazy {
        WinCardOrChallengeViewModel(
            fetchWeeklyChallengeDetailUseCase,
            markWeeklyChallengeViewedUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}