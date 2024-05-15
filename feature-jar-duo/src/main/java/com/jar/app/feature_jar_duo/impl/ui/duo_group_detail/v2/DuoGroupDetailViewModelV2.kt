package com.jar.app.feature_jar_duo.impl.ui.duo_group_detail.v2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupInfoV2
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DuoGroupDetailViewModelV2 @Inject constructor(
    private val fetchGroupInfoUseCase: FetchGroupInfoUseCase,
) : ViewModel() {

    private val _groupInfoV2LiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupInfoV2>>>()
    val groupInfoV2LiveData : LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupInfoV2>>>
        get() = _groupInfoV2LiveData

    private val _renameGroupLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit>>>()
    val renameGroupLiveData : LiveData<RestClientResult<ApiResponseWrapper<Unit>>>
        get() = _renameGroupLiveData

    private val _deleteGroupLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit>>>()
    val deleteGroupLiveData : LiveData<RestClientResult<ApiResponseWrapper<Unit>>>
        get() = _deleteGroupLiveData

    fun fetchGroupInfoV2(groupId : String?){
        viewModelScope.launch {
            fetchGroupInfoUseCase.fetchGroupInfoV2(groupId).collectLatest {
                _groupInfoV2LiveData.postValue(it)

            }
        }
    }


}