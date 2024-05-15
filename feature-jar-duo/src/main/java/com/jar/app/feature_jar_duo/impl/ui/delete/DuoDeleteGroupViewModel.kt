package com.jar.app.feature_jar_duo.impl.ui.delete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData
import com.jar.app.feature_jar_duo.shared.domain.use_case.DeleteGroupUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DuoDeleteGroupViewModel @Inject constructor(
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val fetchGroupListUseCase: FetchGroupListUseCase
) : ViewModel() {
    private val _deleteGroupLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val deleteGroupLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteGroupLiveData

    private val _listGroupLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<List<DuoGroupData>>>>()
    val listGroupLiveData : LiveData<RestClientResult<ApiResponseWrapper<List<DuoGroupData>>>>
        get() = _listGroupLiveData

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            deleteGroupUseCase.deleteGroup(groupId).collectLatest {
                _deleteGroupLiveData.postValue(it)
            }
        }
    }

    fun fetchGroupList(){
        viewModelScope.launch {
            fetchGroupListUseCase.fetchGroupList().collectLatest {
                _listGroupLiveData.postValue(it)
            }
        }
    }
}