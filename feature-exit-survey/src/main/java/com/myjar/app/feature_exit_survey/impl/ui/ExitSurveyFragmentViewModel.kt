package com.myjar.app.feature_exit_survey.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.Cancel
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.ClickAction
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.ExitSurvey_BSClicked
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.ExitSurvey_BSShown
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.OptionChosen
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.OptionTyped
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.Screen
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.Submit
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.SurveyOptions
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.SurveyQuestion
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyEventConstants.TellUsWhyYouAreLeaving
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyQuestions
import com.jar.app.feature_exit_survey.shared.domain.model.SubmitExitSurveyResponseModel
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.PostExitSureveyUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExitSurveyFragmentViewModel @Inject constructor(
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    private val postExitSurveyUseCase: PostExitSureveyUseCase,
    private val analyticsApi: com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
): ViewModel() {

    private val _exitSurveyResponse: MutableStateFlow<ExitSurveyQuestions?> = MutableStateFlow(null)
    val exitSurveyResponse: StateFlow<ExitSurveyQuestions?> = _exitSurveyResponse

    private val _loading: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val loading:StateFlow<Boolean?> = _loading

    private val _dismissBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dismissBottomSheet: StateFlow<Boolean> = _dismissBottomSheet

    private val _error: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error

    private var fromWhichScreen = ""

    fun handleAction(action: ExitSurveyFragmentAction) {
        viewModelScope.launch {
            when(action) {
                is ExitSurveyFragmentAction.Init -> {
                    fromWhichScreen = action.screen
                    fetchExitSurveyQuestionsUseCase.fetchExitSurveyQuestions(action.screen).collect(
                        onLoading = {
                            _loading.value = true
                        },
                        onSuccessWithNullData = {
                            _loading.value = false
                        },
                        onSuccess = { it ->
                            _loading.value = false
                            _exitSurveyResponse.value = it
                            analyticsApi.postEvent(
                                ExitSurvey_BSShown,
                                mapOf(
                                    Screen to action.screen,
                                    SurveyQuestion to it?.question.orEmpty(),
                                    SurveyOptions to it?.choices!!.joinToString(
                                        separator = ", ",
                                        prefix = "{", postfix = "}"
                                    ) { it!!.text }
                                )
                            )
                        },
                        onError = { errMsg,_ ->
                            handleError(errMsg)
                        }
                    )
                }

                is ExitSurveyFragmentAction.SubmitResponse -> {
                    postExitSurveyUseCase.postExitSurvey(SubmitExitSurveyResponseModel(
                        featureSurveyId = _exitSurveyResponse.value?.featureSurveyId!!,
                        responses = listOf(action.reason)
                    )).collect(
                        onLoading = {
                            _loading.value = true
                        },
                        onSuccessWithNullData = {
                            val isAnswerSelectedFromTheChoices = exitSurveyResponse.value?.choices?.any { it?.text == action.reason }
                            analyticsApi.postEvent(
                                ExitSurvey_BSClicked,
                                mapOf(
                                    Screen to fromWhichScreen,
                                    ClickAction to Submit,
                                    OptionChosen to if(isAnswerSelectedFromTheChoices == true) action.reason else "",
                                    OptionTyped to if(isAnswerSelectedFromTheChoices == false) action.reason else "",
                                    SurveyQuestion to exitSurveyResponse.value?.question.orEmpty(),
                                    SurveyOptions to exitSurveyResponse.value?.choices!!.joinToString(
                                        separator = ", ",
                                        prefix = "{", postfix = "}"
                                    ) { it!!.text }
                                )
                            )
                            _dismissBottomSheet.value = true
                            _loading.value = false
                        },
                        onSuccess = {
                            _dismissBottomSheet.value = true
                            _loading.value = false
                        },
                        onError = { errMsg,_ ->
                            handleError(errMsg)
                        }
                    )
                }

                ExitSurveyFragmentAction.OnClickOnHelpCta -> {
                    analyticsApi.postEvent(
                        ExitSurvey_BSClicked,
                        mapOf(
                            Screen to fromWhichScreen,
                            ClickAction to Cancel,
                            OptionChosen to TellUsWhyYouAreLeaving,
                            SurveyQuestion to exitSurveyResponse.value?.question.orEmpty(),
                            SurveyOptions to exitSurveyResponse.value?.choices!!.joinToString(
                                separator = ", ",
                                prefix = "{", postfix = "}"
                            ) { it!!.text }
                        )
                    )
                }

                ExitSurveyFragmentAction.OnClickOnClose -> {
                    analyticsApi.postEvent(
                        ExitSurvey_BSClicked,
                        mapOf(
                            Screen to fromWhichScreen,
                            ClickAction to Cancel,
                            SurveyQuestion to exitSurveyResponse.value?.question.orEmpty(),
                            SurveyOptions to exitSurveyResponse.value?.choices!!.joinToString(
                                separator = ", ",
                                prefix = "{", postfix = "}"
                            ) { it!!.text }
                        )
                    )
                }
            }
        }
    }

    private fun handleError(errMessage: String) {
        _loading.value = false
    }
}