package com.jar.app.feature_weekly_magic.impl.ui.info_bottom_sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_weekly_magic.shared.domain.usecase.FetchWeeklyChallengeInfoUseCase
import com.jar.app.feature_weekly_magic.shared.domain.usecase.MarkWeeklyChallengeInfoAsViewedUseCase
import com.jar.app.feature_weekly_magic.shared.ui.WeeklyChallengeInfoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WeeklyChallengeInfoViewModelAndroid @Inject constructor(
    fetchWeeklyChallengeInfoUseCase: FetchWeeklyChallengeInfoUseCase,
    markWeeklyChallengeInfoAsViewedUseCase: MarkWeeklyChallengeInfoAsViewedUseCase
): ViewModel(){

    private val viewModel by lazy {
        WeeklyChallengeInfoViewModel(
            fetchWeeklyChallengeInfoUseCase,
            markWeeklyChallengeInfoAsViewedUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}