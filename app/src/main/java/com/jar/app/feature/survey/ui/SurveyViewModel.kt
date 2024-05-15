package com.jar.app.feature.survey.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.survey.domain.model.*
import com.jar.app.feature.survey.domain.use_case.FetchUserSurveyUseCase
import com.jar.app.feature.survey.domain.use_case.SubmitUserSurveyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject

@HiltViewModel
internal class SurveyViewModel @Inject constructor(
    private val submitUserSurveyUseCase: SubmitUserSurveyUseCase,
    private val fetchUserSurveyUseCase: FetchUserSurveyUseCase
) : ViewModel() {

    private val _surveyLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Survey?>>>()
    val surveyLiveData: LiveData<RestClientResult<ApiResponseWrapper<Survey?>>>
        get() = _surveyLiveData

    private val _submitSurveyLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<SubmitSurveyResponse>>>()
    val submitSurveyLiveData: LiveData<RestClientResult<ApiResponseWrapper<SubmitSurveyResponse>>>
        get() = _submitSurveyLiveData

    private val _choiceLiveData = MutableLiveData<ArrayList<ChoiceWrapper>>()
    val choiceLiveData: LiveData<ArrayList<ChoiceWrapper>>
        get() = _choiceLiveData

    private lateinit var surveyId: String
    private val surveyList = ArrayList<SurveyQuestion>()
    val userChoicesMap = HashMap<String, List<Int>>()

    fun fetchSurvey() {
        viewModelScope.launch {
            val response = fetchUserSurveyUseCase.fetchUserSurvey()
            _surveyLiveData.postValue(response)
            response.data?.data?.let {
                it.surveyQuestions?.let {
                    surveyList.addAll(it)
                }
                surveyId = it.surveyId
            }
        }
    }

    fun getSurveyQuestionByPosition(position: Int) = surveyList[position].question

    fun getSurveyListSize() = surveyList.size

    fun getSurveyDataByPosition(position: Int) {
        viewModelScope.launch {
            if (position < surveyList.size) {
                val list = ArrayList<ChoiceWrapper>()
                surveyList[position].choices.map { list.add(ChoiceWrapper(it)) }
                _choiceLiveData.postValue(list)
            }
        }
    }

    fun choiceSelected(surveyPosition: Int, position: Int, list: List<ChoiceWrapper>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (surveyPosition < surveyList.size) {
                val newList = ArrayList(list.map { it.copy() })
                newList[position].isSelected = !newList[position].isSelected
                if (!surveyList[surveyPosition].mcq)
                    newList.mapIndexed { index, item -> item.copy(isSelected = index != position) }
                _choiceLiveData.postValue(newList)
            }
        }
    }

    fun submitQuestion(position: Int, submitSurvey: Boolean = false) {
        viewModelScope.launch {
            if (position < surveyList.size) {
                val tempList = ArrayList<Int>()
                _choiceLiveData.value?.forEachIndexed { index, choiceWrapper ->
                    if (choiceWrapper.isSelected)
                        tempList.add(index)
                }
                userChoicesMap[surveyList[position].id] = tempList
                if (submitSurvey)
                    submitUserSurvey()
            }
        }
    }

    fun submitUserSurvey() {
        viewModelScope.launch {
            val userChoiceList = ArrayList<UserChoice>()
            userChoicesMap.map {
                userChoiceList.add(UserChoice(it.key, it.value.map { it }))
            }
            _submitSurveyLiveData.postValue(
                submitUserSurveyUseCase.submitSurvey(
                    Json.encodeToJsonElement(UserSurveyResponses(surveyId, userChoiceList))
                )
            )
        }
    }
}