package com.jar.health_insurance.impl.ui.benefits_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchBenefitsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BenefitsScreenViewModel @Inject constructor(
    private val fetchBenefitsDetailsUseCase: FetchBenefitsDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BenefitsPageState())
    val uiState = _uiState.asStateFlow()

    fun onTriggerEvent(eventType: BenefitsEvent) {
        when (eventType) {
            is BenefitsEvent.OnCardExpanded -> onExpandCard(eventType.id)
            is BenefitsEvent.LoadBenefits -> LoadBenefits(eventType.insuranceId)
            BenefitsEvent.ErrorMessageDisplayed -> clearErrorMessage()
        }
    }

    private fun onExpandCard(cardId: String) {

        val benefitsList =
            _uiState.value.benefitsList

        benefitsList[cardId.toInt() - 1].isExpanded = !benefitsList[cardId.toInt() - 1].isExpanded
        _uiState.update {
            it.copy(benefitsList = benefitsList)
        }
    }

    private fun clearErrorMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }

    private fun LoadBenefits(insuranceId: String?) {
        viewModelScope.launch {
            fetchBenefitsDetailsUseCase.fetchBenefitsDetails(insuranceId)
                .collect(
                    onSuccess = {
                        val title = it.toolBarText
                        val initialBenefitsList =
                            it.benefitsList.apply { first().isExpanded = true }
                        _uiState.update {
                            it.copy(
                                benefitsList = initialBenefitsList,
                                toolBarTitle = title
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
}