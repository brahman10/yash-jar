package com.jar.app.feature_transaction.shared.di

import com.jar.app.feature_transaction.shared.domain.use_case.FetchNewTransactionDetailsUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchNewTransactionDetailsUseCaseImpl
import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_transaction.shared.data.network.TransactionDataSource
import com.jar.app.feature_transaction.shared.data.repository.TransactionRepositoryImpl
import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.InvestWinningInGoldUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.impl.InvestWinningInGoldUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.FetchPaymentTransactionBreakupUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.FetchPostSetupTransactionDetailsUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchInvestedAmntBreakupUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTransactionFilterUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTransactionListingUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTxnDetailsUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserGoldDetailsUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserWinningBreakdownUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserWinningDetailsUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchWinningListingUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.PostTransactionActionUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchInvestedAmntBreakupUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchPaymentTransactionBreakupUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchPostSetupTransactionDetailsUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchTransactionFilterUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchTransactionListingUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchTxnDetailsUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchUserGoldDetailsUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchUserWinningDetailsUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.FetchWinningListingUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.IFetchUserWinningBreakdownUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.PostTransactionActionUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class TransactionModule {

    @Provides
    @Singleton
    internal fun provideTransactionDataSource(@AppHttpClient client: HttpClient): TransactionDataSource {
        return TransactionDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideNewTransactionRepository(transactionDataSource: TransactionDataSource): TransactionRepository {
        return TransactionRepositoryImpl(transactionDataSource)
    }

    @Provides
    @Singleton
    internal fun provideUserGoldDetailsUseCase(transactionRepository: TransactionRepository): IFetchUserGoldDetailsUseCase {
        return FetchUserGoldDetailsUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideUserWinningDetailUseCase(transactionRepository: TransactionRepository): IFetchUserWinningDetailsUseCase {
        return FetchUserWinningDetailsUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideInvestedAmountBreakdownUseCase(transactionRepository: TransactionRepository): IFetchInvestedAmntBreakupUseCase {
        return FetchInvestedAmntBreakupUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideTransactionFilterUseCase(transactionRepository: TransactionRepository): IFetchTransactionFilterUseCase {
        return FetchTransactionFilterUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideTransactionListingUseCase(transactionRepository: TransactionRepository): IFetchTransactionListingUseCase {
        return FetchTransactionListingUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideWinningListingUseCase(transactionRepository: TransactionRepository): IFetchWinningListingUseCase {
        return FetchWinningListingUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideTxnDetailsUseCase(transactionRepository: TransactionRepository): IFetchTxnDetailsUseCase {
        return FetchTxnDetailsUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideInvestWinningInGoldUseCase(transactionRepository: TransactionRepository): InvestWinningInGoldUseCase {
        return InvestWinningInGoldUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPaymentTransactionBreakupUseCase(transactionRepository: TransactionRepository): FetchPaymentTransactionBreakupUseCase {
        return FetchPaymentTransactionBreakupUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun providePostTransactionActionUseCase(transactionRepository: TransactionRepository): PostTransactionActionUseCase {
        return PostTransactionActionUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideIFetchUserWinningBreakdownUseCase(transactionRepository: TransactionRepository): IFetchUserWinningBreakdownUseCase {
        return IFetchUserWinningBreakdownUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPostSetupTransactionDetailsUseCase(transactionRepository: TransactionRepository): FetchPostSetupTransactionDetailsUseCase {
        return FetchPostSetupTransactionDetailsUseCaseImpl(transactionRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchNewTransactionDetailsUseCase(transactionRepository: TransactionRepository): FetchNewTransactionDetailsUseCase {
        return FetchNewTransactionDetailsUseCaseImpl(transactionRepository)
    }
}