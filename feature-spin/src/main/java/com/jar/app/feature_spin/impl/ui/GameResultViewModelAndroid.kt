package com.jar.app.feature_spin.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_spin.shared.domain.usecase.FetchJackpotOutComeDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinIntroUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinsResultDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchUseWinningUseCase
import com.jar.app.feature_spin.shared.domain.usecase.SpinFlatOutcomeUseCase
import com.jar.app.feature_spin.shared.ui.GameResultViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GameResultViewModelAndroid @Inject constructor(
    private val fetchSpinDataUseCase: FetchSpinDataUseCase,
    private val fetchSpinsResultDataUseCase: FetchSpinsResultDataUseCase,
    private val fetchSpinIntroUseCase: FetchSpinIntroUseCase,
    private val fetchFlatOutComeUseCase: SpinFlatOutcomeUseCase,
    private val fetchJackpotOutComeUseCase: FetchJackpotOutComeDataUseCase,
    private val fetchUseWinningUseCase: FetchUseWinningUseCase,
    private val prefs: PrefsApi
) : ViewModel() {

    private val viewModel by lazy {
        GameResultViewModel(
            fetchSpinDataUseCase,
            fetchSpinsResultDataUseCase,
            fetchSpinIntroUseCase,
            fetchFlatOutComeUseCase,
            fetchJackpotOutComeUseCase,
            fetchUseWinningUseCase,
            prefs,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}