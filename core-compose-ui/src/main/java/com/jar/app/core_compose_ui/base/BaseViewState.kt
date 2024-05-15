package com.jar.app.core_compose_ui.base

sealed interface BaseViewState<out T> {
    object Loading : BaseViewState<Nothing>
    object Empty : BaseViewState<Nothing>
    data class Data<T>(val value: T) : BaseViewState<T>
    data class Error<E>(val value: E) : BaseViewState<E>
}