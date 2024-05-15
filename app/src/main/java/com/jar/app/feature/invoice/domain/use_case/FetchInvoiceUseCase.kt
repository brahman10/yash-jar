package com.jar.app.feature.invoice.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.invoice.domain.model.InvoiceResp

interface FetchInvoiceUseCase {

    suspend fun fetchInvoice(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<InvoiceResp>>

}