package com.jar.app.feature_settings.impl.ui.payment_methods.dialog_add_upi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_settings.domain.use_case.FetchVpaChipUseCase
import com.jar.app.feature_settings.domain.use_case.VerifyUpiUseCase
import com.jar.app.feature_settings.ui.AddUpiViewModel
import com.jar.app.feature_user_api.domain.use_case.AddNewUserVpaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AddUpiViewModelAndroid @Inject constructor(
    private val fetchVpaChipUseCase: FetchVpaChipUseCase,
    private val addNewUserVpaUseCase: AddNewUserVpaUseCase,
    private val verifyUpiUseCase: VerifyUpiUseCase
): ViewModel() {

    private val viewModel by lazy {
        AddUpiViewModel(
            fetchVpaChipUseCase,
            addNewUserVpaUseCase,
            verifyUpiUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}