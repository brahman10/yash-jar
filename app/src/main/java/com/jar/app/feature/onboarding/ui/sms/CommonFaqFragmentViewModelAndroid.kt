package com.jar.app.feature.onboarding.ui.sms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchFaqStaticDataUseCase
import com.jar.app.feature_onboarding.shared.ui.common_faq.CommonFaqViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CommonFaqFragmentViewModelAndroid @Inject constructor(
    fetchFaqStaticDataUseCase: FetchFaqStaticDataUseCase
) : ViewModel() {

    private val viewModel by lazy {
        CommonFaqViewModel(
            fetchFaqStaticDataUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}