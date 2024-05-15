package com.jar.app.feature_daily_investment.impl.ui.faq

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_daily_investment.shared.domain.model.GenericFAQs
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailySavingsFaqDataUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DailySavingsV2FaqViewModel @Inject constructor(
    private val fetchDailySavingsFaqDataUseCase: FetchDailySavingsFaqDataUseCase
) : ViewModel(
) {
    private val _faqListLiveData = SingleLiveEvent<RestClientResult<List<GenericFAQs>>>()
    val faqListLiveData: LiveData<RestClientResult<List<GenericFAQs>>>
        get() = _faqListLiveData


    fun fetchFaQData() {
        viewModelScope.launch {
            fetchDailySavingsFaqDataUseCase.fetchDailySavingsFaqData().collect(
                onLoading = {
                    _faqListLiveData.postValue(RestClientResult.loading())
                },
                onSuccess = {
                    _faqListLiveData.postValue(RestClientResult.success(it.genericFAQResponse.genericFAQs))
                },
                onError = { message, _ ->
                    _faqListLiveData.postValue(RestClientResult.error(message))
                }
            )
        }
    }

    fun updateFaqList(list: List<GenericFAQs>, position: Int) {
        viewModelScope.launch {
            val newList = ArrayList(list.map { it.copy() })
            val item = newList[position]
            item.isExpanded = !item.isExpanded
            newList[position] = item
            _faqListLiveData.postValue(RestClientResult.success(newList))
        }
    }
}

