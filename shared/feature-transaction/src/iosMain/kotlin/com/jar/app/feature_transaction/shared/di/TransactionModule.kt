package com.jar.app.feature_transaction.shared.di

import com.jar.app.feature_transaction.shared.data.network.TransactionDataSource
import com.jar.app.feature_transaction.shared.data.repository.TransactionRepositoryImpl
import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
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
import com.jar.app.feature_transaction.shared.domain.use_case.InvestWinningInGoldUseCase
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
import com.jar.app.feature_transaction.shared.domain.use_case.impl.InvestWinningInGoldUseCaseImpl
import com.jar.app.feature_transaction.shared.domain.use_case.impl.PostTransactionActionUseCaseImpl
import io.ktor.client.HttpClient

class TransactionModule(
    client: HttpClient
) {

    private val transactionDataSource by lazy {
        TransactionDataSource(client)
    }

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(transactionDataSource)
    }

    val fetchUserGoldDetailsUseCase: IFetchUserGoldDetailsUseCase by lazy {
        FetchUserGoldDetailsUseCaseImpl(transactionRepository)
    }

    val fetchUserWinningDetailUseCase: IFetchUserWinningDetailsUseCase by lazy {
        FetchUserWinningDetailsUseCaseImpl(transactionRepository)
    }

    val fetchInvestedAmountBreakdownUseCase: IFetchInvestedAmntBreakupUseCase by lazy {
        FetchInvestedAmntBreakupUseCaseImpl(transactionRepository)
    }

    val fetchTransactionFilterUseCase: IFetchTransactionFilterUseCase by lazy {
        FetchTransactionFilterUseCaseImpl(transactionRepository)
    }

    val fetchTransactionListingUseCase: IFetchTransactionListingUseCase by lazy {
        FetchTransactionListingUseCaseImpl(transactionRepository)
    }

    val fetchWinningListingUseCase: IFetchWinningListingUseCase by lazy {
        FetchWinningListingUseCaseImpl(transactionRepository)
    }

    val fetchTxnDetailsUseCase: IFetchTxnDetailsUseCase by lazy {
        FetchTxnDetailsUseCaseImpl(transactionRepository)
    }

    val fetchInvestWinningInGoldUseCase: InvestWinningInGoldUseCase by lazy {
        InvestWinningInGoldUseCaseImpl(transactionRepository)
    }

    val fetchPaymentTransactionBreakupUseCase: FetchPaymentTransactionBreakupUseCase by lazy {
        FetchPaymentTransactionBreakupUseCaseImpl(transactionRepository)
    }

    val postTransactionActionUseCase: PostTransactionActionUseCase by lazy {
        PostTransactionActionUseCaseImpl(transactionRepository)
    }

    val fetchUserWinningBreakdownUseCase: IFetchUserWinningBreakdownUseCase by lazy {
        IFetchUserWinningBreakdownUseCaseImpl(transactionRepository)
    }

    val fetchPostSetupTransactionDetailsUseCase: FetchPostSetupTransactionDetailsUseCase by lazy {
        FetchPostSetupTransactionDetailsUseCaseImpl(transactionRepository)
    }
}