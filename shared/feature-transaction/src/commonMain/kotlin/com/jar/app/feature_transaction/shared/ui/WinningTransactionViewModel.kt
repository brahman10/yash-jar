package com.jar.app.feature_transaction.shared.ui

import com.jar.app.feature_transaction.shared.domain.model.UserWinningDetailsRes
import com.jar.app.feature_transaction.shared.domain.model.WinningData
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserWinningDetailsUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchWinningListingUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WinningTransactionViewModel constructor(
    private val fetchUserWinningDetailsUseCase: IFetchUserWinningDetailsUseCase,
    private val fetchWinningListingUseCase: IFetchWinningListingUseCase,
    coroutineScope: CoroutineScope?
) {

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    var pager: Pager<Int, WinningData>? = null

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _winningsListFlow = MutableStateFlow<PagingData<WinningData>?>(null)
    val winningsListFlow: CFlow<PagingData<WinningData>?>
        get() = _winningsListFlow.toCommonFlow()

    private val _userWinningFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserWinningDetailsRes>>>(
            RestClientResult.none()
        )
    val userWinningFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserWinningDetailsRes>>>
        get() = _userWinningFlow.toCommonStateFlow()

    fun fetchUserWinningDetails() {
        viewModelScope.launch {
            fetchUserWinningDetailsUseCase.fetchUserWinningDetails().collect {
                _userWinningFlow.emit(it)
            }
        }
    }

    fun fetchWinnings() {
        viewModelScope.launch {
            pager = Pager(
                viewModelScope,
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = NETWORK_PAGE_SIZE
                ),
                initialKey = 0,
                getItems = { currentKey, size ->
                    val response =
                        fetchWinningListingUseCase.fetchWinningListing(
                            pageNo = currentKey,
                            pageSize = size
                        )
                    val items = response.data?.data.orEmpty()
                    PagingResult(
                        items = items,
                        currentKey = currentKey,
                        prevKey = { currentKey - 1 },
                        nextKey = { currentKey + 1 }
                    )
                },
            )
            pager!!.pagingData.cachedIn(viewModelScope).collectLatest {
                _winningsListFlow.emit(it)
            }
        }
    }
}

