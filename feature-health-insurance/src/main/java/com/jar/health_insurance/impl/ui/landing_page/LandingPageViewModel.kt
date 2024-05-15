package com.jar.health_insurance.impl.ui.landing_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchLandingScreenDetailsUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingPageViewModel @Inject constructor(
    private val fetchLandingScreenDetailsUseCase: FetchLandingScreenDetailsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LandingPageState())
    val uiState = _uiState.asStateFlow()
    var apiExecutionCount: Int = 0


    fun onTriggerEvent(eventType: LandingPageEvent) {
        when (eventType) {
            LandingPageEvent.LoadLandingPageData -> loadData()
            LandingPageEvent.ErrorMessageDisplayed -> clearErrorMessage()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            fetchLandingScreenDetailsUseCase.fetchLandingScreenDetails()
                .collect(
                    onLoading = {
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    },
                    onSuccess = { response ->
                        _uiState.update {
                            it.copy(
                                landingPageData = response,
                                isLoading = false
                            )
                        }
                    },

                    onError = { errorMessage, errorCode ->
                        _uiState.update {
                            it.copy(
                                errorMessage = errorMessage,
                                isLoading = false
                            )
                        }
                    }
                )
        }
    }

    private fun clearErrorMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }
}