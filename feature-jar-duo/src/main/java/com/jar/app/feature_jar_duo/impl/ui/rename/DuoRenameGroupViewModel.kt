package com.jar.app.feature_jar_duo.impl.ui.rename

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_jar_duo.shared.domain.use_case.RenameGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DuoRenameGroupViewModel @Inject constructor(
    private val renameGroupUseCase: RenameGroupUseCase
) : ViewModel() {

    private val _renameGroupLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val renameGroupLiveData : LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _renameGroupLiveData

    fun renameGroup(groupId: String, groupName:String){
        viewModelScope.launch {
            renameGroupUseCase.renameGroup(groupId, groupName).collectLatest {
                _renameGroupLiveData.postValue(it)
            }
        }
    }
}