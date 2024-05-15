package com.jar.app.feature.invoice.domain.repository

import com.jar.app.feature.invoice.domain.model.InvoiceResp
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface InvoiceRepository : BaseRepository {

    suspend fun fetchInvoices(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<InvoiceResp>>
}