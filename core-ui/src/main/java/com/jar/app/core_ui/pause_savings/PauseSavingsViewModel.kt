package com.jar.app.core_ui.pause_savings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper
import com.jar.app.base.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PauseSavingsViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _pauseOptionsLiveData =
        MutableLiveData<ArrayList<PauseSavingOptionWrapper>>()
    val pauseOptionsLiveData: LiveData<ArrayList<PauseSavingOptionWrapper>>
        get() = _pauseOptionsLiveData
    var pauseSavingOptionWrapper: PauseSavingOptionWrapper? = null

    fun fetchPauseOptions(list: ArrayList<PauseSavingOptionWrapper>) {
        viewModelScope.launch {
            pauseSavingOptionWrapper = list.find { it.isSelected }
            _pauseOptionsLiveData.postValue(list)
        }
    }
    fun updatePauseOptionListOnItemClick(list: List<PauseSavingOptionWrapper>, position: Int) {
        viewModelScope.launch(dispatcherProvider.default) {
            val newList = ArrayList(list.map { it })
            if (newList[position].isSelected) {
                newList[position].isSelected = false
                pauseSavingOptionWrapper = null
            } else {
                newList.filter { it.isSelected }.map { it.isSelected = false }
                newList[position].isSelected = true
                pauseSavingOptionWrapper = newList[position]
            }
            _pauseOptionsLiveData.postValue(newList)
        }
    }

}