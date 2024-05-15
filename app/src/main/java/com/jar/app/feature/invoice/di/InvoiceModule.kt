package com.jar.app.feature.invoice.di

import com.jar.app.feature.home.data.network.HomeDataSource
import com.jar.app.feature.invoice.data.repository.FetchInvoiceRepositoryImpl
import com.jar.app.feature.invoice.domain.repository.InvoiceRepository
import com.jar.app.feature.invoice.domain.use_case.FetchInvoiceUseCase
import com.jar.app.feature.invoice.domain.use_case.impl.FetchInvoiceUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class InvoiceModule {

    companion object {
        @Provides
        @Singleton
        internal fun provideInvoiceRepository(homeDataSource: HomeDataSource): InvoiceRepository {
            return FetchInvoiceRepositoryImpl(homeDataSource)
        }

        @Provides
        @Singleton
        internal fun provideFetchInvoiceUseCase(invoiceRepository: InvoiceRepository): FetchInvoiceUseCase {
            return FetchInvoiceUseCaseImpl(invoiceRepository)
        }

    }

}