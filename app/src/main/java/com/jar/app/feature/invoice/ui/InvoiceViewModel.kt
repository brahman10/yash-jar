package com.jar.app.feature.invoice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.jar.app.feature.invoice.data.network.InvoicePagingSource
import com.jar.app.feature.invoice.domain.use_case.FetchInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val fetchInvoiceUseCase: FetchInvoiceUseCase
):ViewModel() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    fun fetchInvoices() = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            initialLoadSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            InvoicePagingSource(fetchInvoiceUseCase)
        }
    ).flow.cachedIn(viewModelScope)
}