package com.jar.app.feature_settings.impl.ui.payment_methods.add_card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_settings.domain.model.CardBinInfo
import com.jar.app.feature_settings.domain.model.CardDetail
import com.jar.app.feature_settings.domain.use_case.AddNewCardUseCase
import com.jar.app.feature_settings.domain.use_case.FetchCardBinInfoUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AddNewCardViewModel @Inject constructor(
    private val addNewCardUseCase: AddNewCardUseCase,
    private val fetchCardBinInfoUseCase: FetchCardBinInfoUseCase
) :
    ViewModel() {

    private val _cardBinInfoLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<CardBinInfo>>>()
    val cardBinInfoLiveData: LiveData<RestClientResult<ApiResponseWrapper<CardBinInfo>>>
        get() = _cardBinInfoLiveData

    private val _addNewCardLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val addNewCardLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _addNewCardLiveData

    fun fetchCardBinInfo(cardBin: String) {
        viewModelScope.launch {
            fetchCardBinInfoUseCase.fetchCardBinInfo(cardBin).collect {
                _cardBinInfoLiveData.postValue(it)
            }
        }
    }

    fun addNewCard(cardDetail: CardDetail) {
        viewModelScope.launch {
            addNewCardUseCase.addNewCard(cardDetail).collect {
                _addNewCardLiveData.postValue(it)
            }
        }
    }
}