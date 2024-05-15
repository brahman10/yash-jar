package com.jar.app.feature_homepage.impl.ui.help_videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHelpVideosUseCase
import com.jar.app.feature_homepage.shared.ui.help_videos.HelpVideosViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HelpVideosViewModelAndroid @Inject constructor(
    private val fetchHelpVideosUseCase: FetchHelpVideosUseCase
) : ViewModel() {

    private val viewModel by lazy {
        HelpVideosViewModel(
            fetchHelpVideosUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}