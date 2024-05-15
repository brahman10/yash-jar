package com.jar.app.feature_settings.ui

import com.jar.app.feature_settings.domain.model.DeleteCard
import com.jar.app.feature_settings.domain.use_case.DeleteSavedCardUseCase
import com.jar.app.feature_user_api.domain.use_case.DeleteUserVpaUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DeletePaymentMethodViewModel constructor(
    private val deleteSavedCardUseCase: DeleteSavedCardUseCase,
    private val deleteSavedUpiIdUseCase: DeleteUserVpaUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _deleteCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val deleteCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteCardLiveData.toCommonStateFlow()

    private val _deleteUpiIdLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<String?>>>(RestClientResult.none())
    val deleteUpiIdLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<String?>>>
        get() = _deleteUpiIdLiveData.toCommonStateFlow()

    fun deleteUpiId(savedVpaId: String) {
        viewModelScope.launch {
            deleteSavedUpiIdUseCase.deleteUserSavedVPA(savedVpaId).collect {
                _deleteUpiIdLiveData.emit(it)
            }
        }
    }

    fun deleteCard(deleteCard: DeleteCard) {
        viewModelScope.launch {
            deleteSavedCardUseCase.deleteSavedCardUseCase(deleteCard).collect {
                _deleteCardLiveData.emit(it)
            }
        }
    }
}