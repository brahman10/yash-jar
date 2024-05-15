package com.jar.app.feature_vasooli.impl.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_utils.data.NetworkFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class VasooliViewModel @Inject constructor(
    private val networkFlow: NetworkFlow
): ViewModel(){

    private val _networkStateLiveData = MutableLiveData<Boolean>()
    val networkStateLiveData: LiveData<Boolean>
        get() = _networkStateLiveData

    private var networkJob: Job? = null

    init {
        observeNetwork()
    }

    fun observeNetwork() {
        networkJob?.cancel()
        networkJob = viewModelScope.launch {
            networkFlow.networkStatus.collect {
                _networkStateLiveData.postValue(it)
            }
        }
    }
}