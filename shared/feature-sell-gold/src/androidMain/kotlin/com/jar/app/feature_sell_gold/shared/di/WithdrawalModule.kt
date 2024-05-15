package com.jar.app.feature_sell_gold.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_sell_gold.shared.data.network.WithdrawalDataSource
import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchDrawerDetailsUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchKycDetailsForSellGoldUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchGoldSellOptionUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchSellGoldStaticContentUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalBottomSheetDataUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalStatusUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostTransactionActionUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostWithdrawRequestUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IUpdateWithdrawalReasonUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchDrawerDetailsUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchGoldSellOptionUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchKycDetailsForSellGoldUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchSellGoldStaticContentUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchWithdrawalBottomSheetDataImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchWithdrawalStatusUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.PostTransactionActionUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.PostWithdrawRequestUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.UpdateWithdrawalReasonUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton
import com.jar.app.feature_sell_gold.shared.data.repository.WithdrawalRepositoryImpl as WithdrawlRepositoryImpl1

@Module
@InstallIn(SingletonComponent::class)
internal class WithdrawalModule {

    @Provides
    @Singleton
    internal fun provideWithdrawalDataSource(@AppHttpClient client: HttpClient): WithdrawalDataSource {
        return WithdrawalDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideWithdrawalRepository(withdrawalDataSource: WithdrawalDataSource): IWithdrawalRepository {
        return WithdrawlRepositoryImpl1(withdrawalDataSource)
    }

    @Provides
    @Singleton
    internal fun providePostWithdrawalRequestUseCase(repository: IWithdrawalRepository): IPostWithdrawRequestUseCase {
        return PostWithdrawRequestUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldSellOptionsUseCase(repository: IWithdrawalRepository): IFetchGoldSellOptionUseCase {
        return FetchGoldSellOptionUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    internal fun provideStaticDataUseCase(repository: IWithdrawalRepository): IFetchSellGoldStaticContentUseCase {
        return FetchSellGoldStaticContentUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    internal fun provideIFetchWithdrawalStatusUseCase(repository: IWithdrawalRepository): IFetchWithdrawalStatusUseCase {
        return FetchWithdrawalStatusUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    internal fun provideIUpdateWithdrawalReasonUseCase(repository: IWithdrawalRepository): IUpdateWithdrawalReasonUseCase {
        return UpdateWithdrawalReasonUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    internal fun providePostTransactionActionUseCase(repository: IWithdrawalRepository): IPostTransactionActionUseCase {
        return PostTransactionActionUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    internal fun provideIFetchWithdrawalBottomSheetDataUseCase(repository: IWithdrawalRepository): IFetchWithdrawalBottomSheetDataUseCase {
        return FetchWithdrawalBottomSheetDataImpl(repository)
    }

    @Provides
    @Singleton
    internal fun provideFetchDrawerDetailsUseCase(repository: IWithdrawalRepository): FetchDrawerDetailsUseCase {
        return FetchDrawerDetailsUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    internal fun provideFetchKycDetailsUseCase(repository: IWithdrawalRepository): FetchKycDetailsForSellGoldUseCase {
        return FetchKycDetailsForSellGoldUseCaseImpl(repository)
    }
}