package com.jar.health_insurance.impl.ui.add_details

import androidx.compose.runtime.derivedStateOf
import com.jar.app.base.util.toIntOrZero
import com.jar.app.feature_health_insurance.shared.data.models.add_details.AddDetailsScreenStaticDataResponse

data class AddDetailsFragmentState(
    val maximumAgeEntered: String = "",
    val selectedMembers: List<Int> = emptyList(),
    val isGoodHealthDeclarationChecked: Boolean = false,
    val addDetailsScreenStaticDataResponse: AddDetailsScreenStaticDataResponse? = null,
    val isWrongAgeError: Boolean = false,
    val errorMessage: String? = null,
    val isScreenDataLoading: Boolean = true,
) {
    val isButtonEnabled = derivedStateOf {
        isGoodHealthDeclarationChecked && (maximumAgeEntered.toIntOrZero() >= 18) && (maximumAgeEntered.toIntOrZero() <= 45) && isValidMembers(
            selectedMembers
        )
    }

    val shouldKidsPolicyBeShown = derivedStateOf {
        (selectedMembers.contains(2) || selectedMembers.contains(3)) && !selectedMembers.contains(1)
    }

    val shouldHintForMyselfAndSpouseBeShown = derivedStateOf {
        selectedMembers.contains(1)
    }

    val isKidSelected = derivedStateOf {
        selectedMembers.contains(2) || selectedMembers.contains(3)
    }

    private fun isValidMembers(selectedMembers: List<Int>): Boolean {
        return if (selectedMembers.contains(2) || selectedMembers.contains(3)) {
            selectedMembers.contains(1)
        } else {
            true
        }
    }
}

sealed class AddDetailsFragmentEvents {
    data class OnMaximumAgeChanged(val maximumAgeEntered: String) : AddDetailsFragmentEvents()
    data class OnSelectedMembersChanged(val selectedMembers: List<Int>) : AddDetailsFragmentEvents()
    data class OnGoodHealthDeclarationCheckedChanged(val isChecked: Boolean) :
        AddDetailsFragmentEvents()

    object OnLoadData : AddDetailsFragmentEvents()
    data class OnNextButtonClicked(val action: (String) -> Unit) : AddDetailsFragmentEvents()
    object OnSubmittingWrongAge : AddDetailsFragmentEvents()
    object ErrorMessageDisplayed : AddDetailsFragmentEvents()
    data class OnScreenDataLoading(val isScreenLoading: Boolean) : AddDetailsFragmentEvents()
}