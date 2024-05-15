package com.jar.health_insurance.impl.ui.add_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchAddDetailsScreenStaticDataUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchIncompleteProposalUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.InitiateInsurancePlanUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDetailsViewModel @Inject constructor(
    private val fetchAddDetailsScreenStaticDataUseCase: FetchAddDetailsScreenStaticDataUseCase,
    private val fetchIncompleteProposalUseCase: FetchIncompleteProposalUseCase,
    private val initiateInsurancePlanUseCase: InitiateInsurancePlanUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDetailsFragmentState())
    val uiState = _uiState.asStateFlow()

    fun onTriggerEvent(eventType: AddDetailsFragmentEvents) {
        when (eventType) {
            is AddDetailsFragmentEvents.OnMaximumAgeChanged -> onMaximumAgeChanged(eventType.maximumAgeEntered)

            is AddDetailsFragmentEvents.OnSelectedMembersChanged -> onSelectedMembersChanged(
                eventType.selectedMembers
            )

            is AddDetailsFragmentEvents.OnGoodHealthDeclarationCheckedChanged -> onGoodHealthDeclarationCheckedChanged(
                eventType.isChecked
            )

            is AddDetailsFragmentEvents.OnLoadData -> onLoadingScreenData()

            is AddDetailsFragmentEvents.OnNextButtonClicked -> onNextButtonClicked(eventType.action)

            is AddDetailsFragmentEvents.OnSubmittingWrongAge -> onSubmittingWrongAge()

            is AddDetailsFragmentEvents.ErrorMessageDisplayed -> clearErrorMessage()

            is AddDetailsFragmentEvents.OnScreenDataLoading -> onScreenDataLoadingStatusChanged(
                eventType.isScreenLoading
            )
        }
    }

    private fun onScreenDataLoadingStatusChanged(isScreenDataLoading: Boolean) {
        _uiState.update {
            it.copy(
                isScreenDataLoading = isScreenDataLoading
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

    private fun onSubmittingWrongAge() {
        _uiState.update {
            it.copy(
                isWrongAgeError = true
            )
        }
    }

    private fun onNextButtonClicked(action: (String) -> Unit) {
        val maxAge = _uiState.value.maximumAgeEntered.toInt()

        val kidsCnt = getKidsCnt(_uiState.value.selectedMembers)

        val adultCnt = getAdultCnt(_uiState.value.selectedMembers)

        if (maxAge > 45) {
            onTriggerEvent(AddDetailsFragmentEvents.OnSubmittingWrongAge)
        } else {
            viewModelScope.launch {
                onTriggerEvent(AddDetailsFragmentEvents.OnScreenDataLoading(true))
                initiateInsurancePlanUseCase.initiateInsurancePlan(
                    maxAge,
                    adultCnt,
                    kidsCnt
                ).collect(
                    onSuccess = { initiateInsurancePlanResponse ->
                        action(initiateInsurancePlanResponse.orderId)
                        onTriggerEvent(AddDetailsFragmentEvents.OnScreenDataLoading(false))
                    },
                    onError = { errorMessage, _ ->
                        _uiState.update {
                            it.copy(
                                errorMessage = errorMessage,
                                isScreenDataLoading = false
                            )
                        }
                    }
                )
            }
        }
    }

    fun getKidsCnt(selectedMemberIndexList: List<Int>): Int {
        return when {

            selectedMemberIndexList.contains(2) && selectedMemberIndexList.contains(3) -> 2

            !selectedMemberIndexList.contains(2) && !selectedMemberIndexList.contains(3) -> 0

            else -> 1
        }
    }

    fun getAdultCnt(selectedMembers: List<Int>): Int {
        return if (selectedMembers.contains(1)) 2 else 1
    }

    private fun onLoadingScreenData() {
        onTriggerEvent(AddDetailsFragmentEvents.OnScreenDataLoading(true))
        viewModelScope.launch {
            fetchIncompleteProposalUseCase.fetchIncompleteProposal()
                .collect(
                    onSuccess = { incompleteProposalResponse ->
                        val age = incompleteProposalResponse.age
                        fetchAddDetailsScreenStaticDataUseCase.fetchAddDetailsScreenStaticData()
                            .collect(
                                onSuccess = { staticDataResponse ->
                                    _uiState.update {
                                        it.copy(
                                            maximumAgeEntered = age.orEmpty(),
                                            selectedMembers = staticDataResponse.defaultMembers.orEmpty(),
                                            isGoodHealthDeclarationChecked = false,
                                            addDetailsScreenStaticDataResponse = staticDataResponse,
                                            errorMessage = null,
                                            isWrongAgeError = false,
                                            isScreenDataLoading = false
                                        )
                                    }
                                },
                            )
                    },
                    onError = { errorMessage, _ ->
                        _uiState.update {
                            it.copy(
                                errorMessage = errorMessage,
                                isScreenDataLoading = false
                            )
                        }
                    }
                )
        }
    }


    private fun onGoodHealthDeclarationCheckedChanged(checked: Boolean) {
        _uiState.update {
            it.copy(
                isGoodHealthDeclarationChecked = checked
            )
        }
    }

    private fun onSelectedMembersChanged(selectedMembers: List<Int>) {
        _uiState.update {
            it.copy(
                selectedMembers = selectedMembers
            )
        }
    }

    private fun onMaximumAgeChanged(maximumAgeEntered: String) {
        _uiState.update {
            it.copy(
                maximumAgeEntered = maximumAgeEntered
            )
        }
    }
}