package com.jar.app.feature.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHamburgerMenuItemsUseCase
import com.jar.app.feature_homepage.shared.domain.model.hamburger.HamburgerData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
//import com.jar.internal.library.jar_core_network.api.util.checkIfAnyRestClientIsLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComposeViewModelState(
    val hamburgerData: RestClientResult<ApiResponseWrapper<HamburgerData?>> = RestClientResult.none(),
    val hamburgerData2: RestClientResult<ApiResponseWrapper<HamburgerData?>> = RestClientResult.none()
)

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val fetchUseCase: FetchHamburgerMenuItemsUseCase,
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _uiStateFlow = MutableStateFlow<ComposeViewModelState>(ComposeViewModelState())
    val uiStateFlow: StateFlow<ComposeViewModelState>
        get() = _uiStateFlow.asStateFlow()

    //    private val _combinedFlowLoading = _uiStateFlow.transform {
//        emit(checkIfAnyRestClientIsLoading(it.hamburgerData, it.hamburgerData2))
//    }
//    val combinedFlowLoading: Flow<Boolean> = _combinedFlowLoading
    init {
        testAPI()
        testAPI2()
    }

    fun getMessage() {
        viewModelScope.launch {
            _message.postValue("Loading...")
            delay(2000L)
            _message.postValue("Hi from compose view model.")
        }
    }

    fun testAPI() {
        viewModelScope.launch {
            fetchUseCase.fetchHamburgerData().collectLatest { data ->
                _uiStateFlow.update {
                    it.copy(hamburgerData = data)
                }
            }
        }
    }

    fun testAPI2() {
        viewModelScope.launch {
            fetchUseCase.fetchHamburgerData().collectLatest { data ->
                _uiStateFlow.update {
                    it.copy(hamburgerData2 = data)
                }
            }
        }
    }
}