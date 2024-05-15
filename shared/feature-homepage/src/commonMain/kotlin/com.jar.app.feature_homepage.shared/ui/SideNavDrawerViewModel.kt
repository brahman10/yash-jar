package com.jar.app.feature_homepage.shared.ui

import com.jar.app.feature_homepage.shared.domain.model.hamburger.HamburgerData
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHamburgerMenuItemsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SideNavDrawerViewModel constructor(
    private val fetchHamburgerMenuItemsUseCase: FetchHamburgerMenuItemsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _hamburgerMenuFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<HamburgerData?>>>(
            RestClientResult.none()
        )
    val hamburgerMenuFlow: CStateFlow<RestClientResult<ApiResponseWrapper<HamburgerData?>>>
        get() = _hamburgerMenuFlow.toCommonStateFlow()

    fun fetchHamburgerMenuItems() {
        viewModelScope.launch {
            fetchHamburgerMenuItemsUseCase.fetchHamburgerData().collect {
                _hamburgerMenuFlow.emit(it)
            }
        }
    }

}