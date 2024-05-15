package com.jar.feature_quests.impl.ui.trivia_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.feature_quests.shared.domain.model.request.SubmitAnswerRequestData
import com.jar.feature_quests.shared.domain.use_case.GetQuizGameQuestionUseCase
import com.jar.feature_quests.shared.domain.use_case.MarkAnswerUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.checkIfAnyRestClientIsLoading
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class QuestTriviaViewModel @Inject constructor(
    private val getQuizGameQuestionUseCase: GetQuizGameQuestionUseCase,
    private val markAnswerUseCase: MarkAnswerUseCase,
    private val analyticsApi: AnalyticsApi
): ViewModel() {

    private val _questTriviaState = MutableStateFlow(QuestTriviaViewState())
    val questTriviaState: CStateFlow<QuestTriviaViewState>
        get() = _questTriviaState.asStateFlow().toCommonStateFlow()

    private val _combinedFlowLoading = _questTriviaState.transform {
        emit(checkIfAnyRestClientIsLoading(it.questionAnswersData, it.submitAnswerData))
    }.toCommonFlow()
    val combinedFlowLoading: CFlow<Boolean> = _combinedFlowLoading.toCommonFlow()

    fun selectOption(optionSelectedIndex: Int, option: String) {
        markQuizAnswer(
            SubmitAnswerRequestData(
                questionId = questTriviaState.value.questionAnswersData.data?.data?.questionId.orEmpty(),
                answeredOption = option
            ),
            optionSelectedIndex
        )
    }

    fun fetchQuizQuestionAnswer() {
        viewModelScope.launch {
            getQuizGameQuestionUseCase.getQuizGameQuestion().collectUnwrapped(
                onSuccess = { data ->
                    _questTriviaState.update {
                        it.copy(
                            questionAnswersData = RestClientResult.success(data),
                            submitAnswerData = RestClientResult.none(),
                            optionSelectedIndex = null,
                            errorMessage = data.errorMessage
                        )
                    }
                },
                onError = { errorMessage, _ ->
                    _questTriviaState.update {
                        it.copy(
                            errorMessage = errorMessage
                        )
                    }
                }
            )
        }
    }

    private fun markQuizAnswer(submitAnswerRequestData: SubmitAnswerRequestData, optionSelectedIndex: Int) {
        viewModelScope.launch {
            markAnswerUseCase.markAnswer(submitAnswerRequestData).collectUnwrapped(
                onSuccess = { data ->
                    _questTriviaState.update {
                        it.copy(submitAnswerData = RestClientResult.success(data), optionSelectedIndex = optionSelectedIndex, errorMessage = data.errorMessage)
                    }
                },
                onError = { errorMessage, _ ->
                    _questTriviaState.update {
                        it.copy(
                            errorMessage = errorMessage
                        )
                    }
                }
            )
        }
    }

    fun fireShownTriviaEvent(chancesLeft: Int) {
        analyticsApi.postEvent(
            QuestEventKey.Events.Shown_QuestQuizPage,
            mapOf(
                QuestEventKey.Properties.chances_left to chancesLeft
            )
        )
    }

    fun fireClickedTriviaEvent(analyticsData: Map<String, String>) {
        analyticsApi.postEvent(
            QuestEventKey.Events.Clicked_QuestQuizPage,
            analyticsData
        )
    }
}
