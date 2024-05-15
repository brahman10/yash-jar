package com.jar.app.feature_buy_gold_v2.shared.ui

import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousDate
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousDatesUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
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

class AuspiciousDatesViewModel constructor(
    private val fetchAuspiciousDatesUseCase: FetchAuspiciousDatesUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }

    private val _auspiciousDatedFlow: MutableStateFlow<PagingData<AuspiciousDate>?> =
        MutableStateFlow(null)
    val auspiciousDatedFlow: CStateFlow<PagingData<AuspiciousDate>?>
        get() = _auspiciousDatedFlow.toCommonStateFlow()

    var pager: Pager<Int, AuspiciousDate>? = null

    fun fetchAuspiciousDates() {
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
                    val response = fetchAuspiciousDatesUseCase.fetchAuspiciousDates(
                        page = currentKey,
                        size = size
                    )
                    val items = response.data?.data?.auspiciousDateList.orEmpty()
                    PagingResult(
                        items = items,
                        currentKey = currentKey,
                        prevKey = { currentKey - 1 },
                        nextKey = { currentKey + 1 }
                    )
                },
            )
            pager!!.pagingData.cachedIn(viewModelScope).collectLatest {
                _auspiciousDatedFlow.emit(it)
            }
        }
    }
}