package com.jar.app.feature_jar_duo.impl.ui.duo_group_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupInfo
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DuoGroupDetailViewModel @Inject constructor(
    private val fetchGroupInfoUseCase: FetchGroupInfoUseCase,
) : ViewModel() {

    private val _groupInfoLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DuoGroupInfo>>>()
    val groupInfoLiveData : LiveData<RestClientResult<ApiResponseWrapper<DuoGroupInfo>>>
        get() = _groupInfoLiveData

    private val _renameGroupLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit>>>()
    val renameGroupLiveData : LiveData<RestClientResult<ApiResponseWrapper<Unit>>>
        get() = _renameGroupLiveData

    private val _deleteGroupLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit>>>()
    val deleteGroupLiveData : LiveData<RestClientResult<ApiResponseWrapper<Unit>>>
        get() = _deleteGroupLiveData

    fun fetchGroupInfo(groupId : String?){
        viewModelScope.launch {
            fetchGroupInfoUseCase.fetchGroupInfo(groupId).collectLatest {
                _groupInfoLiveData.postValue(it)

            }
        }
    }



}