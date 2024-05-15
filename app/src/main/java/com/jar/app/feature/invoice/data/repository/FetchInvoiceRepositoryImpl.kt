package com.jar.app.feature.invoice.data.repository

import com.jar.app.feature.home.data.network.HomeDataSource
import com.jar.app.feature.invoice.domain.repository.InvoiceRepository

internal class FetchInvoiceRepositoryImpl constructor(private val homeDataSource: HomeDataSource) :
    InvoiceRepository {

    override suspend fun fetchInvoices(
        page: Int,
        size: Int
    ) = homeDataSource.fetchInvoice(page, size)

}