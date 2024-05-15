package com.jar.app.feature_lending.impl.ui.personal_details.employment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.personal_details.employment.LendingEmploymentDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingEmploymentDetailsViewModelAndroid @Inject constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
) : ViewModel() {

    var isPickerShownOnce: Boolean = false

    var minSalaryAllowed = LendingEmploymentDetailsFragment.MIN_SALARY_DEFAULT
    var maxSalaryAllowed = LendingEmploymentDetailsFragment.MAX_SALARY_DEFAULT

    private val viewModel by lazy {
        LendingEmploymentDetailsViewModel(
            updateLoanDetailsV2UseCase = updateLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}