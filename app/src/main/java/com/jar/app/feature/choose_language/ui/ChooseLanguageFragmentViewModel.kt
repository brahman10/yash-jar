package com.jar.app.feature.choose_language.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_onboarding.shared.domain.model.Language
import com.jar.app.feature_onboarding.shared.domain.model.LanguageList
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSupportedLanguagesUseCase
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ChooseLanguageFragmentViewModel @Inject constructor(
    private val fetchSupportedLanguageUseCase: FetchSupportedLanguagesUseCase,
    private val prefs: PrefsApi
) :
    ViewModel() {

    var languages: MutableList<Language>? = null
    var selectedLanguage: Language? = null

    private val _languageLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<LanguageList>>>()
    val languageLiveData: LiveData<RestClientResult<ApiResponseWrapper<LanguageList>>>
        get() = _languageLiveData

    fun getSupportedLanguages() {
        viewModelScope.launch {
            fetchSupportedLanguageUseCase.fetchSupportedLanguages().collect {
                _languageLiveData.postValue(it)
                val selectedLanguageCode = prefs.getCurrentLanguageCode()
                selectedLanguage =
                    it.data?.data?.languages?.firstOrNull { selectedLanguageCode == it.code }
            }
        }
    }

    fun updateSelectedLanguage(selectedLanguage: Language) {
        // user can not unselect the selected language
        if (selectedLanguage.code == this.selectedLanguage?.code && selectedLanguage.isSelected) return

        val newList = languages?.map {
            if (it.code == selectedLanguage.code) {
                this.selectedLanguage = it
                it.copy(isSelected = !selectedLanguage.isSelected)
            } else {
                it.copy(isSelected = false)
            }
        }.orEmpty()
        _languageLiveData.postValue(
            RestClientResult.success(ApiResponseWrapper(LanguageList(newList), true))
        )
    }

    fun updateSelectedLanguage(selectedLanguageCode: String) {
        val newList = languages?.map {
            if (it.code == selectedLanguageCode) {
                it.copy(isSelected = true)
            } else {
                it.copy(isSelected = false)
            }
        }.orEmpty()
        _languageLiveData.postValue(
            RestClientResult.success(ApiResponseWrapper(LanguageList(newList), true))
        )
    }
}