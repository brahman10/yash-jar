package com.jar.app.feature.home.ui.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHamburgerMenuItemsUseCase
import com.jar.app.feature_homepage.shared.ui.SideNavDrawerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SideNavDrawerViewModelAndroid @Inject constructor(
    private val fetchHamburgerMenuItemsUseCase: FetchHamburgerMenuItemsUseCase
): ViewModel() {

    private val viewModel by lazy {
        SideNavDrawerViewModel(
            fetchHamburgerMenuItemsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}