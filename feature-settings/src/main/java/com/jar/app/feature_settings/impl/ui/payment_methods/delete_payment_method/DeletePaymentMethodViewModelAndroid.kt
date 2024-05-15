package com.jar.app.feature_settings.impl.ui.payment_methods.delete_payment_method

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_settings.domain.use_case.DeleteSavedCardUseCase
import com.jar.app.feature_settings.ui.DeletePaymentMethodViewModel
import com.jar.app.feature_user_api.domain.use_case.DeleteUserVpaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DeletePaymentMethodViewModelAndroid @Inject constructor(
    private val deleteSavedCardUseCase: DeleteSavedCardUseCase,
    private val deleteSavedUpiIdUseCase: DeleteUserVpaUseCase
) : ViewModel() {

    private val viewModel by lazy {
        DeletePaymentMethodViewModel(
            deleteSavedCardUseCase,
            deleteSavedUpiIdUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}