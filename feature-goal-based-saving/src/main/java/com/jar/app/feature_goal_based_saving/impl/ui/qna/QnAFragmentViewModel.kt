package com.jar.app.feature_goal_based_saving.impl.ui.qna

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.clickaction
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalEndRequest
import com.jar.app.feature_goal_based_saving.shared.data.model.QnAResponse
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.EndGoalUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchQnaUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QnAFragmentViewModel @Inject constructor(
    private val fetchQnaUseCase: FetchQnaUseCase,
    private val endgoalUseCase: EndGoalUseCase,
    private val serializer: Serializer,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state = MutableStateFlow(QnAFragmentState())
    val state: StateFlow<QnAFragmentState> = _state
    private var goalId: String = ""

    private val _onData = MutableSharedFlow<QnAResponse?>()
    val onData:SharedFlow<QnAResponse?> = _onData

    private val _onLoading = MutableSharedFlow<Boolean?>()
    val onLoading: SharedFlow<Boolean?> = _onLoading

    private val _onClose = MutableSharedFlow<Unit?>()
    val onClose: SharedFlow<Unit?> = _onClose

    private val _onNavigateToEndGoalScreen = MutableSharedFlow<OnNavigateToEndGoalScreenRequestBody?>()
    val onNavigateToEndGoalScreen: SharedFlow<OnNavigateToEndGoalScreenRequestBody?> = _onNavigateToEndGoalScreen


    fun handleActions(actions: QnAFragmentActions) {
        viewModelScope.launch(Dispatchers.IO) {
            when(actions) {
                is QnAFragmentActions.Init -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenShown,
                        mapOf(
                            screen_type to "End Goal Survey screen"
                        )
                    )
                    _state.value = _state.value.copy(
                        GoalEndResponse = actions.goalEndResponse
                    )
                    goalId = actions.goalId
                    fetchQnA()
                }
                QnAFragmentActions.OnClickOnSubmit -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "End Goal Survey screen",
                            clickaction to "Submit",
                            "optionschosen" to (_state.value.selectedMessage?.first ?: "")
                        )
                    )
                    endgoalUseCase.execute(GoalEndRequest(goalId = goalId, message = state.value.selectedMessage?.first ?: "")).collect(
                        onLoading = {
                            _onLoading.emit(
                                true
                            )
                        },
                        onSuccess = {
                            val qnaResponse = serializer.encodeToString(it)
                            _onLoading.emit(
                                false
                            )
                            _onNavigateToEndGoalScreen.emit(
                                OnNavigateToEndGoalScreenRequestBody(
                                    qnaResponse, goalId
                                )
                            )
                        },
                        onError = {_,_ ->
                            _onLoading.emit(
                                false
                            )
                        }
                    )
                }
                is QnAFragmentActions.OnOptionSelected -> {
                    _state.value = _state.value.copy(
                        selectedMessage = Pair(actions.message, actions.index)
                    )
                }

                QnAFragmentActions.OnClickOnClose -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "End Goal Survey screen",
                            clickaction to "Cross clicked",
                            "optionschosen" to (_state.value.selectedMessage?.first ?: "")
                        )
                    )
                    _onClose.emit(
                        Unit
                    )
                }
            }
        }
    }

    private fun fetchQnA() {
        viewModelScope.launch {
            fetchQnaUseCase.execute().collect(onLoading = {
                _onLoading.emit(
                    true
                )
            },
                onSuccess = {
                    _onLoading.emit(
                        false
                    )
                    _onData.emit(
                        it
                    )
                }
            )
        }
    }
}