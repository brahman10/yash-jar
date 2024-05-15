package com.jar.app.feature_settings.impl.ui.payment_methods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_settings.domain.use_case.FetchUserSavedCardsUseCase
import com.jar.app.feature_settings.ui.PaymentMethodsViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchUserVpaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PaymentMethodsViewModelAndroid @Inject constructor(
    private val fetchUserVpaUseCase: FetchUserVpaUseCase,
    private val fetchUserSavedCardsUseCase: FetchUserSavedCardsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        PaymentMethodsViewModel(
            fetchUserVpaUseCase,
            fetchUserSavedCardsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}