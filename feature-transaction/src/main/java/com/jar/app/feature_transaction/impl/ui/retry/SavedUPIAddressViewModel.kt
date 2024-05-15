package com.jar.app.feature_transaction.impl.ui.retry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_settings.domain.model.VpaChips
import com.jar.app.feature_settings.domain.use_case.FetchVpaChipUseCase
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.app.feature_user_api.domain.model.SavedVpaResponse
import com.jar.app.feature_user_api.domain.use_case.DeleteUserVpaUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserVpaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jar.internal.library.jar_core_network.api.model.RestClientResult as LibraryRestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper as LibraryApiResponseWrapper

@HiltViewModel
class SavedUPIAddressViewModel @Inject constructor(
    private val fetchUserSavedVpaUseCase: FetchUserVpaUseCase,
    private val fetchVpaChipsUseCase: FetchVpaChipUseCase,
    private val deleteUserSavedVpaUseCase: DeleteUserVpaUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _userVPAsLiveData =
        MutableLiveData<LibraryRestClientResult<LibraryApiResponseWrapper<SavedVpaResponse>>>()
    val userVPAsLiveData: LiveData<LibraryRestClientResult<LibraryApiResponseWrapper<SavedVpaResponse>>>
        get() = _userVPAsLiveData

    private val _vpaChipsLiveData =
        MutableLiveData<LibraryRestClientResult<LibraryApiResponseWrapper<VpaChips>>>()
    val vpaChipsLiveData: LiveData<LibraryRestClientResult<LibraryApiResponseWrapper<VpaChips>>>
        get() = _vpaChipsLiveData

    private val _searchVpaChipsLiveData =
        MutableLiveData<LibraryRestClientResult<LibraryApiResponseWrapper<VpaChips>>>()
    val searchVpaChipsLiveData: LiveData<LibraryRestClientResult<LibraryApiResponseWrapper<VpaChips>>>
        get() = _searchVpaChipsLiveData

    private val _deleteVPALiveData =
        MutableLiveData<LibraryRestClientResult<LibraryApiResponseWrapper<String?>>>()
    val deleteVPALiveData: LiveData<LibraryRestClientResult<LibraryApiResponseWrapper<String?>>>
        get() = _deleteVPALiveData

    private var job: Job? = null
    private var vpaRespList: List<String>? = null

    private var savedVPAList: List<SavedVPA>? = null

    fun fetchUserSavedVPAs() {
        viewModelScope.launch {
            fetchUserSavedVpaUseCase.fetchUserSavedVPAs().collect {
                savedVPAList = it.data?.data?.payoutSavedVpas
                _userVPAsLiveData.postValue(it)
            }
        }
    }

    fun fetchVpaChips() {
        viewModelScope.launch {
            fetchVpaChipsUseCase.fetchVpaChips().collect {
                _vpaChipsLiveData.postValue(it)
                it.data?.data?.vpaChips?.let {
                    vpaRespList = it
                }
            }
        }
    }

    fun vpaSearch(text: CharSequence?) {
        job?.cancel()
        job = viewModelScope.launch(dispatcherProvider.default) {
            if (text.isNullOrBlank())
                _searchVpaChipsLiveData.postValue(
                    LibraryRestClientResult.success(
                        LibraryApiResponseWrapper(
                            data = VpaChips(vpaRespList ?: emptyList()),
                            success = true
                        )
                    )
                )
            else {
                val filteredList =
                    vpaRespList?.filter { it.contains(text, true) }
                _searchVpaChipsLiveData.postValue(
                    LibraryRestClientResult.success(
                        LibraryApiResponseWrapper(
                            data = VpaChips(filteredList ?: emptyList()),
                            success = true
                        )
                    )
                )
            }
        }
    }

    fun deleteSavedVpa(savedVPA: SavedVPA) {
        viewModelScope.launch {
            deleteUserSavedVpaUseCase.deleteUserSavedVPA(savedVPA.id).collect {
                _deleteVPALiveData.postValue(it)
            }
        }
    }
    fun updateListOnVpaSelection(selectedId: String?) {
        viewModelScope.launch(dispatcherProvider.default) {
            val newList = mutableListOf<SavedVPA>()
            savedVPAList?.forEach {
                val data = SavedVPA(
                    it.id,
                    it.vpaHandle,
                    it.isDeleted,
                    it.isDefault,
                    it.isVerified,
                    it.isPrimaryUpi,
                    it.id == selectedId
                )
                newList.add(data)
            }
            _userVPAsLiveData.postValue(
                LibraryRestClientResult.success(
                    LibraryApiResponseWrapper(
                        success = true,
                        data = SavedVpaResponse(newList)
                    )
                )
            )
        }
    }
}