package com.jar.app.feature_vasooli.impl.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_vasooli.impl.domain.model.Intro
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class VasooliIntroViewModel @Inject constructor(
    private val introListGenerator: IntroListGenerator
): ViewModel() {

    private val _introListLiveData = MutableLiveData<List<Intro>>()
    val introListLiveData: LiveData<List<Intro>>
        get() = _introListLiveData

    var size = 0

    init {
        fetchIntroList()
    }

    fun fetchIntroList() {
        viewModelScope.launch {
            val introList = introListGenerator.getIntroList()
            size = introList.size
            _introListLiveData.postValue(introList)
        }
    }
}