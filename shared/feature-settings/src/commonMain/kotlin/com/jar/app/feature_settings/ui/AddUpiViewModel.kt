package com.jar.app.feature_settings.ui

import com.jar.app.feature_settings.domain.model.VerifyUpiResponse
import com.jar.app.feature_settings.domain.model.VpaChips
import com.jar.app.feature_settings.domain.use_case.FetchVpaChipUseCase
import com.jar.app.feature_settings.domain.use_case.VerifyUpiUseCase
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.app.feature_user_api.domain.use_case.AddNewUserVpaUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddUpiViewModel constructor(
    private val fetchVpaChipUseCase: FetchVpaChipUseCase,
    private val addNewUserVpaUseCase: AddNewUserVpaUseCase,
    private val verifyUpiUseCase: VerifyUpiUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _vpaChipsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<VpaChips>>>(
            RestClientResult.none()
        )
    val vpaChipsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<VpaChips>>>
        get() = _vpaChipsLiveData.toCommonStateFlow()

    private val _searchVpaChipsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<VpaChips>>>(RestClientResult.none())
    val searchVpaChipsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<VpaChips>>>
        get() = _searchVpaChipsLiveData.toCommonStateFlow()

    private val _addNewVPALiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SavedVPA?>>>(RestClientResult.none())
    val addNewVPALiveData: CStateFlow<RestClientResult<ApiResponseWrapper<SavedVPA?>>>
        get() = _addNewVPALiveData.toCommonStateFlow()

    private val _verifyUpiLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<VerifyUpiResponse>>>(RestClientResult.none())
    val verifyUpiLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<VerifyUpiResponse>>>
        get() = _verifyUpiLiveData.toCommonStateFlow()

    private var job: Job? = null
    private var vpaRespList: List<String>? = null

    fun fetchVpaChips() {
        viewModelScope.launch {
            fetchVpaChipUseCase.fetchVpaChips().collect {
                _vpaChipsLiveData.emit(it)
                it.data?.data?.vpaChips?.let { list ->
                    vpaRespList = list
                }
            }
        }
    }

    fun vpaSearch(text: String?) {
        job?.cancel()
        job = viewModelScope.launch {
            if (text.isNullOrBlank())
                _searchVpaChipsLiveData.emit(
                    RestClientResult.success(
                        ApiResponseWrapper(
                            data = VpaChips(vpaRespList ?: emptyList()), success = true
                        )
                    )
                )
            else {
                val filteredList =
                    vpaRespList?.filter { it.contains(text, true) }
                _searchVpaChipsLiveData.emit(
                    RestClientResult.success(
                        ApiResponseWrapper(
                            data = VpaChips(filteredList ?: emptyList()),
                            success = true
                        )
                    )
                )
            }
        }
    }

    fun addVpa(vpaName: String) {
        viewModelScope.launch {
            addNewUserVpaUseCase.addNewVPA(vpaName).collect {
                _addNewVPALiveData.emit(it)
            }
        }
    }

    fun verifyUpiAddress(upiAddress: String) {
        viewModelScope.launch {
            verifyUpiUseCase.verifyUpiAddress(upiAddress).collect {
                _verifyUpiLiveData.emit(it)
            }
        }
    }
}