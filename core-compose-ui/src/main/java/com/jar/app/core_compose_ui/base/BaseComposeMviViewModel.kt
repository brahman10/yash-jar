package com.jar.app.core_compose_ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseComposeMviViewModel<STATE : BaseViewState<*>, EVENT> : ViewModel() {

    private val _uiState = MutableStateFlow<BaseViewState<*>>(BaseViewState.Empty)
    val uiState = _uiState.asStateFlow()

    abstract fun onTriggerEvent(eventType: EVENT)

    protected fun setState(state: STATE) = viewModelScope.launch {
        _uiState.update {state}
    }

    private fun startLoading() {
        _uiState.value = BaseViewState.Loading
    }

}