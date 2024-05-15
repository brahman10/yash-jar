package com.jar.app.feature.invoice.domain.use_case.impl

import com.jar.app.feature.invoice.domain.repository.InvoiceRepository
import com.jar.app.feature.invoice.domain.use_case.FetchInvoiceUseCase

internal class FetchInvoiceUseCaseImpl constructor(private val invoiceRepository: InvoiceRepository) :
    FetchInvoiceUseCase {

    override suspend fun fetchInvoice(
        page: Int,
        size: Int
    ) = invoiceRepository.fetchInvoices(page, size)
}