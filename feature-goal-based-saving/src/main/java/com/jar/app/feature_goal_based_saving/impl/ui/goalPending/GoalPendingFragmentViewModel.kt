package com.jar.app.feature_goal_based_saving.impl.ui.goalPending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.shared.data.model.InProgressResponse
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class GoalPendingFragmentViewModel @Inject constructor(
    private val serializer: Serializer
): ViewModel() {
    private val _state = MutableStateFlow(GoalPendingFragmentState())
    val state: StateFlow<GoalPendingFragmentState> = _state

    private val _onClickOnPendingBanner = MutableSharedFlow<String>()
    val onClickOnPendingBanner: SharedFlow<String> = _onClickOnPendingBanner


    fun handleAction(action: GoalPendingFragmentActions){
        viewModelScope.launch(Dispatchers.IO) {
            when(action) {
                is GoalPendingFragmentActions.Init -> {
                    val data = serializer.decodeFromString<InProgressResponse>(action.serializedData)
                    _state.value = _state.value.copy(
                        OnData = data
                    )
                }
                GoalPendingFragmentActions.OnClickOnBanner -> {
                    viewModelScope.launch {
                        _onClickOnPendingBanner.emit(
                            state.value.OnData?.banner?.deeplink ?: ""
                        )
                    }
                }
            }
        }
    }

}