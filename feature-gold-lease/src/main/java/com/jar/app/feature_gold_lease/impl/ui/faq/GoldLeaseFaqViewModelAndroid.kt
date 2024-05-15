package com.jar.app.feature_gold_lease.impl.ui.faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseFaqsUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseFaqViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
internal class GoldLeaseFaqViewModelAndroid @Inject constructor(
    fetchGoldLeaseFaqsUseCase: FetchGoldLeaseFaqsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseFaqViewModel(
            fetchGoldLeaseFaqsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}