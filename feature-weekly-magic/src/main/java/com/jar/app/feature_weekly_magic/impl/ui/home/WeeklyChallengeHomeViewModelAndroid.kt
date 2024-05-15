package com.jar.app.feature_weekly_magic.impl.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_weekly_magic.shared.ui.WeeklyChallengeHomeViewModel
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeDetailUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WeeklyChallengeHomeViewModelAndroid @Inject constructor(
    fetchWeeklyChallengeDetailUseCase: FetchWeeklyChallengeDetailUseCase,
    fetchWeeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase,
    markWeeklyChallengeViewedUseCase: MarkWeeklyChallengeViewedUseCase,
): ViewModel(){

    private val viewModel by lazy {
        WeeklyChallengeHomeViewModel(
            fetchWeeklyChallengeDetailUseCase,
            fetchWeeklyChallengeMetaDataUseCase,
            markWeeklyChallengeViewedUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}