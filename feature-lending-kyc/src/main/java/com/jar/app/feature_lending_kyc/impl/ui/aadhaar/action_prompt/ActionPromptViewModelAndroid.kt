package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.action_prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.action_prompt.ActionPromptViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ActionPromptViewModelAndroid @Inject constructor() : ViewModel() {
    private val viewModel by lazy {
        ActionPromptViewModel(
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}