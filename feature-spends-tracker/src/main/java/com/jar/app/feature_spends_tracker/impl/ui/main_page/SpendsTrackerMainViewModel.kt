package com.jar.app.feature_spends_tracker.impl.ui.main_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.jar.app.feature_spends_tracker.impl.data.paging_source.SpendsTransactionDataPagingSource
import com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData.SpendsData
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsDataUseCase
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsTransactionDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SpendsTrackerMainViewModel @Inject constructor(
    private val fetchSpendsDataUseCase: FetchSpendsDataUseCase,
    private val fetchSpendsTransactionDataUseCase: FetchSpendsTransactionDataUseCase
) :
    ViewModel() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
        private const val INITIAL_PAGE = 10
    }

    private val _spendsDataLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<SpendsData>>>()
    val spendsDataLiveData: LiveData<RestClientResult<ApiResponseWrapper<SpendsData>>>
        get() = _spendsDataLiveData

    fun fetchSpendsData() {
        viewModelScope.launch {
            fetchSpendsDataUseCase.fetchSpendsData().collectLatest {
                _spendsDataLiveData.postValue(it)
            }
        }
    }

    fun fetchSpendsTransactionData() = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            initialLoadSize = INITIAL_PAGE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SpendsTransactionDataPagingSource(fetchSpendsTransactionDataUseCase) }
    ).flow.cachedIn(viewModelScope)
}