package com.jar.app.feature_homepage.impl.util

import android.util.Log
import com.jar.app.feature_homepage.impl.domain.model.ScrollState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScrollStateProvider @Inject constructor() {

    private val _scrollStateFlow = MutableSharedFlow<ScrollState>(replay = 1)
    val scrollStateFlow = _scrollStateFlow.asSharedFlow()

    suspend fun updateScrollState(scrollState: ScrollState) {
        _scrollStateFlow.emit(scrollState)
    }
}