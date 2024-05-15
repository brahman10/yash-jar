package com.jar.app.feature.faq.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature.home.domain.usecase.FetchDashboardStaticContentUseCase
import com.jar.app.core_base.domain.model.Faq
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FaqViewModel @Inject constructor(
    private val fetchDashboardStaticContentUseCase: FetchDashboardStaticContentUseCase
) : ViewModel() {

    private val _faqLiveData =
        MutableLiveData<RestClientResult<List<Faq>>>()
    val faqLiveData: LiveData<RestClientResult<List<Faq>>>
        get() = _faqLiveData

    fun fetchFaqData() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArrayList<Faq>()
            fetchDashboardStaticContentUseCase.fetchDashboardStaticContent(StaticContentType.GENERAL_FAQS)
                .collect(
                    onLoading = {
                        _faqLiveData.postValue(RestClientResult.loading())
                    },
                    onSuccess = {
                        it?.faqList?.faqDataList?.forEach { data ->
                            list.addAll(data.faqs.map { it.copy(type = data.type) })
                        }
                        _faqLiveData.postValue(RestClientResult.success(list))
                    },
                    onError = { errorMessage, errorCode ->
                        _faqLiveData.postValue(RestClientResult.error(errorMessage))
                    }
                )
        }
    }
}