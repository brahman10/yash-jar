package com.jar.app.feature_vasooli.impl.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_vasooli.impl.data.network.VasooliDataSource
import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.repository.VasooliRepositoryImpl
import com.jar.app.feature_vasooli.impl.domain.use_case.*
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchLoansListUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchRepaymentHistoryUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchVasooliOverviewUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.PostRepaymentEntryRequestUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.PostVasooliRequestUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.impl.*
import com.jar.app.feature_vasooli.impl.domain.use_case.impl.FetchLoansListUseCaseImpl
import com.jar.app.feature_vasooli.impl.domain.use_case.impl.FetchRepaymentHistoryUseCaseImpl
import com.jar.app.feature_vasooli.impl.domain.use_case.impl.FetchVasooliOverviewUseCaseImpl
import com.jar.app.feature_vasooli.impl.domain.use_case.impl.PostRepaymentEntryRequestUseCaseImpl
import com.jar.app.feature_vasooli.impl.domain.use_case.impl.PostVasooliRequestUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class VasooliModule {

    @Provides
    @Singleton
    internal fun provideVasooliDataSource(@AppHttpClient client: HttpClient): VasooliDataSource {
        return VasooliDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideVasooliRepository(vasooliDataSource: VasooliDataSource): VasooliRepository {
        return VasooliRepositoryImpl(vasooliDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchVasooliOverviewUseCase(vasooliRepository: VasooliRepository): FetchVasooliOverviewUseCase {
        return FetchVasooliOverviewUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchLoansListUseCase(vasooliRepository: VasooliRepository): FetchLoansListUseCase {
        return FetchLoansListUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun providePostVasooliRequestUseCase(vasooliRepository: VasooliRepository): PostVasooliRequestUseCase {
        return PostVasooliRequestUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchRepaymentHistoryUseCase(vasooliRepository: VasooliRepository): FetchRepaymentHistoryUseCase {
        return FetchRepaymentHistoryUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun providePostRepaymentEntryRequestUseCase(vasooliRepository: VasooliRepository): PostRepaymentEntryRequestUseCase {
        return PostRepaymentEntryRequestUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateVasooliStatusUseCase(vasooliRepository: VasooliRepository): UpdateVasooliStatusUseCase {
        return UpdateVasooliStatusUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun provideDeleteVasooliEntryUseCase(vasooliRepository: VasooliRepository): DeleteVasooliEntryUseCase {
        return DeleteVasooliEntryUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchLoanDetailsUseCase(vasooliRepository: VasooliRepository): FetchLoanDetailsUseCase {
        return FetchLoanDetailsUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateVasooliEntryUseCase(vasooliRepository: VasooliRepository): UpdateVasooliEntryUseCase {
        return UpdateVasooliEntryUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchReminderUseCase(vasooliRepository: VasooliRepository): FetchReminderUseCase {
        return FetchReminderUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchNewImageUseCase(vasooliRepository: VasooliRepository): FetchNewImageUseCase {
        return FetchNewImageUseCaseImpl(vasooliRepository)
    }

    @Provides
    @Singleton
    internal fun providePostSendReminderUseCase(vasooliRepository: VasooliRepository): PostSendReminderUseCase {
        return PostSendReminderUseCaseImpl(vasooliRepository)
    }
}